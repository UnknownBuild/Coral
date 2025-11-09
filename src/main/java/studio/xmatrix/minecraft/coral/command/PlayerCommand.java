package studio.xmatrix.minecraft.coral.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.PlayerConfigEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;
import org.jetbrains.annotations.NotNull;
import studio.xmatrix.minecraft.coral.config.Config;
import studio.xmatrix.minecraft.coral.config.Language;
import studio.xmatrix.minecraft.coral.mixin.command.player.MinecraftServerAccessor;
import studio.xmatrix.minecraft.coral.store.CoralPlayerState;
import studio.xmatrix.minecraft.coral.util.FileUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * player 命令提供了玩家查询和转移的扩展
 */
public class PlayerCommand {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!Config.getBoolean("command.player")) {
            return;
        }

        // 注册命令
        dispatcher.register(CommandManager.literal("player")
                .then(CommandManager.literal("list").executes(c -> executeList(c.getSource())))
                .then(CommandManager.literal("listall").requires(s -> s.hasPermissionLevel(3)).executes(c -> executeListAll(c.getSource()))));
    }

    /**
     * 展示当前在线玩家列表
     */
    private static int executeList(ServerCommandSource source) {
        // 输出当前在线玩家列表
        var players = source.getServer().getPlayerManager().getPlayerList();
        var playerText = Texts.join(players, Text.literal("\n"), p ->
                createPlayerText(new PlayerData(p.getUuid(), p.getGameProfile().name(), true, 0, null), false));
        if (!players.isEmpty()) {
            playerText = Text.literal(":\n").append(playerText);
        }
        var text = Language.formatStyle("coral.command.player.list", players.size(), playerText);
        source.sendFeedback(() -> text, false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 展示所有玩家列表
     */
    private static int executeListAll(ServerCommandSource source) {
        var players = new ArrayList<PlayerData>();
        var playerMap = new HashSet<UUID>();
        var opList = source.getServer().getPlayerManager().getOpList();

        // 获取服务器在线玩家列表
        for (var p : source.getServer().getPlayerManager().getPlayerList()) {
            var op = opList.get(p.getPlayerConfigEntry());
            var opLevel = op != null ? op.getPermissionLevel() : 0;
            players.add(new PlayerData(p.getUuid(), p.getGameProfile().name(), true, opLevel, null));
            playerMap.add(p.getUuid());
        }

        // 获取本地存储的离线玩家列表
        var playerDataDir = ((MinecraftServerAccessor) source.getServer()).getSession().getDirectory(WorldSavePath.PLAYERDATA).toFile();
        var coralPlayers = CoralPlayerState.fromServer(source.getServer()).getPlayers();
        for (var file : Objects.requireNonNull(playerDataDir.listFiles(File::isFile))) {
            // 获取用户 uuid
            UUID uuid;
            try {
                uuid = UUID.fromString(FileUtil.getPrefixName(file));
            } catch (IllegalArgumentException e) {
                continue;
            }
            if (playerMap.contains(uuid)) {
                continue;
            }
            playerMap.add(uuid);

            // 获取用户信息
            var op = opList.get(new PlayerConfigEntry(uuid, ""));
            var opLevel = op != null ? op.getPermissionLevel() : 0;
            var coralPlayer = coralPlayers.get(uuid);
            if (coralPlayer != null) {
                players.add(new PlayerData(uuid, coralPlayer.getName(), false, opLevel, coralPlayer.getPlayTime()));
            } else {
                players.add(new PlayerData(uuid, null, false, opLevel, null));
            }
        }
        Collections.sort(players);

        // 输出信息到聊天框
        var playerText = Texts.join(players, Text.literal("\n"), p -> createPlayerText(p, true));
        if (!players.isEmpty()) {
            playerText = Text.literal(":\n").append(playerText);
        }
        var text = Language.formatStyle("coral.command.player.listall", players.size(), playerText);
        source.sendFeedback(() -> text, false);
        return Command.SINGLE_SUCCESS;
    }

    private static Text createPlayerText(PlayerData player, boolean showStatus) {
        // 默认展示玩家名称, 如果没有名称则展示 uuid
        MutableText nameText;
        String copyString;
        if (player.name != null && !player.name.isEmpty()) {
            nameText = Text.literal(player.name);
            copyString = String.format("%s [%s]", player.name, player.uuid);
        } else {
            nameText = Texts.bracketed(Text.literal(player.uuid.toString())).formatted(Formatting.GRAY);
            copyString = String.format("[%s]", player.uuid);
        }
        nameText.styled(style -> style.withClickEvent(new ClickEvent.CopyToClipboard(copyString))
                .withHoverEvent(new HoverEvent.ShowText(Text.translatable("chat.copy.click"))));

        // 添加在线状态和权限
        MutableText statusText = null;
        if (showStatus) {
            if (player.online) {
                statusText = Language.format("coral.command.player.listall.online");
            } else {
                statusText = Language.format("coral.command.player.listall.offline");
                if (player.playTime != null) {
                    statusText.append(Texts.DEFAULT_SEPARATOR)
                            .append(Language.formatStyle("coral.command.player.listall.playtime", dateFormat.format(player.playTime)));
                }
            }
            if (player.permissionLevel > 0) {
                statusText.append(Texts.DEFAULT_SEPARATOR)
                        .append(Language.formatStyle("coral.command.player.listall.operator", player.permissionLevel));
                statusText.formatted(Formatting.LIGHT_PURPLE);
            }
        }

        MutableText text = Text.empty();
        text.append(player.online ? nameText : nameText.formatted(Formatting.GRAY));
        if (showStatus) {
            text.append(" ").append(player.online ? Texts.bracketed(statusText) :
                    Texts.bracketed(statusText.formatted(Formatting.GRAY)).formatted(Formatting.GRAY));
        }
        return text;
    }

    // 玩家数据
    private record PlayerData(UUID uuid, String name, boolean online, int permissionLevel,
                              Date playTime) implements Comparable<PlayerData> {
        @Override
        public int compareTo(@NotNull PlayerData obj) {
            // 在线用户优先
            if (online != obj.online) {
                return online ? -1 : 1;
            }
            // 权限高的优先
            if (permissionLevel != obj.permissionLevel) {
                return -(permissionLevel - obj.permissionLevel);
            }
            // 游玩时间最新的优先, 如果不存在游玩时间记录则排在最后
            if (playTime != null && obj.playTime != null) {
                return -playTime.compareTo(obj.playTime);
            } else if (playTime == null && obj.playTime == null) {
                return 0;
            }
            return playTime != null ? -1 : 1;
        }
    }
}

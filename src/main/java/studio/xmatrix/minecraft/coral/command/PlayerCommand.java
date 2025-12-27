package studio.xmatrix.minecraft.coral.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraft.server.players.NameAndId;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.NotNull;
import studio.xmatrix.minecraft.coral.config.Config;
import studio.xmatrix.minecraft.coral.config.Language;
import studio.xmatrix.minecraft.coral.mixin.command.player.MinecraftServerAccessor;
import studio.xmatrix.minecraft.coral.store.CoralPlayerSaveData;
import studio.xmatrix.minecraft.coral.util.FileUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * player 命令提供了玩家查询和转移的扩展
 */
public class PlayerCommand {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (!Config.getBoolean("command.player")) {
            return;
        }

        // 注册命令
        dispatcher.register(Commands.literal("player")
                .then(Commands.literal("list").executes(c -> executeList(c.getSource())))
                .then(Commands.literal("listall")
                        .requires(Commands.hasPermission(Commands.LEVEL_ADMINS))
                        .executes(c -> executeListAll(c.getSource()))));
    }

    /**
     * 展示当前在线玩家列表
     */
    private static int executeList(CommandSourceStack source) {
        // 输出当前在线玩家列表
        var players = source.getServer().getPlayerList().getPlayers();
        var playerText = ComponentUtils.formatList(players, Component.literal("\n"), p ->
                createPlayerText(new PlayerData(p.getUUID(), p.getGameProfile().name(), true, 0, null), false));
        if (!players.isEmpty()) {
            playerText = Component.literal(":\n").append(playerText);
        }
        var text = Language.formatStyle("coral.command.player.list", players.size(), playerText);
        source.sendSuccess(() -> text, false);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * 展示所有玩家列表
     */
    private static int executeListAll(CommandSourceStack source) {
        var players = new ArrayList<PlayerData>();
        var playerMap = new HashSet<UUID>();
        var opList = source.getServer().getPlayerList().getOps();

        // 获取服务器在线玩家列表
        for (var p : source.getServer().getPlayerList().getPlayers()) {
            var op = opList.get(p.nameAndId());
            var opLevel = op != null ? op.permissions().level().id() : 0;
            players.add(new PlayerData(p.getUUID(), p.getGameProfile().name(), true, opLevel, null));
            playerMap.add(p.getUUID());
        }

        // 获取本地存储的离线玩家列表
        var playerDataDir = ((MinecraftServerAccessor) source.getServer()).getStorageSource().getLevelPath(LevelResource.PLAYER_DATA_DIR).toFile();
        var coralPlayers = CoralPlayerSaveData.fromServer(source.getServer()).getPlayers();
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
            var op = opList.get(new NameAndId(uuid, ""));
            var opLevel = op != null ? op.permissions().level().id() : 0;
            var coralPlayer = coralPlayers.get(uuid);
            if (coralPlayer != null) {
                players.add(new PlayerData(uuid, coralPlayer.getName(), false, opLevel, coralPlayer.getPlayTime()));
            } else {
                players.add(new PlayerData(uuid, null, false, opLevel, null));
            }
        }
        Collections.sort(players);

        // 输出信息到聊天框
        var playerText = ComponentUtils.formatList(players, Component.literal("\n"), p -> createPlayerText(p, true));
        if (!players.isEmpty()) {
            playerText = Component.literal(":\n").append(playerText);
        }
        var text = Language.formatStyle("coral.command.player.listall", players.size(), playerText);
        source.sendSuccess(() -> text, false);
        return Command.SINGLE_SUCCESS;
    }

    private static Component createPlayerText(PlayerData player, boolean showStatus) {
        // 默认展示玩家名称, 如果没有名称则展示 uuid
        MutableComponent nameText;
        String copyString;
        if (player.name != null && !player.name.isEmpty()) {
            nameText = Component.literal(player.name);
            copyString = String.format("%s [%s]", player.name, player.uuid);
        } else {
            nameText = ComponentUtils.wrapInSquareBrackets(Component.literal(player.uuid.toString())).withStyle(ChatFormatting.GRAY);
            copyString = String.format("[%s]", player.uuid);
        }
        nameText.withStyle(style -> style.withClickEvent(new ClickEvent.CopyToClipboard(copyString))
                .withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.copy.click"))));

        // 添加在线状态和权限
        MutableComponent statusText = null;
        if (showStatus) {
            if (player.online) {
                statusText = Language.format("coral.command.player.listall.online");
            } else {
                statusText = Language.format("coral.command.player.listall.offline");
                if (player.playTime != null) {
                    statusText.append(ComponentUtils.DEFAULT_SEPARATOR)
                            .append(Language.formatStyle("coral.command.player.listall.playtime", dateFormat.format(player.playTime)));
                }
            }
            if (player.permissionLevel > 0) {
                statusText.append(ComponentUtils.DEFAULT_SEPARATOR)
                        .append(Language.formatStyle("coral.command.player.listall.operator", player.permissionLevel));
                statusText.withStyle(ChatFormatting.LIGHT_PURPLE);
            }
        }

        MutableComponent text = Component.empty();
        text.append(player.online ? nameText : nameText.withStyle(ChatFormatting.GRAY));
        if (showStatus) {
            text.append(" ").append(player.online ? ComponentUtils.wrapInSquareBrackets(statusText) :
                    ComponentUtils.wrapInSquareBrackets(statusText.withStyle(ChatFormatting.GRAY)).withStyle(ChatFormatting.GRAY));
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

package studio.xmatrix.minecraft.coral.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;
import org.apache.commons.lang3.tuple.Triple;
import studio.xmatrix.minecraft.coral.config.Config;
import studio.xmatrix.minecraft.coral.config.Language;
import studio.xmatrix.minecraft.coral.mixin.command.player.MinecraftServerAccessor;
import studio.xmatrix.minecraft.coral.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

/**
 * player 命令提供了玩家查询和转移的扩展
 */
public class PlayerCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!Config.getBoolean("command.player")) {
            return;
        }

        // 注册命令
        dispatcher.register(CommandManager.literal("player")
                .then(CommandManager.literal("list").executes(c -> executeList(c.getSource())))
                .then(CommandManager.literal("listall").requires(s -> s.hasPermissionLevel(3)).executes(c -> executeListAll(c.getSource()))));
    }

    private static int executeList(ServerCommandSource source) {
        // 输出当前在线玩家列表
        var players = source.getServer().getPlayerManager().getPlayerList();
        var playerText = Texts.join(players, Text.literal("\n"), p -> createPlayerText(p.getUuidAsString(), p.getGameProfile().getName(), 0));
        var text = Language.formatStyle("coral.command.player.list", players.size(), playerText);
        source.sendFeedback(() -> text, false);
        return Command.SINGLE_SUCCESS;
    }

    private static int executeListAll(ServerCommandSource source) {
        var players = new ArrayList<Triple<String, String, Integer>>(); // uuid, name, level
        var playerMap = new HashSet<String>();
        var opList = source.getServer().getPlayerManager().getOpList();

        // 获取服务器在线玩家列表
        for (var p : source.getServer().getPlayerManager().getPlayerList()) {
            var op = opList.get(p.getGameProfile());
            var opLevel = op != null ? op.getPermissionLevel() : 0;
            players.add(Triple.of(p.getUuidAsString(), p.getGameProfile().getName(), opLevel));
            playerMap.add(p.getUuidAsString());
        }

        // 获取服务器玩家数据文件夹下的玩家列表
        var playerDataDir = ((MinecraftServerAccessor) source.getServer()).getSession().getDirectory(WorldSavePath.PLAYERDATA).toFile();
        var userCache = Objects.requireNonNull(source.getServer().getUserCache());
        for (var file : Objects.requireNonNull(playerDataDir.listFiles(File::isFile))) {
            // 获取用户 uuid
            var uuidStr = FileUtil.getPrefixName(file);
            if (uuidStr.length() != 36 || playerMap.contains(uuidStr)) {
                continue;
            }
            playerMap.add(uuidStr);
            var uuid = UUID.fromString(uuidStr);
            // 获取用户名称和判断是否为管理员
            String playerName = "";
            int opLevel = 0;
            var gameProfile = userCache.getByUuid(uuid);
            if (gameProfile.isPresent()) {
                playerName = gameProfile.get().getName();
                var op = opList.get(gameProfile.get());
                opLevel = op != null ? op.getPermissionLevel() : 0;
            }
            players.add(Triple.ofNonNull(uuidStr, playerName, opLevel));
        }
        players.sort((p1, p2) -> p1.getRight() > p2.getRight() ? -1 :
                (p1.getRight().equals(p2.getRight()) ? p1.getLeft().compareTo(p2.getLeft()) : 1));

        // 输出信息到聊天框
        var playerText = Texts.join(players, Text.literal("\n"), p -> createPlayerText(p.getLeft(), p.getMiddle(), p.getRight()));
        var text = Language.formatStyle("coral.command.player.listall", players.size(), playerText);
        source.sendFeedback(() -> text, false);
        return Command.SINGLE_SUCCESS;
    }

    private static Text createPlayerText(String uuidStr, String name, int permissionLevel) {
        var text = Text.empty();
        if (name != null && !name.isEmpty()) {
            text.append(name + " ");
        }
        if (permissionLevel > 0) {
            text.append(Text.literal(String.format("(op:%d) ", permissionLevel)).formatted(Formatting.LIGHT_PURPLE));
        }
        text.append(Texts.bracketed(Text.literal(uuidStr)).formatted(Formatting.GRAY)
                .styled(style -> style.withClickEvent(new ClickEvent.CopyToClipboard(uuidStr))
                        .withHoverEvent(new HoverEvent.ShowText(Text.translatable("chat.copy.click")))));
        return text;
    }
}

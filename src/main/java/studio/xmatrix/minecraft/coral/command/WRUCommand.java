package studio.xmatrix.minecraft.coral.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import studio.xmatrix.minecraft.coral.config.Config;
import studio.xmatrix.minecraft.coral.config.Language;

/**
 * wru 命令用于询问指定玩家的位置
 */
public class WRUCommand {
    private static final SimpleCommandExceptionType ERR_SELF = new SimpleCommandExceptionType(Language.format("coral.command.wru.err_self"));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!Config.getBoolean("command.wru")) {
            return;
        }

        // 注册命令, 要求指定一个玩家, 要求当前服务器为多人服务器才生效
        dispatcher.register(CommandManager.literal("wru")
                .requires(s -> s.getServer() != null && s.getServer().isRemote())
                .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(c -> executeWRU(c.getSource(), EntityArgumentType.getPlayer(c, "player")))));
    }

    private static int executeWRU(ServerCommandSource source, ServerPlayerEntity entity) throws CommandSyntaxException {
        // 获取玩家信息, 禁止自己询问自己
        var player = source.getPlayerOrThrow();
        if (player.equals(entity)) {
            throw ERR_SELF.create();
        }

        // 发送消息询问位置
        MutableText text = Language.formatStyle("coral.command.wru", player.getDisplayName(), entity.getDisplayName());
        source.getServer().getPlayerManager().broadcast(text, false);
        return Command.SINGLE_SUCCESS;
    }
}

package studio.xmatrix.minecraft.coral.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import studio.xmatrix.minecraft.coral.config.Config;
import studio.xmatrix.minecraft.coral.config.Language;

/**
 * wru 命令用于询问指定玩家的位置
 */
public class WRUCommand {
    private static final SimpleCommandExceptionType ERR_SELF = new SimpleCommandExceptionType(Language.format("coral.command.wru.err_self"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (!Config.getBoolean("command.wru")) {
            return;
        }

        // 注册命令, 要求指定一个玩家, 要求当前服务器为多人服务器才生效
        dispatcher.register(Commands.literal("wru")
                .requires(s -> s.getServer() != null && s.getServer().isPublished())
                .then(Commands.argument("player", EntityArgument.player())
                        .executes(c -> executeWRU(c.getSource(), EntityArgument.getPlayer(c, "player")))));
    }

    private static int executeWRU(CommandSourceStack source, ServerPlayer entity) throws CommandSyntaxException {
        // 获取玩家信息, 禁止自己询问自己
        var player = source.getPlayerOrException();
        if (player.equals(entity)) {
            throw ERR_SELF.create();
        }

        // 发送消息询问位置
        MutableComponent text = Language.formatStyle("coral.command.wru", player.getDisplayName(), entity.getDisplayName());
        source.getServer().getPlayerList().broadcastSystemMessage(text, false);
        return Command.SINGLE_SUCCESS;
    }
}

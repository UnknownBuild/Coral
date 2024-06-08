package studio.xmatrix.minecraft.coral.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.MutableText;
import studio.xmatrix.minecraft.coral.config.Config;
import studio.xmatrix.minecraft.coral.config.Language;
import studio.xmatrix.minecraft.coral.consts.Dimension;

/**
 * here 命令可以给执行命令的玩家赋予一个高亮的特效, 用于向其他玩家广播和提示自己的位置.
 */
public class HereCommand {
    private static int duration; // 高亮特效的持续时间, 单位秒

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!Config.getBoolean("command.here")) {
            return;
        }

        // 注册命令, 要求当前服务器为多人服务器才生效
        duration = Config.getInt("command.here.duration");
        dispatcher.register(CommandManager.literal("here")
                .requires(s -> s.getServer().isRemote())
                .executes(c -> executeHere(c.getSource())));
    }

    private static int executeHere(ServerCommandSource source) throws CommandSyntaxException {
        // 给执行命令的玩家赋予高亮特效
        var player = source.getPlayerOrThrow();
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, duration * 20));

        // 广播信息到聊天框
        String coordinateText = String.format("[%d, %d, %d]", player.getBlockX(), player.getBlockY(), player.getBlockZ());
        MutableText text = Language.formatStyle("coral.command.here", player.getDisplayName(),
                Dimension.getStyleText(player.getWorld().getRegistryKey()), coordinateText);
        source.getServer().getPlayerManager().broadcast(text, false);
        source.sendFeedback(() -> Language.formatStyle("coral.command.here.feedback", duration), false);

        return Command.SINGLE_SUCCESS;
    }
}

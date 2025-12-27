package studio.xmatrix.minecraft.coral.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import studio.xmatrix.minecraft.coral.config.Config;
import studio.xmatrix.minecraft.coral.config.Language;
import studio.xmatrix.minecraft.coral.consts.Dimension;

/**
 * here 命令可以给执行命令的玩家赋予一个高亮的特效, 用于向其他玩家广播和提示自己的位置.
 */
public class HereCommand {
    private static int duration; // 高亮特效的持续时间, 单位秒

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        if (!Config.getBoolean("command.here")) {
            return;
        }

        // 注册命令, 要求当前服务器为多人服务器才生效
        duration = Config.getInt("command.here.duration");
        dispatcher.register(Commands.literal("here")
                .requires(s -> s.getServer() != null && s.getServer().isPublished())
                .executes(c -> executeHere(c.getSource())));
    }

    private static int executeHere(CommandSourceStack source) throws CommandSyntaxException {
        // 给执行命令的玩家赋予高亮特效
        var player = source.getPlayerOrException();
        player.addEffect(new MobEffectInstance(MobEffects.GLOWING, duration * 20));

        // 广播信息到聊天框
        String coordinateText = String.format("[%d, %d, %d]", player.getBlockX(), player.getBlockY(), player.getBlockZ());
        MutableComponent text = Language.formatStyle("coral.command.here", player.getDisplayName(),
                Dimension.getStyleText(player.level().dimension()), coordinateText);
        source.getServer().getPlayerList().broadcastSystemMessage(text, false);
        source.sendSuccess(() -> Language.formatStyle("coral.command.here.feedback", duration), false);

        return Command.SINGLE_SUCCESS;
    }
}

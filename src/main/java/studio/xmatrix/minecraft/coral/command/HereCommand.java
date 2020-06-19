package studio.xmatrix.minecraft.coral.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.world.dimension.DimensionType;
import studio.xmatrix.minecraft.coral.config.ConfigLoader;
import studio.xmatrix.minecraft.coral.util.TextUtil;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class HereCommand {

    private static final Map<DimensionType, MutableText> dimensionTexts = new HashMap<>();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!ConfigLoader.getConfig().getCommand().getHere().getEnabled()) {
            return;
        }

        dimensionTexts.put(DimensionType.OVERWORLD, TextUtil.byKeyAndStyle("env.dimension.overWorld", "msg.iAmHere.$2.overWorld"));
        dimensionTexts.put(DimensionType.THE_NETHER, TextUtil.byKeyAndStyle("env.dimension.nether", "msg.iAmHere.$2.nether"));
        dimensionTexts.put(DimensionType.THE_END, TextUtil.byKeyAndStyle("env.dimension.end", "msg.iAmHere.$2.end"));

        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("here")
                .executes(hereBuilder());
        dispatcher.register(literalArgumentBuilder);
    }

    private static Command<ServerCommandSource> hereBuilder() {
        return c -> {
            ServerCommandSource source = c.getSource();
            MinecraftServer minecraftServer = source.getMinecraftServer();
            ServerPlayerEntity player = source.getPlayer();
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 30 * 20));

            MutableText coordinateText = new LiteralText(String.format("[x%d, y:%d, z:%d]", player.getBlockPos().getX(), player.getBlockPos().getY(), player.getBlockPos().getZ()))
                    .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                            String.format("/tp @s %d %d %d", player.getBlockPos().getX(), player.getBlockPos().getY(), player.getBlockPos().getZ())))
                            .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslatableText("chat.coordinates.tooltip"))));
            MutableText text = TextUtil.byKey("msg.iAmHere", player.getDisplayName(), dimensionTexts.get(player.dimension), coordinateText);
            minecraftServer.getPlayerManager().sendToAll(text);
            source.sendFeedback(TextUtil.byKey("feedback.playerGlowing", 30), false);
            return Command.SINGLE_SUCCESS;
        };
    }
}

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
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import studio.xmatrix.minecraft.coral.config.ConfigLoader;
import studio.xmatrix.minecraft.coral.util.TextUtil;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.server.command.CommandManager.literal;

public class HereCommand {

    private static final Map<RegistryKey<World>, MutableText> dimensionTexts = new HashMap<>();

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!ConfigLoader.getConfig().getCommandHere()) {
            return;
        }

        dimensionTexts.put(World.OVERWORLD, TextUtil.byKeyAndStyle("env.dimension.overWorld", "msg.iAmHere.$2.overWorld"));
        dimensionTexts.put(World.NETHER, TextUtil.byKeyAndStyle("env.dimension.nether", "msg.iAmHere.$2.nether"));
        dimensionTexts.put(World.END, TextUtil.byKeyAndStyle("env.dimension.end", "msg.iAmHere.$2.end"));

        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("here")
                .executes(hereBuilder());
        dispatcher.register(literalArgumentBuilder);
    }

    private static Command<ServerCommandSource> hereBuilder() {
        return c -> {
            ServerCommandSource source = c.getSource();
            MinecraftServer minecraftServer = source.getServer();
            ServerPlayerEntity player = source.getPlayer();
            if (player == null) {
                return Command.SINGLE_SUCCESS;
            }
            int duration = ConfigLoader.getConfig().getCommandHereDuration();
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, duration * 20));

            MutableText coordinateText = Text.literal(String.format("[x%d, y:%d, z:%d]", player.getBlockPos().getX(), player.getBlockPos().getY(), player.getBlockPos().getZ()))
                    .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                            String.format("/execute in %s run tp @s %d %d %d", player.world.getRegistryKey().getValue().toString(),
                                    player.getBlockPos().getX(), player.getBlockPos().getY(), player.getBlockPos().getZ())))
                            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.coordinates.tooltip"))));
            MutableText text = TextUtil.byKey("msg.iAmHere", player.getDisplayName(), dimensionTexts.get(player.world.getRegistryKey()), coordinateText);
            minecraftServer.getPlayerManager().broadcast(text, false);
            source.sendFeedback(TextUtil.byKey("feedback.playerGlowing", duration), false);
            return Command.SINGLE_SUCCESS;
        };
    }
}

package studio.xmatrix.minecraft.coral.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.arguments.EntityArgumentType;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.Util;
import studio.xmatrix.minecraft.coral.config.ConfigLoader;
import studio.xmatrix.minecraft.coral.util.TextUtil;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WRUCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!ConfigLoader.getConfig().getCommand().getWru()) {
            return;
        }
        LiteralArgumentBuilder<ServerCommandSource> literalArgumentBuilder = literal("wru")
                .then(argument("player", EntityArgumentType.player())
                        .executes(askPlayerBuilder()));
        dispatcher.register(literalArgumentBuilder);
    }

    private static Command<ServerCommandSource> askPlayerBuilder() {
        return c -> {
            ServerCommandSource source = c.getSource();
            MinecraftServer minecraftServer = source.getMinecraftServer();
            ServerPlayerEntity player = source.getPlayer();
            ServerPlayerEntity argumentPlayer = EntityArgumentType.getPlayer(c, "player");

            MutableText text = TextUtil.byKey("msg.whereAreYou", player.getDisplayName(), argumentPlayer.getDisplayName());
            minecraftServer.getPlayerManager().broadcastChatMessage(text, MessageType.SYSTEM, Util.NIL_UUID);
            return Command.SINGLE_SUCCESS;
        };
    }
}

package studio.xmatrix.minecraft.coral.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import studio.xmatrix.minecraft.coral.config.ConfigLoader;
import studio.xmatrix.minecraft.coral.util.TextUtil;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class WRUCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!ConfigLoader.getConfig().getCommandWru()) {
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
            MinecraftServer minecraftServer = source.getServer();
            ServerPlayerEntity player = source.getPlayer();
            ServerPlayerEntity argumentPlayer = EntityArgumentType.getPlayer(c, "player");

            MutableText text = TextUtil.byKey("msg.whereAreYou", player.getDisplayName(), argumentPlayer.getDisplayName());
            minecraftServer.getPlayerManager().broadcast(text, false);
            return Command.SINGLE_SUCCESS;
        };
    }
}

package studio.xmatrix.minecraft.coral.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.ServerCommandSource;

public class CommandCallback {
    private static Event callback;

    public static void init(Event callback) {
        CommandCallback.callback = callback;
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        callback.apply(dispatcher);
    }

    public interface Event {
        void apply(CommandDispatcher<ServerCommandSource> dispatcher);
    }
}

package studio.xmatrix.minecraft.coral.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public class CommandCallback {
    private static Event callback;

    public static void init(Event callback) {
        CommandCallback.callback = callback;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        callback.apply(dispatcher);
    }

    public interface Event {
        void apply(CommandDispatcher<CommandSourceStack> dispatcher);
    }
}

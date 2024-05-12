package studio.xmatrix.minecraft.coral;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.apache.logging.log4j.Logger;
import studio.xmatrix.minecraft.coral.command.HereCommand;
import studio.xmatrix.minecraft.coral.command.WRUCommand;
import studio.xmatrix.minecraft.coral.config.ConfigLoader;
import studio.xmatrix.minecraft.coral.config.LangLoader;
import studio.xmatrix.minecraft.coral.config.StyleLoader;
import studio.xmatrix.minecraft.coral.util.LogUtil;

public class CoralServer implements DedicatedServerModInitializer {

    private static final Logger LOGGER = LogUtil.getLogger();

    @Override
    public void onInitializeServer() {
        // Load config at first
        ConfigLoader.init();
        LangLoader.init();
        StyleLoader.init();

        registerCommands();
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            HereCommand.register(dispatcher);
            WRUCommand.register(dispatcher);
        });
    }
}

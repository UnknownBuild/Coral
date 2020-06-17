package studio.xmatrix.minecraft.coral;

import net.fabricmc.api.DedicatedServerModInitializer;
import org.apache.logging.log4j.Logger;
import studio.xmatrix.minecraft.coral.config.Config;
import studio.xmatrix.minecraft.coral.config.ConfigLoader;
import studio.xmatrix.minecraft.coral.util.LogUtil;

public class CoralServer implements DedicatedServerModInitializer {

    private static final Logger LOGGER = LogUtil.getLogger();

    @Override
    public void onInitializeServer() {
        // Load config at first
        ConfigLoader.getConfig();
    }
}

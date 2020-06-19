package studio.xmatrix.minecraft.coral.config;

import org.apache.logging.log4j.Logger;
import studio.xmatrix.minecraft.coral.util.FileUtil;
import studio.xmatrix.minecraft.coral.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ConfigLoader {

    private static final Logger LOGGER = LogUtil.getLogger();

    private static final String DEFAULT_CONFIG_FILE_PATH = "assets/coral/default-config.yaml";
    private static final String[] CUSTOM_CONFIG_FILE_PATHS = new String[]{
            "configs/coral.yaml",
            "configs/coral.yml"
    };

    private static Config config;

    public static void init() {
        getConfig();
        LOGGER.info("ConfigLoader init finish");
        LOGGER.info("Config function.msgCallSleep: {}", config.getFunction().getMsgCallSleep());
        LOGGER.info("Config translation.customLangFile: {}", config.getTranslation().getCustomLangFile());
        LOGGER.info("Config translation.customStyleFile: {}", config.getTranslation().getCustomStyleFile());
        LOGGER.info("Config translation.region: {}", config.getTranslation().getRegion());
    }

    public static Config getConfig() {
        if (config == null) {
            config = loadConfig();
        }
        return config;
    }

    private static Config loadConfig() {
        // Load default config from resource, crash when load fail
        Config defaultConfig;
        try {
            defaultConfig = FileUtil.fromYamlResource(DEFAULT_CONFIG_FILE_PATH, Config.class);
        } catch (IOException e) {
            throw new RuntimeException("Load default config fail", e);
        }

        // Load custom config from local file system
        File file = getCustomFile();
        if (file == null) {
            LOGGER.warn("Config not found in files {}, now use default config", Arrays.toString(CUSTOM_CONFIG_FILE_PATHS));
            return defaultConfig;
        } else {
            try {
                return FileUtil.fromYaml(file, Config.class, defaultConfig);
            } catch (IOException e) {
                LOGGER.error("Load config fail, now use default config, file:{}, err:{}", file.getAbsolutePath(), e.getMessage());
                return defaultConfig;
            }
        }
    }

    private static File getCustomFile() {
        for (String path : CUSTOM_CONFIG_FILE_PATHS) {
            File file = new File(path);
            if (file.exists() && file.isFile()) {
                return file;
            }
        }
        return null;
    }
}

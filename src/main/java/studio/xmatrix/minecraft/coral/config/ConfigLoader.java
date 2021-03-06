package studio.xmatrix.minecraft.coral.config;

import org.apache.logging.log4j.Logger;
import studio.xmatrix.minecraft.coral.config.validate.IValidator;
import studio.xmatrix.minecraft.coral.config.validate.ValidatorException;
import studio.xmatrix.minecraft.coral.util.FileUtil;
import studio.xmatrix.minecraft.coral.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ConfigLoader {

    private static final Logger LOGGER = LogUtil.getLogger();

    private static final String DEFAULT_CONFIG_FILE_PATH = "assets/coral/default-config.json";
    private static final String[] CUSTOM_CONFIG_FILE_PATHS = new String[]{
            "configs/coral.json"
    };

    private static Config config;

    public static void init() {
        IValidator validator = getConfig();
        try {
            validator.validate();
        } catch (ValidatorException e) {
            LOGGER.fatal("ConfigLoader init failed", e);
            throw new RuntimeException(e);
        }
        LOGGER.info("ConfigLoader init finish");
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
            defaultConfig = FileUtil.fromJsonResource(DEFAULT_CONFIG_FILE_PATH, Config.class);
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
                return FileUtil.fromJson(file, Config.class, defaultConfig);
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

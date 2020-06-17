package studio.xmatrix.minecraft.coral.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.logging.log4j.Logger;
import studio.xmatrix.minecraft.coral.util.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class ConfigLoader {

    private static final Logger LOGGER = LogUtil.getLogger();

    private static final String DEFAULT_CONFIG_FILE_PATH = "assets/coral/default-config.yaml";
    private static final String[] CUSTOM_CONFIG_FILE_PATHS = new String[]{
            "configs/coral.yaml",
            "configs/coral.yml"
    };

    private static Config defaultConfig;
    private static Config config;

    public static Config getConfig() {
        if (config == null) {
            config = loadConfig();
        }
        return config;
    }

    private static Config loadConfig() {
        if (defaultConfig == null) {
            defaultConfig = loadDefaultConfig();
        }
        File file = getCustomFile();
        if (file == null) {
            LOGGER.warn("Config not found in files {}, now use default config", Arrays.toString(CUSTOM_CONFIG_FILE_PATHS));
            return defaultConfig;
        } else {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                ObjectReader reader = mapper.readerForUpdating(defaultConfig);
                return reader.readValue(inputStream, Config.class);
            } catch (IOException e) {
                LOGGER.error("Load config fail, now use default config, file:{}, err:{}", file.getAbsolutePath(), e.getMessage());
                return defaultConfig;
            }
        }
    }

    private static Config loadDefaultConfig() {
        ClassLoader classLoader = ConfigLoader.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(DEFAULT_CONFIG_FILE_PATH)) {
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            return mapper.readValue(inputStream, Config.class);
        } catch (Exception e) {
            LOGGER.error("Load default config fail, err:{}", e.getMessage());
            return null;
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

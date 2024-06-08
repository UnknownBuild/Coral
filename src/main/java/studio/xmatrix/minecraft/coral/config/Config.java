package studio.xmatrix.minecraft.coral.config;

import org.apache.logging.log4j.Logger;
import studio.xmatrix.minecraft.coral.util.FileUtil;
import studio.xmatrix.minecraft.coral.util.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

/**
 * Coral 配置加载和获取
 */
public class Config {
    private static final Logger LOGGER = LogUtil.getLogger();
    private static final String DEFAULT_CONFIG_FILE_PATH = "/assets/coral/coral-config-%s.properties";
    private static final String[] CUSTOM_CONFIG_FILE_PATHS = new String[]{
            "coral.properties",
            "config/coral.properties",
            "configs/coral.properties"
    };
    private static Properties globalProperties;
    private static RuntimeException initException;

    private Config() {
    }

    /**
     * 初始化 Coral 配置
     */
    public static void init() {
        if (initException != null) {
            throw initException;
        }

        // 检查用户自定义配置是否存在
        File customFile = null;
        for (var path : CUSTOM_CONFIG_FILE_PATHS) {
            var file = new File(path);
            if (file.exists() && file.isFile()) {
                if (customFile == null) {
                    customFile = file;
                } else {
                    throw new IllegalArgumentException(String.format("Duplicated config files of '%s' and '%s'", customFile.getPath(), file.getPath()));
                }
            }
        }
        // 加载自定义配置
        Properties customProperties = new Properties();
        if (customFile != null) {
            try (var fileStream = new FileInputStream(customFile)) {
                customProperties.load(fileStream);
            } catch (IOException e) {
                throw new IllegalArgumentException(String.format("Failed to read Coral custom config '%s'", customFile.getPath()), e);
            }
        }

        // 获取默认配置
        String defaultPath = String.format(DEFAULT_CONFIG_FILE_PATH, customProperties.getProperty("use", "default"));
        Properties defaultProperties = new Properties();
        try (var defaultStream = FileUtil.getResourceAsStream(defaultPath)) {
            if (defaultStream == null) {
                throw new IllegalStateException(String.format("Failed to read Coral default config: file %s not exist", defaultPath));
            }
            defaultProperties.load(defaultStream);
            defaultProperties.forEach((key, val) -> customProperties.computeIfAbsent(key, k -> val));
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read Coral default config", e);
        }

        // 处理和校验配置, 校验合法性
        try {
            validate(customProperties);
        } catch (InvalidPropertiesFormatException e) {
            throw new IllegalStateException("Failed to validate Coral config", e);
        }

        globalProperties = customProperties;
        if (customFile != null) {
            LOGGER.info("Init Coral config success, use custom config '{}'", customFile.getPath());
        } else {
            LOGGER.info("Init Coral config success, use default config");
        }
    }

    public static void setInitException(RuntimeException e) {
        initException = e;
    }

    public static boolean getBoolean(String key) {
        return (boolean) globalProperties.get(key);
    }

    public static int getInt(String key) {
        return (int) globalProperties.get(key);
    }

    public static String getString(String key) {
        return globalProperties.getProperty(key);
    }

    private static void validate(Properties properties) throws InvalidPropertiesFormatException {
        tryBoolean(properties, "command.here");
        tryInteger(properties, "command.here.duration");
        tryBoolean(properties, "command.wru");

        tryBoolean(properties, "feature.call_sleep");
        tryBoolean(properties, "feature.death_info");
    }

    private static void tryBoolean(Properties properties, String key) throws InvalidPropertiesFormatException {
        String value = properties.getProperty(key);
        if (value != null && !value.equals("true") && !value.equals("false")) {
            throw new InvalidPropertiesFormatException(String.format("Config %s should be true or false, but got %s", key, value));
        }
        boolean result = value != null && value.equals("true");
        properties.put(key, result);
    }

    private static void tryInteger(Properties properties, String key) throws InvalidPropertiesFormatException {
        String value = properties.getProperty(key);
        try {
            int result = Integer.parseInt(value);
            properties.put(key, result);
        } catch (NumberFormatException e) {
            throw new InvalidPropertiesFormatException(String.format("Config %s should be a number, but got %s", key, value));
        }
    }
}

package studio.xmatrix.minecraft.coral.config;

import com.google.gson.reflect.TypeToken;
import org.apache.logging.log4j.Logger;
import studio.xmatrix.minecraft.coral.util.FileUtil;
import studio.xmatrix.minecraft.coral.util.LogUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class LangLoader {

    private static final Logger LOGGER = LogUtil.getLogger();

    private static final String DEFAULT_LANG_FILE_PATH = "assets/coral/lang/%s.json";

    private static Map<String, String> lang;

    public static void init() {
        if (lang == null) {
            lang = loadConfig();
        }
        LOGGER.info("LangLoader init finish");
    }

    public static String getValue(String key) {
        if (lang == null) {
            lang = loadConfig();
        }
        return lang.getOrDefault(key, key);
    }

    private static Map<String, String> loadConfig() {
        String region = ConfigLoader.getConfig().getTranslationRegion();
        String customLangFileName = ConfigLoader.getConfig().getTranslationCustomLangFile();
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();

        // Load default config from resource, crash when load fail
        Map<String, String> defaultLang;
        try {
            defaultLang = FileUtil.fromJsonResource(String.format(DEFAULT_LANG_FILE_PATH, region), type);
        } catch (IOException e) {
            throw new RuntimeException("Load default lang config fail", e);
        }
        if (customLangFileName == null || customLangFileName.isEmpty()) {
            return defaultLang;
        }

        // Load custom config from local file system
        File file = new File(customLangFileName);
        Map<String, String> customLang;
        try {
            customLang = FileUtil.fromJson(file, type);
            customLang.forEach((k, v) -> {
                if (defaultLang.containsKey(k)) {
                    defaultLang.put(k, v);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Load custom lang config fail, now use default config, file:{}, err:{}", file.getAbsolutePath(), e.getMessage());
        }
        return defaultLang;
    }
}

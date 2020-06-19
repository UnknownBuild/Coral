package studio.xmatrix.minecraft.coral.config;

import com.fasterxml.jackson.core.type.TypeReference;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.Logger;
import studio.xmatrix.minecraft.coral.util.FileUtil;
import studio.xmatrix.minecraft.coral.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StyleLoader {

    private static final Logger LOGGER = LogUtil.getLogger();

    private static final String DEFAULT_STYLE_FILE_PATH = "assets/coral/style.json";

    private static Map<String, String> style;

    public static void init() {
        if (style == null) {
            style = loadConfig();
        }
        LOGGER.info("StyleLoader init finish");
    }

    public static Formatting getValue(String key) {
        if (style == null) {
            style = loadConfig();
        }
        return Formatting.byName(style.get(key));
    }

    private static Map<String, String> loadConfig() {
        String customStyleFileName = ConfigLoader.getConfig().getTranslation().getCustomStyleFile();
        TypeReference<HashMap<String, String>> typeRef = new TypeReference<HashMap<String, String>>() {
        };

        // Load default config from resource, crash when load fail
        Map<String, String> defaultStyle;
        try {
            defaultStyle = FileUtil.fromJsonResource(DEFAULT_STYLE_FILE_PATH, typeRef);
        } catch (IOException e) {
            throw new RuntimeException("Load default style config fail", e);
        }
        if (customStyleFileName == null || customStyleFileName.isEmpty()) {
            return defaultStyle;
        }

        // Load custom config from local file system
        File file = new File(customStyleFileName);
        Map<String, String> customStyle;
        try {
            customStyle = FileUtil.fromJson(file, typeRef);
            customStyle.forEach((k, v) -> {
                if (defaultStyle.containsKey(k)) {
                    defaultStyle.put(k, v);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Load custom style config fail, now use default config, file:{}, err:{}", file.getAbsolutePath(), e.getMessage());
        }
        return defaultStyle;
    }
}

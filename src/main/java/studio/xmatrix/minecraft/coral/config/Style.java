package studio.xmatrix.minecraft.coral.config;

import com.google.gson.reflect.TypeToken;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.Logger;
import studio.xmatrix.minecraft.coral.util.FileUtil;
import studio.xmatrix.minecraft.coral.util.LogUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 语言相关的配置
 */
public class Style {
    private static final Logger LOGGER = LogUtil.getLogger();
    private static final String DEFAULT_STYLE_FILE_PATH = "/assets/coral/coral-style.json";
    private static Map<String, String> style;

    public static void init() {
        // 获取默认配置
        var type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        try {
            style = FileUtil.getResource(DEFAULT_STYLE_FILE_PATH, type);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read Coral default style config", e);
        }

        // 获取自定义配置
        String customPath = Config.getString("style.path");
        if (customPath != null && !customPath.isEmpty()) {
            try {
                Map<String, String> customStyle = FileUtil.get(customPath, type);
                style.putAll(customStyle);
            } catch (IOException e) {
                throw new IllegalArgumentException(String.format("Failed to read Coral style config '%s'", customPath), e);
            }
            LOGGER.info("Init Coral style success, use custom config '{}'", customPath);
        } else {
            LOGGER.info("Init Coral style success");
        }
    }

    public static MutableText format(MutableText text, String key) {
        var formatting = Formatting.byName(style.get(key));
        if (formatting == null || Formatting.WHITE.equals(formatting)) {
            return text;
        }
        return text.formatted(formatting);
    }

    public static MutableText format(MutableText text, String key, int index) {
        var formatting = Formatting.byName(style.get(String.format("%s.$%d", key, index)));
        if (formatting == null || Formatting.WHITE.equals(formatting)) {
            return text;
        }
        return text.formatted(formatting);
    }
}

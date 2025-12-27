package studio.xmatrix.minecraft.coral.config;

import com.google.gson.reflect.TypeToken;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
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
            style = FileUtil.fromResource(DEFAULT_STYLE_FILE_PATH, type);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read Coral default style config", e);
        }

        // 获取自定义配置
        String customPath = Config.getString("style.path");
        if (customPath != null && !customPath.isEmpty()) {
            try {
                Map<String, String> customStyle = FileUtil.fromPath(customPath, type);
                style.putAll(customStyle);
            } catch (IOException e) {
                throw new IllegalArgumentException(String.format("Failed to read Coral style config '%s'", customPath), e);
            }
            LOGGER.info("Init Coral style success, use custom config '{}'", customPath);
        } else {
            LOGGER.info("Init Coral style success");
        }
    }

    public static MutableComponent format(MutableComponent text, String key) {
        var formatting = ChatFormatting.getByName(style.get(key));
        if (formatting == null || ChatFormatting.WHITE.equals(formatting)) {
            return text;
        }
        return text.withStyle(formatting);
    }

    public static MutableComponent format(MutableComponent text, String key, int index) {
        var formatting = ChatFormatting.getByName(style.get(String.format("%s.$%d", key, index)));
        if (formatting == null || ChatFormatting.WHITE.equals(formatting)) {
            return text;
        }
        return text.withStyle(formatting);
    }
}

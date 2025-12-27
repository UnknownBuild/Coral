package studio.xmatrix.minecraft.coral.config;

import com.google.gson.reflect.TypeToken;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.logging.log4j.Logger;
import studio.xmatrix.minecraft.coral.util.FileUtil;
import studio.xmatrix.minecraft.coral.util.LogUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 语言相关的配置
 */
public class Language {
    private static final Logger LOGGER = LogUtil.getLogger();
    private static final String DEFAULT_LANG_FILE_PATH = "/assets/coral/lang/%s.json";
    private static final Pattern ARG_FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");
    private static Map<String, String> lang;

    public static void init() {
        // 获取默认配置
        String code = Config.getString("language");
        var type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        try {
            lang = FileUtil.fromResource(String.format(DEFAULT_LANG_FILE_PATH, code), type);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read Coral default language config", e);
        }

        // 获取自定义配置
        String customPath = Config.getString("language.path");
        if (customPath != null && !customPath.isEmpty()) {
            try {
                Map<String, String> customLang = FileUtil.fromPath(customPath, type);
                lang.putAll(customLang);
            } catch (IOException e) {
                throw new IllegalArgumentException(String.format("Failed to read Coral language config '%s'", customPath), e);
            }
            LOGGER.info("Init Coral language success, use custom config '{}'", customPath);
        } else {
            LOGGER.info("Init Coral language success");
        }
    }

    /**
     * 翻译文本
     */
    public static MutableComponent format(String key) {
        return Component.literal(lang.getOrDefault(key, key));
    }

    /**
     * 翻译文本, 并携带样式
     */
    public static MutableComponent formatStyle(String key, Object... args) {
        if (!lang.containsKey(key)) {
            return Component.literal(key);
        }

        String value = lang.get(key);
        MutableComponent text = Component.empty();
        Matcher matcher = ARG_FORMAT.matcher(value);
        int start = 0, matchEnd;
        for (int i = 0; matcher.find(start); start = matchEnd) {
            int matchStart = matcher.start();
            matchEnd = matcher.end();

            if (matchStart > start) {
                text.append(Style.format(Component.literal(value.substring(start, matchStart)), key));
            }

            String matchString = value.substring(matchStart, matchEnd);
            String type = matcher.group(2);
            if ("%".equals(type) && "%%".equals(matchString)) {
                text.append("%");
            } else if (!"s".equals(type)) {
                throw new IllegalArgumentException("Unsupported format: " + matchString);
            } else {
                String numString = matcher.group(1);
                int num = numString != null ? Integer.parseInt(numString) : ++i;
                if (num <= args.length) {
                    text.append(Style.format(convertArgToText(args[num - 1]), key, num));
                }
            }
        }

        if (start < value.length()) {
            text.append(Style.format(Component.literal(value.substring(start)), key));
        }
        return text;
    }

    private static MutableComponent convertArgToText(Object obj) {
        if (obj instanceof Component) {
            return (MutableComponent) obj;
        } else if (obj == null) {
            return Component.literal("null");
        } else {
            return Component.literal(obj.toString());
        }
    }
}

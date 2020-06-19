package studio.xmatrix.minecraft.coral.util;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import studio.xmatrix.minecraft.coral.config.LangLoader;
import studio.xmatrix.minecraft.coral.config.StyleLoader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {

    private static final Pattern ARG_FORMAT = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    public static MutableText byKey(String key, Object... args) {
        MutableText text = new LiteralText("");
        String value = LangLoader.getValue(key);
        Matcher matcher = ARG_FORMAT.matcher(value);
        int start = 0, matchEnd;
        for (int i = 0; matcher.find(start); start = matchEnd) {
            int matchStart = matcher.start();
            matchEnd = matcher.end();

            if (matchStart > start) {
                Formatting formatting = getFormatting(key, -1);
                if (formatting == null) {
                    text.append(value.substring(start, matchStart));
                } else {
                    text.append(new LiteralText(value.substring(start, matchStart)).formatted(formatting));
                }
            }

            String matchString = value.substring(matchStart, matchEnd);
            String type = matcher.group(2);
            if ("%".equals(type) && "%%".equals(matchString)) {
                text.append("%");
            } else if (!"s".equals(type)) {
                throw new IllegalArgumentException("Unsupported format: " + matchString);
            } else {
                String numString = matcher.group(1);
                int num = numString != null ? Integer.parseInt(numString) - 1 : i++;
                if (num < args.length) {
                    Formatting formatting = getFormatting(key, num);
                    if (formatting == null) {
                        text.append(convertArgToText(args[num]));
                    } else {
                        text.append(convertArgToText(args[num]).formatted(formatting));
                    }
                }
            }
        }

        if (start < value.length()) {
            Formatting formatting = getFormatting(key, -1);
            if (formatting == null) {
                text.append(value.substring(start));
            } else {
                text.append(new LiteralText(value.substring(start)).formatted(formatting));
            }
        }
        return text;
    }

    public static MutableText byKeyAndStyle(String key, String styleKey) {
        String value = LangLoader.getValue(key);
        Formatting formatting = StyleLoader.getValue(styleKey);
        if (formatting == null || formatting.equals(Formatting.WHITE)) {
            return new LiteralText(value);
        } else {
            return new LiteralText(value).formatted(formatting);
        }
    }

    private static Formatting getFormatting(String key, int index) {
        Formatting formatting;
        if (index == -1) {
            formatting = StyleLoader.getValue(key);
        } else {
            formatting = StyleLoader.getValue(String.format("%s.$%d", key, index + 1));
        }
        if (Formatting.WHITE.equals(formatting)) {
            return null;
        }
        return formatting;
    }

    private static LiteralText convertArgToText(Object obj) {
        if (obj instanceof Text) {
            return (LiteralText) obj;
        } else if (obj == null) {
            return new LiteralText("null");
        } else {
            return new LiteralText(obj.toString());
        }
    }
}

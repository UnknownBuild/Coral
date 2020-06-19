package studio.xmatrix.minecraft.coral.util;

import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import studio.xmatrix.minecraft.coral.config.LangLoader;

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
                text.append(value.substring(start, matchStart));
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
                    text.append(convertArgToText(args[num]));
                }
            }
        }

        if (start < value.length()) {
            text.append(value.substring(start));
        }
        return text;
    }

    private static Text convertArgToText(Object obj) {
        if (obj instanceof Text) {
            return (Text) obj;
        } else if (obj == null) {
            return new LiteralText("null");
        } else {
            return new LiteralText(obj.toString());
        }
    }
}

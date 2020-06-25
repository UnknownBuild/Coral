package studio.xmatrix.minecraft.coral.config.validate;

import java.util.regex.Pattern;

public class Validator {

    public static <T> void in(String field, T obj, T[] arr) throws ValidatorException {
        for (T item : arr) {
            if (item.equals(obj)) {
                return;
            }
        }
        throw ValidatorException.in(field, obj, arr);
    }

    public static void match(String field, String obj, String pattern) throws ValidatorException {
        if (Pattern.matches(pattern, obj)) {
            return;
        }
        throw ValidatorException.match(field, obj);
    }

    public static <T> void notNull(String field, T obj) throws ValidatorException {
        if (obj != null) {
            return;
        }
        throw ValidatorException.notNull(field);
    }
}

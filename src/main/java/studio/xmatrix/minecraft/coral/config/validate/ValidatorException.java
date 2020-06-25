package studio.xmatrix.minecraft.coral.config.validate;

import java.util.Arrays;

public class ValidatorException extends Exception {

    private static final String IN = "SET %1$s FAIL, value must be in %3$s, but got %2$s";
    private static final String MATCH = "SET %1$s FAIL, value %2$s doesn't match rule";
    private static final String NOT_NULL = "SET %1$s FAIL, value can't be null";

    private ValidatorException(String msg) {
        super(msg);
    }

    public static <T> ValidatorException in(String field, T obj, T[] arr) {
        return new ValidatorException(String.format(IN, field, obj.toString(), Arrays.toString(arr)));
    }

    public static <T> ValidatorException match(String field, String obj) {
        return new ValidatorException(String.format(MATCH, field, obj));
    }

    public static ValidatorException notNull(String field) {
        return new ValidatorException(String.format(NOT_NULL, field));
    }
}

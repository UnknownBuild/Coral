package studio.xmatrix.minecraft.coral.util;

import com.google.gson.Gson;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

public class FileUtil {

    private static final Logger LOGGER = LogUtil.getLogger();

    public static <T> T fromJson(File file, Type valueType) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return loadJson(inputStream, valueType, null);
        }
    }

    public static <T> T fromJson(File file, Type valueType, T defaultValue) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return loadJson(inputStream, valueType, defaultValue);
        }
    }

    public static <T> T fromJsonResource(String path, Type valueType) throws IOException {
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
            return loadJson(inputStream, valueType, null);
        }
    }

    private static <T> T loadJson(InputStream inputStream, Type valueType, T defaultValue) throws IOException {
        if (inputStream == null) {
            throw new IOException("get null inputStream");
        }
        Reader reader = new InputStreamReader(inputStream);
        T value = new Gson().fromJson(reader, valueType);
        if (defaultValue != null) {
            simpleMergeObject(value, defaultValue);
        }
        reader.close();
        return value;
    }

    private static <T> void simpleMergeObject(T mergeObject, T defaultObject) throws IOException {
        try {
            Field[] fields = mergeObject.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                field.setAccessible(true);
                if (field.get(mergeObject) == null) {
                    field.set(mergeObject, field.get(defaultObject));
                }
            }
        } catch (IllegalAccessException e) {
            LOGGER.error("merge object fail, err:{}", e.getMessage());
            throw new IOException("simple merge object fail", e);
        }
    }
}

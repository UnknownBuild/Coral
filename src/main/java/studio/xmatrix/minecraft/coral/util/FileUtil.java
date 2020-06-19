package studio.xmatrix.minecraft.coral.util;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.*;

public class FileUtil {

    public static <T> T fromJson(File file, TypeReference<T> valueTypeRef) throws IOException {
        return loadResource(file, valueTypeRef, new JsonFactory());
    }

    public static <T> T fromJsonResource(String path, TypeReference<T> valueTypeRef) throws IOException {
        return loadResource(path, valueTypeRef, new JsonFactory());
    }

    public static <T> T fromYaml(File file, Class<T> valueType, T defaultValue) throws IOException {
        return loadResource(file, valueType, new YAMLFactory(), defaultValue);
    }

    public static <T> T fromYamlResource(String path, Class<T> valueType) throws IOException {
        return loadResource(path, valueType, new YAMLFactory());
    }

    private static <T> T loadResource(String path, Class<T> valueType, JsonFactory factory) throws IOException {
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
            ObjectMapper mapper = new ObjectMapper(factory);
            return mapper.readValue(inputStream, valueType);
        }
    }

    private static <T> T loadResource(String path, TypeReference<T> valueTypeRef, JsonFactory factory) throws IOException {
        ClassLoader classLoader = FileUtil.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
            ObjectMapper mapper = new ObjectMapper(factory);
            return mapper.readValue(inputStream, valueTypeRef);
        }
    }

    private static <T> T loadResource(File file, Class<T> valueType, JsonFactory factory, T defaultValue) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file);) {
            ObjectMapper mapper = new ObjectMapper(factory);
            mapper.setDefaultSetterInfo(JsonSetter.Value.forValueNulls(Nulls.SKIP));
            ObjectReader reader = mapper.readerForUpdating(defaultValue);
            return reader.readValue(inputStream, valueType);
        }
    }

    private static <T> T loadResource(File file, TypeReference<T> valueTypeRef, JsonFactory factory) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file);) {
            ObjectMapper mapper = new ObjectMapper(factory);
            return mapper.readValue(inputStream, valueTypeRef);
        }
    }
}

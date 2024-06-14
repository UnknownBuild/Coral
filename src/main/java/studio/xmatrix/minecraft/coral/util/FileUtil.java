package studio.xmatrix.minecraft.coral.util;

import com.google.gson.Gson;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

/**
 * 文件相关的工具
 */
public class FileUtil {
    public static <T> T fromPath(String path, Type valueType) throws IOException {
        var file = new File(path);
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return loadJson(inputStream, valueType);
        }
    }

    public static <T> T fromResource(String path, Type valueType) throws IOException {
        var file = FileUtil.class.getResourceAsStream(path);
        return loadJson(file, valueType);
    }

    public static String getPrefixName(File file) {
        String name = file.getName();
        int last = name.lastIndexOf('.');
        if (last == -1) {
            return name;
        }
        return name.substring(0, last);
    }

    public static InputStream getResourceAsStream(String path) {
        return FileUtil.class.getResourceAsStream(path);
    }

    private static <T> T loadJson(InputStream inputStream, Type valueType) throws IOException {
        Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        T value = new Gson().fromJson(reader, valueType);
        reader.close();
        return value;
    }
}

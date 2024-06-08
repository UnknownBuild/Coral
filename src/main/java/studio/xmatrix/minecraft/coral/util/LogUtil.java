package studio.xmatrix.minecraft.coral.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.StackLocatorUtil;

public class LogUtil {
    private static final String MOD_NAME = "Coral";

    public static Logger getLogger() {
        String className = StackLocatorUtil.getCallerClass(2).getSimpleName();
        return LogManager.getLogger(String.format("%s|%s", MOD_NAME, className));
    }
}

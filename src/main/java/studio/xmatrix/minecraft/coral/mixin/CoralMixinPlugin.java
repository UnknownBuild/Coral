package studio.xmatrix.minecraft.coral.mixin;

import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import studio.xmatrix.minecraft.coral.config.Config;
import studio.xmatrix.minecraft.coral.util.LogUtil;

import java.util.List;
import java.util.Set;

public class CoralMixinPlugin implements IMixinConfigPlugin {
    private static final Logger LOGGER = LogUtil.getLogger();
    private static final String MIXIN_PACKAGE = "studio.xmatrix.minecraft.coral.mixin.";

    @Override
    public void onLoad(String mixinPackage) {
        // 由于注入 Mixin 依赖配置, 这里提前初始化配置
        try {
            Config.init();
        } catch (RuntimeException e) {
            Config.setInitException(e);
            throw e; // 重新抛出异常
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (!mixinClassName.startsWith(MIXIN_PACKAGE)) {
            LOGGER.error("Error mixin class {}", mixinClassName);
            return false;
        }

        // 将 Mixin 转换成配置项
        StringBuilder builder = new StringBuilder(mixinClassName.length() - MIXIN_PACKAGE.length());
        var firstWord = true;
        for (int i = MIXIN_PACKAGE.length() + "Mixin".length(); i < mixinClassName.length(); i++) {
            char c = mixinClassName.charAt(i);
            if (Character.isUpperCase(c)) {
                if (!builder.isEmpty()) {
                    builder.append(firstWord ? '.' : '_');
                    firstWord = false;
                }
                builder.append(Character.toLowerCase(c));
            } else {
                builder.append(c);
            }
        }

        // 判断功能是否启用
        return Config.getBoolean(builder.toString());
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}

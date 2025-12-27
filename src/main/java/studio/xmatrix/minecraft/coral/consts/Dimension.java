package studio.xmatrix.minecraft.coral.consts;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import studio.xmatrix.minecraft.coral.config.Language;

import java.util.HashMap;
import java.util.Map;

public class Dimension {
    private static final Map<ResourceKey<Level>, MutableComponent> styleTexts = new HashMap<>();

    public static MutableComponent getStyleText(ResourceKey<Level> dimension) {
        if (styleTexts.containsKey(dimension)) {
            return styleTexts.get(dimension);
        }
        // 处理维度文本
        styleTexts.put(Level.OVERWORLD, Language.formatStyle("coral.dimension.over_world"));
        styleTexts.put(Level.NETHER, Language.formatStyle("coral.dimension.nether"));
        styleTexts.put(Level.END, Language.formatStyle("coral.dimension.end"));
        return styleTexts.get(dimension);
    }
}

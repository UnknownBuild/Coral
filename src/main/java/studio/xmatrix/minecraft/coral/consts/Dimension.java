package studio.xmatrix.minecraft.coral.consts;

import net.minecraft.registry.RegistryKey;
import net.minecraft.text.MutableText;
import net.minecraft.world.World;
import studio.xmatrix.minecraft.coral.config.Language;

import java.util.HashMap;
import java.util.Map;

public class Dimension {
    private static final Map<RegistryKey<World>, MutableText> styleTexts = new HashMap<>();

    public static MutableText getStyleText(RegistryKey<World> dimension) {
        if (styleTexts.containsKey(dimension)) {
            return styleTexts.get(dimension);
        }
        // 处理维度文本
        styleTexts.put(World.OVERWORLD, Language.formatStyle("coral.dimension.over_world"));
        styleTexts.put(World.NETHER, Language.formatStyle("coral.dimension.nether"));
        styleTexts.put(World.END, Language.formatStyle("coral.dimension.end"));
        return styleTexts.get(dimension);
    }
}

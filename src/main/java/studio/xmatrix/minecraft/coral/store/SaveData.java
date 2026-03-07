package studio.xmatrix.minecraft.coral.store;

import net.minecraft.resources.Identifier;

public class SaveData {
    private static final String NAMESPACE = "coral";

    public static Identifier Id(String name) {
        return Identifier.tryBuild(NAMESPACE, name);
    }
}

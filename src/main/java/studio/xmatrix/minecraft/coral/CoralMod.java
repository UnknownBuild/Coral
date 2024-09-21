package studio.xmatrix.minecraft.coral;

import net.fabricmc.api.ModInitializer;
import studio.xmatrix.minecraft.coral.command.CommandCallback;
import studio.xmatrix.minecraft.coral.command.HereCommand;
import studio.xmatrix.minecraft.coral.command.PlayerCommand;
import studio.xmatrix.minecraft.coral.command.WRUCommand;
import studio.xmatrix.minecraft.coral.config.Config;
import studio.xmatrix.minecraft.coral.config.Language;
import studio.xmatrix.minecraft.coral.config.Style;

public class CoralMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // 初始化模组
        Config.init();
        Language.init();
        Style.init();

        // 注册命令
        CommandCallback.init(dispatcher -> {
            HereCommand.register(dispatcher);
            PlayerCommand.register(dispatcher);
            WRUCommand.register(dispatcher);
        });
    }
}

package studio.xmatrix.minecraft.coral.mixin.feature.call_sleep;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.xmatrix.minecraft.coral.config.Language;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends Player {
    @Final
    @Shadow
    private MinecraftServer server;

    private ServerPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @Inject(method = "startSleeping", at = @At("RETURN"))
    private void startSleeping(CallbackInfo ci) {
        // 如果是单人游戏且非局域网模式, 不启用该功能
        if (!this.server.isPublished()) {
            return;
        }

        // 广播消息提示睡觉
        MutableComponent text = Language.formatStyle("coral.feature.call_sleep", this.getDisplayName());
        this.server.getPlayerList().broadcastSystemMessage(text, false);
    }
}

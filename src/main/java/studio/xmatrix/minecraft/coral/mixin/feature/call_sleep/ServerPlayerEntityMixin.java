package studio.xmatrix.minecraft.coral.mixin.feature.call_sleep;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.xmatrix.minecraft.coral.config.Language;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Final
    @Shadow
    private MinecraftServer server;

    private ServerPlayerEntityMixin(World world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Inject(method = "sleep", at = @At("RETURN"))
    private void sleep(CallbackInfo ci) {
        // 如果是单人游戏且非局域网模式, 不启用该功能
        if (!this.server.isRemote()) {
            return;
        }

        // 广播消息提示睡觉
        MutableText text = Language.formatStyle("coral.feature.call_sleep", this.getDisplayName());
        this.server.getPlayerManager().broadcast(text, false);
    }
}

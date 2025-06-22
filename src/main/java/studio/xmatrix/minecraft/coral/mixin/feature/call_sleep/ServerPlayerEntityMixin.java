package studio.xmatrix.minecraft.coral.mixin.feature.call_sleep;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.xmatrix.minecraft.coral.config.Language;

import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    private ServerPlayerEntityMixin(World world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Inject(method = "sleep", at = @At(value = "RETURN"))
    private void sleep(CallbackInfo ci) {
        // 如果是单人游戏且非局域网模式, 不启用该功能
        var server = Objects.requireNonNull(this.getServer());
        if (!server.isRemote()) {
            return;
        }

        // 广播消息提示睡觉
        MutableText text = Language.formatStyle("coral.feature.call_sleep", this.getDisplayName());
        server.getPlayerManager().broadcast(text, false);
    }
}

package studio.xmatrix.minecraft.coral.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.xmatrix.minecraft.coral.config.Language;

import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinFeatureCallSleep extends PlayerEntity {
    private MixinFeatureCallSleep(World world, BlockPos blockPos, float yaw, GameProfile gameProfile) {
        super(world, blockPos, yaw, gameProfile);
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

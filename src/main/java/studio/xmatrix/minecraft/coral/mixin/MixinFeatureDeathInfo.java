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
import studio.xmatrix.minecraft.coral.consts.Dimension;

import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinFeatureDeathInfo extends PlayerEntity {
    private MixinFeatureDeathInfo(World world, BlockPos blockPos, float yaw, GameProfile gameProfile) {
        super(world, blockPos, yaw, gameProfile);
    }

    @Inject(method = "onDeath", at = @At(value = "RETURN"))
    private void onDeath(CallbackInfo ci) {
        // 广播死亡地址
        String coordinateText = String.format("[%d, %d, %d]", this.getBlockX(), this.getBlockY(), this.getBlockZ());
        MutableText text = Language.formatStyle("coral.feature.death_info", this.getDisplayName(),
                Dimension.getStyleText(this.getWorld().getRegistryKey()), coordinateText);
        Objects.requireNonNull(this.getServer()).getPlayerManager().broadcast(text, false);
    }
}

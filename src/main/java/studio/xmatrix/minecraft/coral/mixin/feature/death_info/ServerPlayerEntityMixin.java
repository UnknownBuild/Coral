package studio.xmatrix.minecraft.coral.mixin.feature.death_info;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.xmatrix.minecraft.coral.config.Language;
import studio.xmatrix.minecraft.coral.consts.Dimension;

import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends LivingEntity {
    @Final
    @Shadow
    private MinecraftServer server;

    protected ServerPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract ServerWorld getEntityWorld();

    @Inject(method = "onDeath", at = @At("RETURN"))
    private void onDeath(CallbackInfo ci) {
        // 广播死亡地址
        String coordinateText = String.format("[%d, %d, %d]", this.getBlockX(), this.getBlockY(), this.getBlockZ());
        MutableText text = Language.formatStyle("coral.feature.death_info", this.getDisplayName(),
                Dimension.getStyleText(this.getEntityWorld().getRegistryKey()), coordinateText);
        Objects.requireNonNull(this.server).getPlayerManager().broadcast(text, false);
    }
}

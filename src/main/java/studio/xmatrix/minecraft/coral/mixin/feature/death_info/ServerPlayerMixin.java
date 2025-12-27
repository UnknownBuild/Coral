package studio.xmatrix.minecraft.coral.mixin.feature.death_info;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.xmatrix.minecraft.coral.config.Language;
import studio.xmatrix.minecraft.coral.consts.Dimension;

import java.util.Objects;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin extends LivingEntity {
    @Final
    @Shadow
    private MinecraftServer server;

    protected ServerPlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow
    public abstract ServerLevel level();

    @Inject(method = "die", at = @At("RETURN"))
    private void die(CallbackInfo ci) {
        // 广播死亡地址
        String coordinateText = String.format("[%d, %d, %d]", this.getBlockX(), this.getBlockY(), this.getBlockZ());
        MutableComponent text = Language.formatStyle("coral.feature.death_info", this.getDisplayName(),
                Dimension.getStyleText(this.level().dimension()), coordinateText);
        Objects.requireNonNull(this.server).getPlayerList().broadcastSystemMessage(text, false);
    }
}

package studio.xmatrix.minecraft.coral.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.xmatrix.minecraft.coral.config.ConfigLoader;
import studio.xmatrix.minecraft.coral.util.TextUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {

    private static Map<RegistryKey<World>, MutableText> dimensionTexts;

    private MixinServerPlayerEntity(World world, BlockPos blockPos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
        super(world, blockPos, yaw, gameProfile, publicKey);
    }

    @Inject(method = "onDeath", at = @At(value = "RETURN"))
    private void onDeath(CallbackInfo ci) {
        if (!ConfigLoader.getConfig().getFunctionMsgDeathInfo()) {
            return;
        }
        MinecraftServer minecraftServer = this.getServer();

        MutableText coordinateText = Text.literal(String.format("[x%d, y:%d, z:%d]", this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ()))
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
                        String.format("/execute in %s run tp @s %d %d %d", this.world.getRegistryKey().getValue().toString(),
                                this.getBlockPos().getX(), this.getBlockPos().getY(), this.getBlockPos().getZ())))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("chat.coordinates.tooltip"))));
        MutableText text = TextUtil.byKey("msg.deathInfo", this.getDisplayName(), getDimensionText(this.world.getRegistryKey()), coordinateText);
        Objects.requireNonNull(minecraftServer).getPlayerManager().broadcast(text, false);
    }

    @Inject(method = "sleep", at = @At(value = "RETURN"))
    private void sleep(CallbackInfo ci) {
        if (!ConfigLoader.getConfig().getFunctionMsgCallSleep()) {
            return;
        }
        MinecraftServer minecraftServer = this.getServer();
        MutableText text = TextUtil.byKey("msg.callSleep", this.getDisplayName());
        Objects.requireNonNull(minecraftServer).getPlayerManager().broadcast(text, false);
    }

    private MutableText getDimensionText(RegistryKey<World> key) {
        if (dimensionTexts == null) {
            dimensionTexts = new HashMap<>();
            dimensionTexts.put(World.OVERWORLD, TextUtil.byKeyAndStyle("env.dimension.overWorld", "msg.deathInfo.$2.overWorld"));
            dimensionTexts.put(World.NETHER, TextUtil.byKeyAndStyle("env.dimension.nether", "msg.deathInfo.$2.nether"));
            dimensionTexts.put(World.END, TextUtil.byKeyAndStyle("env.dimension.end", "msg.deathInfo.$2.end"));
        }
        return dimensionTexts.get(key);
    }
}

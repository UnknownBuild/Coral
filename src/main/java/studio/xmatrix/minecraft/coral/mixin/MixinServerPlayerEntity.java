package studio.xmatrix.minecraft.coral.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.xmatrix.minecraft.coral.config.ConfigLoader;
import studio.xmatrix.minecraft.coral.util.TextUtil;

import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {

    private MixinServerPlayerEntity(World world, BlockPos blockPos, GameProfile gameProfile) {
        super(world, blockPos, gameProfile);
    }

    @Inject(method = "sleep", at = @At(value = "RETURN"))
    private void onSleep(CallbackInfo ci) {
        if (!ConfigLoader.getConfig().getFunction().getMsgCallSleep()) {
            return;
        }
        MinecraftServer minecraftServer = this.getServer();
        MutableText text = TextUtil.byKey("msg.callSleep", this.getDisplayName());
        Objects.requireNonNull(minecraftServer).getPlayerManager().broadcastChatMessage(text, MessageType.SYSTEM, Util.NIL_UUID);
    }
}

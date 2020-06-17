package studio.xmatrix.minecraft.coral.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.xmatrix.minecraft.coral.config.ConfigLoader;

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
        MutableText text = new LiteralText(this.getName().asString())
                .formatted(Formatting.YELLOW)
                .append(new LiteralText(" 喊你睡觉觉啦").formatted(Formatting.AQUA));
        Objects.requireNonNull(minecraftServer).getPlayerManager().sendToAll(text);
    }
}

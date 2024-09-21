package studio.xmatrix.minecraft.coral.mixin.command.player;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.xmatrix.minecraft.coral.store.CoralPlayerState;

@Mixin(PlayerManager.class)
public abstract class PlayerManagerMixin {
    @Shadow
    public abstract MinecraftServer getServer();

    @Inject(method = "savePlayerData", at = @At("RETURN"))
    private void savePlayerData(ServerPlayerEntity player, CallbackInfo ci) {
        // 将玩家数据写入本地存储
        var state = CoralPlayerState.fromServer(getServer());
        state.updatePlayer(player);
    }
}

package studio.xmatrix.minecraft.coral.mixin.command.player;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import studio.xmatrix.minecraft.coral.store.CoralPlayerSaveData;

@Mixin(PlayerList.class)
public abstract class PlayerListMixin {
    @Shadow
    public abstract MinecraftServer getServer();

    @Inject(method = "save", at = @At("RETURN"))
    private void save(ServerPlayer serverPlayer, CallbackInfo ci) {
        // 将玩家数据写入本地存储
        var state = CoralPlayerSaveData.fromServer(getServer());
        state.updatePlayer(serverPlayer);
    }
}

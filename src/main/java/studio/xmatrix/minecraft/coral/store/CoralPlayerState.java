package studio.xmatrix.minecraft.coral.store;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoralPlayerState extends PersistentState {
    private static final String KEY = "coral_player";
    private final HashMap<UUID, CoralPlayer> players = new HashMap<>();

    private static CoralPlayerState fromNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        var state = new CoralPlayerState();
        var playersNbt = nbt.getCompound("players");
        playersNbt.getKeys().forEach(key -> {
            var playerNbt = playersNbt.getCompound(key);
            var name = playerNbt.getString("name");
            var playTime = new Date(playerNbt.getLong("play_time"));
            state.players.put(UUID.fromString(key), new CoralPlayer(name, playTime));
        });
        return state;
    }

    public static CoralPlayerState fromServer(MinecraftServer server) {
        var persistentStateManager = server.getOverworld().getPersistentStateManager();
        var type = new PersistentState.Type<>(CoralPlayerState::new, CoralPlayerState::fromNbt, null);
        return persistentStateManager.getOrCreate(type, KEY);
    }

    public Map<UUID, CoralPlayer> getPlayers() {
        return players;
    }

    // 更新玩家信息
    public void updatePlayer(ServerPlayerEntity serverPlayer) {
        var player = players.computeIfAbsent(serverPlayer.getUuid(), uuid -> new CoralPlayer());
        player.setName(serverPlayer.getGameProfile().getName());
        player.setPlayTime(new Date());
        markDirty(); // 标记数据已更新
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        var playersNbt = new NbtCompound();
        players.forEach((uuid, player) -> {
            var playerNbt = new NbtCompound();
            playerNbt.putString("name", player.getName());
            playerNbt.putLong("play_time", player.getPlayTime().getTime());
            playersNbt.put(uuid.toString(), playerNbt);
        });
        nbt.put("players", playersNbt);
        return nbt;
    }
}

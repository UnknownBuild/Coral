package studio.xmatrix.minecraft.coral.store;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoralPlayerState extends PersistentState {
    private static final String KEY = "coral_player";
    private static final Codec<CoralPlayerState> CODEC = Codec.of(new Encoder<>() {
        @Override
        public <T> DataResult<T> encode(CoralPlayerState state, DynamicOps<T> ops, T prefix) {
            //noinspection unchecked
            return DataResult.success((T) state.toNbt());
        }
    }, new Decoder<>() {
        @Override
        public <T> DataResult<Pair<CoralPlayerState, T>> decode(DynamicOps<T> ops, T input) {
            var nbt = (NbtCompound) ops.convertTo(NbtOps.INSTANCE, input);
            return DataResult.success(Pair.of(fromNbt(nbt), ops.empty()));
        }
    });
    private final HashMap<UUID, CoralPlayer> players = new HashMap<>();

    private static CoralPlayerState fromNbt(NbtCompound nbt) {
        var state = new CoralPlayerState();
        var playersNbtOp = nbt.getCompound("players");
        playersNbtOp.ifPresent(playersNbt -> {
            playersNbt.getKeys().forEach(key -> {
                var playerNbtOp = playersNbt.getCompound(key);
                if (playerNbtOp.isEmpty()) {
                    return;
                }
                var playerNbt = playerNbtOp.get();
                var name = playerNbt.getString("name").orElse("");
                long playTime = playerNbt.getLong("play_time").orElse(0L);
                if (name.isEmpty() || playTime == 0) {
                    return;
                }
                state.players.put(UUID.fromString(key), new CoralPlayer(name, new Date(playTime)));
            });
        });
        return state;
    }

    public static CoralPlayerState fromServer(MinecraftServer server) {
        var persistentStateManager = server.getOverworld().getPersistentStateManager();
        var type = new PersistentStateType<>(KEY, CoralPlayerState::new, CoralPlayerState.CODEC, null);
        return persistentStateManager.getOrCreate(type);
    }

    public NbtCompound toNbt() {
        var nbt = new NbtCompound();
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

    public Map<UUID, CoralPlayer> getPlayers() {
        return players;
    }

    // 更新玩家信息
    public void updatePlayer(ServerPlayerEntity serverPlayer) {
        var player = players.computeIfAbsent(serverPlayer.getUuid(), uuid -> new CoralPlayer());
        player.setName(serverPlayer.getGameProfile().name());
        player.setPlayTime(new Date());
        markDirty(); // 标记数据已更新
    }
}

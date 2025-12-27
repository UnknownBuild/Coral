package studio.xmatrix.minecraft.coral.store;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoralPlayerSaveData extends SavedData {
    private static final String KEY = "coral_player";
    private static final Codec<CoralPlayerSaveData> CODEC = Codec.of(new Encoder<>() {
        @Override
        public <T> DataResult<T> encode(CoralPlayerSaveData state, DynamicOps<T> ops, T prefix) {
            //noinspection unchecked
            return DataResult.success((T) state.toNbt());
        }
    }, new Decoder<>() {
        @Override
        public <T> DataResult<Pair<CoralPlayerSaveData, T>> decode(DynamicOps<T> ops, T input) {
            var nbt = (CompoundTag) ops.convertTo(NbtOps.INSTANCE, input);
            return DataResult.success(Pair.of(fromNbt(nbt), ops.empty()));
        }
    });
    private final HashMap<UUID, CoralPlayer> players = new HashMap<>();

    private static CoralPlayerSaveData fromNbt(CompoundTag nbt) {
        var state = new CoralPlayerSaveData();
        var playersNbtOp = nbt.getCompound("players");
        playersNbtOp.ifPresent(playersNbt -> {
            playersNbt.keySet().forEach(key -> {
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

    public static CoralPlayerSaveData fromServer(MinecraftServer server) {
        var dataStorage = server.overworld().getDataStorage();
        var type = new SavedDataType<>(KEY, CoralPlayerSaveData::new, CoralPlayerSaveData.CODEC, null);
        return dataStorage.get(type);
    }

    public CompoundTag toNbt() {
        var nbt = new CompoundTag();
        var playersNbt = new CompoundTag();
        players.forEach((uuid, player) -> {
            var playerNbt = new CompoundTag();
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
    public void updatePlayer(ServerPlayer serverPlayer) {
        var player = players.computeIfAbsent(serverPlayer.getUUID(), uuid -> new CoralPlayer());
        player.setName(serverPlayer.getGameProfile().name());
        player.setPlayTime(new Date());
        setDirty(); // 标记数据已更新
    }
}

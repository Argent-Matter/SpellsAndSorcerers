package dev.screret.motm.common.memory;

import dev.screret.motm.api.memory.Memory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.longs.Long2ReferenceMap;
import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;

/**
 * A container for memories inside a chunk.
 */
public class LevelMemoryStorage {

    public static final Codec<LevelMemoryStorage> CODEC = Codec.mapPair(BlockPos.CODEC.fieldOf("pos"), Memory.CODEC.fieldOf("memory"))
            .codec().listOf()
            .xmap(entries -> (Long2ReferenceMap<Holder<Memory>>) entries.stream()
                            .map(pair -> Pair.of(pair.getFirst().asLong(), pair.getSecond()))
                            .collect(Collectors.toMap(Pair::getFirst, Pair::getSecond, LevelMemoryStorage::uniqueKeysMerger, Long2ReferenceOpenHashMap::new)),
                    map -> map.long2ReferenceEntrySet().stream()
                            .map(entry -> Pair.of(BlockPos.of(entry.getLongKey()), entry.getValue()))
                            .toList())
            .xmap(LevelMemoryStorage::new, LevelMemoryStorage::getMemories);

    public static final StreamCodec<RegistryFriendlyByteBuf, LevelMemoryStorage> STREAM_CODEC = StreamCodec.ofMember(LevelMemoryStorage::encode, LevelMemoryStorage::decode);

    @Getter(AccessLevel.PRIVATE)
    public final Long2ReferenceMap<Holder<Memory>> memories = new Long2ReferenceOpenHashMap<>();

    public LevelMemoryStorage() {}

    protected LevelMemoryStorage(Long2ReferenceMap<Holder<Memory>> map) {
        this.memories.putAll(map);
    }

    public @Nullable Holder<Memory> getMemoryAtPosition(BlockPos pos) {
        return memories.get(pos.asLong());
    }

    public void putMemory(BlockPos pos, Holder<Memory> memory) {
        this.memories.put(pos.asLong(), memory);
    }

    public LevelMemoryStorage copy(IAttachmentHolder holder, HolderLookup.Provider provider) {
        return new LevelMemoryStorage(this.memories);
    }

    private void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(this.memories.size());

        for (var entry : this.memories.long2ReferenceEntrySet()) {
            buffer.writeVarLong(entry.getLongKey());
            Memory.STREAM_CODEC.encode(buffer, entry.getValue());
        }
    }

    private static LevelMemoryStorage decode(RegistryFriendlyByteBuf buffer) {
        LevelMemoryStorage storage = new LevelMemoryStorage();

        int size = buffer.readVarInt();
        for (int i = 0; i < size; i++) {
            long pos = buffer.readVarLong();
            Holder<Memory> memory = Memory.STREAM_CODEC.decode(buffer);
            storage.memories.put(pos, memory);
        }
        return storage;
    }

    private static Holder<Memory> uniqueKeysMerger(Holder<Memory> o1, Holder<Memory> o2) {
        throw new IllegalArgumentException("Cannot merge map values %s and %s".formatted(o1, o2));
    }
}

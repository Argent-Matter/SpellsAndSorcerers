package dev.screret.motm.common.block.entity;

import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.data.MOTMBlockEntities;
import dev.screret.motm.data.MOTMDataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;

import org.jetbrains.annotations.Nullable;

public class MemorystoneBlockEntity extends BlockEntity {

    @Getter
    @Setter
    private @Nullable Holder<Memory> memory;

    public MemorystoneBlockEntity(BlockPos pos, BlockState state) {
        super(MOTMBlockEntities.MEMORYSTONE.get(), pos, state);
    }

    @Tolerate
    public void setMemory(ResourceKey<Memory> memory) {
        setMemory(getLevel().registryAccess().holderOrThrow(memory));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);

        if (tag.contains("memory")) {
            this.memory = Memory.CODEC.parse(ops, tag.get("memory")).getOrThrow();
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);

        if (this.memory != null) {
            tag.put("memory", Memory.CODEC.encodeStart(ops, this.memory).getOrThrow());
        }
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.memory = componentInput.getOrDefault(MOTMDataComponents.CONTAINED_MEMORY, null);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(MOTMDataComponents.CONTAINED_MEMORY, this.memory);
    }
}

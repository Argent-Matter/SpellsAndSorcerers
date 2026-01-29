package com.gtceu.syncsystem;

import com.gtceu.syncsystem.SyncSystem;
import com.gtceu.common.network.GTNetwork;
import com.gtceu.syncsystem.network.SPacketUpdateBESyncValue;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

/**
 * A BlockEntity that manages sync and save data via the {@code ISyncManaged} syncdata system.
 * 
 * @see ISyncManaged
 */
public abstract class ManagedSyncBlockEntity extends BlockEntity implements ISyncManaged {

    @Getter
    protected final SyncDataHolder syncDataHolder = new SyncDataHolder(this);
    @Getter
    @Setter
    private boolean isDirty;

    public ManagedSyncBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    // Called when this BlockEntity is saved or loaded

    @Override
    protected final void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.merge(getSyncDataHolder().serializeNBT(false));
    }

    @Override
    @MustBeInvokedByOverriders
    public void load(CompoundTag tag) {
        super.load(tag);
        getSyncDataHolder().deserializeNBT(tag,
                (getLevel() == null ? SyncSystem.isClientThread() : getLevel().isClientSide));
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        getSyncDataHolder().resyncAllFields();
        tag.merge(getSyncDataHolder().serializeNBT(true, true));
        return tag;
    }

    @Override
    public final void markAsChanged() {
        isDirty = true;
    }

    public final void updateTick() {
        setChanged();
        if (isDirty) {
            var level = getLevel();
            if (level == null) return;
            GTNetwork.sendToAllPlayersTrackingChunk(level.getChunkAt(getBlockPos()),
                    new SPacketUpdateBESyncValue(this));
            isDirty = false;
        }
    }
}

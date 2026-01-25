package dev.screret.modularui.value.sync;

import dev.screret.modularui.api.value.sync.IValueSyncHandler;

import net.minecraft.network.RegistryFriendlyByteBuf;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

public abstract class ValueSyncHandler<B extends ByteBuf, T> extends SyncHandler implements IValueSyncHandler<B, T> {

    public static final int SYNC_VALUE = 0;

    @Getter
    @Setter
    private Runnable changeListener;

    @SuppressWarnings("unchecked")
    @Override
    public void readOnClient(int id, RegistryFriendlyByteBuf buf) {
        // the lowest subclass of ByteBuf is RegistryFriendlyByteBuf so this *should* work
        if (id == SYNC_VALUE) read((B) buf);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readOnServer(int id, RegistryFriendlyByteBuf buf) {
        // the lowest subclass of ByteBuf is RegistryFriendlyByteBuf so this *should* work
        if (id == SYNC_VALUE) read((B) buf);
    }

    @SuppressWarnings("unchecked")
    protected void sync() {
        // the lowest subclass of ByteBuf is RegistryFriendlyByteBuf so this *should* work
        sync(SYNC_VALUE, buf -> this.write((B) buf));
    }

    @Override
    public void detectAndSendChanges(boolean init) {
        if (updateCacheFromSource(init)) sync();
    }

    /**
     * Called when the cached value of this sync handler updates. Implementations need to call this inside
     * {@link #setValue(Object, boolean, boolean)}.
     */
    protected void onValueChanged() {
        if (this.changeListener != null) {
            this.changeListener.run();
        }
    }
}

package dev.screret.modularui.value.sync;

import dev.screret.modularui.api.value.sync.IValueSyncHandler;
import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

public abstract class ValueSyncHandler<B extends ByteBuf, T> extends SyncHandler implements IValueSyncHandler<B, T> {

    public static final int SYNC_VALUE = 0;

    @Getter
    @Setter
    private Runnable changeListener;

    @Override
    public void readOnClient(int id, RegistryFriendlyByteBuf buf) {
        if (id == SYNC_VALUE) read(buf);
    }

    @Override
    public void readOnServer(int id, RegistryFriendlyByteBuf buf) {
        if (id == SYNC_VALUE) read(buf);
    }

    protected void sync() {
        sync(SYNC_VALUE, this::write);
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

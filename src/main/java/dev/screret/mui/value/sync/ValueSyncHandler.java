package dev.screret.mui.value.sync;

import dev.screret.mui.api.value.sync.IValueSyncHandler;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;

public abstract class ValueSyncHandler<B extends ByteBuf, T> extends SyncHandler implements IValueSyncHandler<B, T> {

    @Getter
    @Setter
    private Runnable changeListener;

    @SuppressWarnings("unchecked")
    @Override
    public void readOnClient(int id, RegistryFriendlyByteBuf buf) {
        // B the lowest B can be is RegistryFriendlyByteBuf so this *should* work
        read((B) buf);
        onValueChanged();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readOnServer(int id, RegistryFriendlyByteBuf buf) {
        // B the lowest B can be is RegistryFriendlyByteBuf so this *should* work
        read((B) buf);
        onValueChanged();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void detectAndSendChanges(boolean init) {
        if (updateCacheFromSource(init)) {
            // B the lowest B can be is RegistryFriendlyByteBuf so this *should* work
            syncToClient(0, buf -> this.write((B) buf));
        }
    }

    protected void onValueChanged() {
        if (this.changeListener != null) {
            this.changeListener.run();
        }
    }
}

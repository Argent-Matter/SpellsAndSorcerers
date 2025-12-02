package dev.screret.modularui.value.sync;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class CursorSlotSyncHandler extends SyncHandler {

    public void sync() {
        sync(0, (RegistryFriendlyByteBuf buffer) -> ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer,
                getSyncManager().getPlayer().containerMenu.getCarried()));
    }

    @Override
    public void readOnClient(int id, RegistryFriendlyByteBuf buf) {
        getSyncManager().getPlayer().containerMenu.setCarried(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
    }

    @Override
    public void readOnServer(int id, RegistryFriendlyByteBuf buf) {
        getSyncManager().getPlayer().containerMenu.setCarried(ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
    }
}

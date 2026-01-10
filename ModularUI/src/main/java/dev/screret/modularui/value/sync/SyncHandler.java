package dev.screret.modularui.value.sync;

import dev.screret.modularui.api.IPacketWriter;
import dev.screret.modularui.api.value.ISyncOrValue;
import dev.screret.modularui.network.ModularNetwork;
import dev.screret.modularui.network.ModularNetworkSide;

import lombok.Getter;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Base class for handling syncing of widgets.
 * A sync handler must exist on client and server.
 * It must be configured exactly the same to avoid issues.
 */
public abstract class SyncHandler implements ISyncOrValue {

    private PanelSyncManager syncManager;
    /**
     * the key that belongs to this sync handler
     */
    @Getter
    private String key;

    @ApiStatus.OverrideOnly
    @MustBeInvokedByOverriders
    public void init(String key, PanelSyncManager syncManager) {
        this.key = key;
        this.syncManager = syncManager;
    }

    @ApiStatus.OverrideOnly
    @MustBeInvokedByOverriders
    public void dispose() {
        this.key = null;
        this.syncManager = null;
    }

    /**
     * Syncs a custom packet to the client
     *
     * @param id             an internal denominator to identify this package
     * @param bufferConsumer the package builder
     */
    public final void syncToClient(int id, @NotNull IPacketWriter<? super RegistryFriendlyByteBuf> bufferConsumer) {
        IPacketWriter<? super RegistryFriendlyByteBuf> writer = buffer -> {
            VarInt.write(buffer, id);
            bufferConsumer.write(buffer);
        };
        sendToClient(getSyncManager().getPanelName(), writer, this);
    }

    /**
     * Syncs a custom packet to the server
     *
     * @param id             an internal denominator to identify this package
     * @param bufferConsumer the package builder
     */
    @OnlyIn(Dist.CLIENT)
    public final void syncToServer(int id, @NotNull IPacketWriter<? super RegistryFriendlyByteBuf> bufferConsumer) {
        IPacketWriter<? super RegistryFriendlyByteBuf> writer = buffer -> {
            VarInt.write(buffer, id);
            bufferConsumer.write(buffer);
        };
        sendToServer(getSyncManager().getPanelName(), writer, this);
    }

    /**
     * Sync a custom packet to the other side.
     *
     * @param id             an internal denominator to identify this package
     * @param bufferConsumer the package builder
     */
    public final void sync(int id, @NotNull IPacketWriter<? super RegistryFriendlyByteBuf> bufferConsumer) {
        IPacketWriter<? super RegistryFriendlyByteBuf> writer = buffer -> {
            VarInt.write(buffer, id);
            bufferConsumer.write(buffer);
        };
        send(ModularNetwork.get(getSyncManager().isClient()), getSyncManager().getPanelName(), writer, this);
    }

    /**
     * Sends an empty packet to the client with an id.
     *
     * @param id identifier
     */
    public final void syncToClient(int id) {
        syncToClient(id, buf -> {});
    }

    /**
     * Sends an empty packet to the server with an id.
     *
     * @param id identifier
     */
    public final void syncToServer(int id) {
        syncToServer(id, buf -> {});
    }

    /**
     * Sends an empty packet to the other side with an id.
     *
     * @param id identifier
     */
    public final void sync(int id) {
        sync(id, buf -> {});
    }

    /**
     * Called when this sync handler receives a packet on client.
     *
     * @param id  an internal denominator to identify this package
     * @param buf package
     */
    @ApiStatus.OverrideOnly
    @OnlyIn(Dist.CLIENT)
    public abstract void readOnClient(int id, RegistryFriendlyByteBuf buf);

    /**
     * Called when this sync handler receives a packet on server.
     *
     * @param id  an internal denominator to identify this package
     * @param buf package
     */
    @ApiStatus.OverrideOnly
    public abstract void readOnServer(int id, RegistryFriendlyByteBuf buf);

    /**
     * Called at least every tick. Use it to compare a cached value to its original and sync it.
     * This is only called on the server side.
     *
     * @param init if this method is being called the first time.
     */
    public void detectAndSendChanges(boolean init) {}

    /**
     * @return is this sync handler has been initialised yet
     */
    public final boolean isValid() {
        return this.key != null && this.syncManager != null;
    }

    /**
     * @return the sync handler manager handling this sync handler
     */
    public PanelSyncManager getSyncManager() {
        if (!isValid()) {
            throw new IllegalStateException("Sync handler is not yet initialised!");
        }
        return this.syncManager;
    }

    public final boolean isRegistered() {
        return isValid() && this.syncManager.hasSyncHandler(this);
    }

    @Override
    public boolean isSyncHandler() {
        return true;
    }

    private static void send(ModularNetworkSide network, String panel, IPacketWriter<? super RegistryFriendlyByteBuf> writer,
                             SyncHandler syncHandler) {
        Objects.requireNonNull(writer);
        Objects.requireNonNull(syncHandler);
        if (!syncHandler.isValid()) {
            throw new IllegalStateException("Not initialized sync handlers can't send packets!");
        }
        network.sendSyncHandlerPacket(panel, syncHandler, writer, syncHandler.syncManager.getPlayer());
    }

    public static void sendToClient(String panel, IPacketWriter<? super RegistryFriendlyByteBuf> writer, SyncHandler syncHandler) {
        send(ModularNetwork.SERVER, panel, writer, syncHandler);
    }

    @OnlyIn(Dist.CLIENT)
    public static void sendToServer(String panel, IPacketWriter<? super RegistryFriendlyByteBuf> writer, SyncHandler syncHandler) {
        send(ModularNetwork.CLIENT, panel, writer, syncHandler);
    }
}

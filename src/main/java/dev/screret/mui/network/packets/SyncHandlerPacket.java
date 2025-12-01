package dev.screret.mui.network.packets;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import dev.screret.mui.ModularUI;
import dev.screret.mui.api.IPacketWriter;
import dev.screret.mui.client.screen.ModularContainerMenu;
import dev.screret.mui.client.screen.ModularScreen;
import dev.screret.mui.network.NetworkUtils;
import dev.screret.mui.value.sync.ModularSyncManager;
import io.netty.buffer.Unpooled;
import org.jetbrains.annotations.Nullable;

public record SyncHandlerPacket(String panel, String key, boolean action,
                                @Nullable("Nullable on the sending side") RegistryFriendlyByteBuf packet,
                                @Nullable("Nullable on the receiving side") IPacketWriter<? super RegistryFriendlyByteBuf> packetWriter)
        implements CustomPacketPayload {

    public static final ResourceLocation ID = ModularUI.id("sync_message");
    public static final Type<SyncHandlerPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncHandlerPacket> CODEC = StreamCodec
            .ofMember(SyncHandlerPacket::encode, SyncHandlerPacket::decode);

    public SyncHandlerPacket(String panel, String key, boolean action,
                                           IPacketWriter<? super RegistryFriendlyByteBuf> packetWriter) {
        this(panel, key, action, null, packetWriter);
    }

    public SyncHandlerPacket(String panel, String key, boolean action, RegistryFriendlyByteBuf packet) {
        this(panel, key, action, packet, null);
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        NetworkUtils.writeStringSafe(buf, this.panel);
        NetworkUtils.writeStringSafe(buf, this.key, 64, true);
        buf.writeBoolean(this.action);
        NetworkUtils.writeByteBuf(buf, processPacketWriter(buf.registryAccess(), buf.getConnectionType()));
    }

    private RegistryFriendlyByteBuf processPacketWriter(RegistryAccess registryAccess, ConnectionType connectionType) {
        if (this.packet != null) {
            return packet;
        } else {
            RegistryFriendlyByteBuf buffer = new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess, connectionType);
            if (this.packetWriter != null) {
                this.packetWriter.write(buffer);
            }
            return buffer;
        }
    }

    public static SyncHandlerPacket decode(RegistryFriendlyByteBuf buf) {
        String panel = NetworkUtils.readStringSafe(buf);
        String key = NetworkUtils.readStringSafe(buf);
        boolean action = buf.readBoolean();
        RegistryFriendlyByteBuf packet = RegistryFriendlyByteBuf.decorator(buf.registryAccess(), buf.getConnectionType())
                .apply(NetworkUtils.readFriendlyByteBuf(buf));

        return new SyncHandlerPacket(panel, key, action, packet);
    }

    public void execute(IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            ModularScreen screen = ModularScreen.getCurrent();
            if (screen != null) {
                executeFromManager(screen.getSyncManager());
            }
        } else if (context.flow() == PacketFlow.SERVERBOUND) {
            AbstractContainerMenu menu = context.player().containerMenu;
            if (menu instanceof ModularContainerMenu modularMenu) {
                executeFromManager(modularMenu.getSyncManager());
            }
        }
    }

    private void executeFromManager(ModularSyncManager syncManager) {
        if (this.packet == null) {
            ModularUI.LOGGER.error("Failed to process packet for sync handler {} in panel {}", this.key, this.panel);
            return;
        }
        try {
            int id = this.action ? 0 : this.packet.readVarInt();
            syncManager.receiveWidgetUpdate(this.panel, this.key, this.action, id, this.packet);
        } catch (IndexOutOfBoundsException e) {
            ModularUI.LOGGER.error("Failed to read packet for sync handler {} in panel {}", this.key, this.panel);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

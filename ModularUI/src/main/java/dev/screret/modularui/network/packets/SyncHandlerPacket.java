package dev.screret.modularui.network.packets;

import dev.screret.modularui.ModularUI;
import dev.screret.modularui.api.IPacketWriter;
import dev.screret.modularui.network.ModularNetwork;
import dev.screret.modularui.network.NetworkUtils;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import io.netty.buffer.Unpooled;

import org.jetbrains.annotations.Nullable;

public record SyncHandlerPacket(int networkId, String panel, String key, boolean action,
                                @Nullable("Nullable on the sending side") RegistryFriendlyByteBuf packet,
                                @Nullable("Nullable on the receiving side") IPacketWriter<? super RegistryFriendlyByteBuf> packetWriter)
        implements CustomPacketPayload {

    public static final ResourceLocation ID = ModularUI.id("sync_message");
    public static final Type<SyncHandlerPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncHandlerPacket> CODEC = StreamCodec
            .ofMember(SyncHandlerPacket::encode, SyncHandlerPacket::decode);

    public SyncHandlerPacket(int networkId, String panel, String key, boolean action,
                             IPacketWriter<? super RegistryFriendlyByteBuf> packetWriter) {
        this(networkId, panel, key, action, null, packetWriter);
    }

    public SyncHandlerPacket(int networkId, String panel, String key, boolean action, RegistryFriendlyByteBuf packet) {
        this(networkId, panel, key, action, packet, null);
    }

    public void encode(RegistryFriendlyByteBuf buf) {
        buf.writeVarInt(this.networkId);
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
        int networkId = buf.readVarInt();
        String panel = NetworkUtils.readStringSafe(buf);
        String key = NetworkUtils.readStringSafe(buf);
        boolean action = buf.readBoolean();
        RegistryFriendlyByteBuf packet = RegistryFriendlyByteBuf.decorator(buf.registryAccess(), buf.getConnectionType())
                .apply(NetworkUtils.readFriendlyByteBuf(buf));

        return new SyncHandlerPacket(networkId, panel, key, action, packet);
    }

    public void execute(IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            ModularNetwork.CLIENT.receivePacket(this);
        } else {
            ModularNetwork.SERVER.receivePacket(this);
        }
    }

    @Override
    public Type<SyncHandlerPacket> type() {
        return TYPE;
    }
}

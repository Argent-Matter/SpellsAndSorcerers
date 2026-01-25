package dev.screret.modularui.network.packets;

import dev.screret.modularui.ModularUI;
import dev.screret.modularui.api.UIFactory;
import dev.screret.modularui.factory.GuiData;
import dev.screret.modularui.factory.GuiManager;
import dev.screret.modularui.network.NetworkUtils;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import io.netty.buffer.ByteBuf;

public record OpenGuiPacket<T extends GuiData>(int windowId, int networkId, UIFactory<T> factory, FriendlyByteBuf data)
        implements CustomPacketPayload {

    public static final ResourceLocation ID = ModularUI.id("open_gui");
    public static final Type<OpenGuiPacket<?>> TYPE = new Type<>(ID);
    public static final StreamCodec<ByteBuf, OpenGuiPacket<?>> CODEC = StreamCodec
            .ofMember(OpenGuiPacket::encode, OpenGuiPacket::decode);

    public void encode(ByteBuf buf) {
        VarInt.write(buf, this.windowId);
        VarInt.write(buf, this.networkId);
        ResourceLocation.STREAM_CODEC.encode(buf, this.factory.getFactoryName());
        NetworkUtils.writeByteBuf(buf, this.data);
    }

    public static <T extends GuiData> OpenGuiPacket<T> decode(ByteBuf buf) {
        int windowId = VarInt.read(buf);
        int networkId = VarInt.read(buf);
        // noinspection unchecked
        UIFactory<T> factory = (UIFactory<T>) GuiManager.getFactory(ResourceLocation.STREAM_CODEC.decode(buf));
        FriendlyByteBuf data = NetworkUtils.readFriendlyByteBuf(buf);
        return new OpenGuiPacket<>(windowId, networkId, factory, data);
    }

    public void execute(IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            GuiManager.openFromClient(this.windowId, this.networkId, this.factory, this.data, (LocalPlayer) context.player());
        } else if (context.flow() == PacketFlow.SERVERBOUND && context.player() instanceof ServerPlayer serverPlayer) {
            T guiData = this.factory.readGuiData(serverPlayer, this.data);
            GuiManager.open(this.factory, guiData, serverPlayer);
        }
    }

    @Override
    public Type<OpenGuiPacket<?>> type() {
        return TYPE;
    }
}

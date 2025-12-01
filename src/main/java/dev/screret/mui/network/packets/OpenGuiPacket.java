package dev.screret.mui.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import dev.screret.mui.ModularUI;
import dev.screret.mui.api.UIFactory;
import dev.screret.mui.factory.GuiData;
import dev.screret.mui.factory.GuiManager;
import dev.screret.mui.network.NetworkUtils;

public record OpenGuiPacket<T extends GuiData>(int windowId, UIFactory<T> factory, FriendlyByteBuf data) implements CustomPacketPayload {

    public static final ResourceLocation ID = ModularUI.id("open_gui");
    public static final Type<OpenGuiPacket<?>> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, OpenGuiPacket<?>> CODEC = StreamCodec
            .ofMember(OpenGuiPacket::encode, OpenGuiPacket::decode);

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.windowId);
        buf.writeResourceLocation(this.factory.getFactoryName());
        NetworkUtils.writeByteBuf(buf, this.data);
    }

    public static <T extends GuiData> OpenGuiPacket<T> decode(FriendlyByteBuf buf) {
        int windowId = buf.readVarInt();
        //noinspection unchecked
        UIFactory<T> factory = (UIFactory<T>) GuiManager.getFactory(buf.readResourceLocation());
        FriendlyByteBuf data = NetworkUtils.readFriendlyByteBuf(buf);
        return new OpenGuiPacket<>(windowId, factory, data);
    }

    public void execute(IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            GuiManager.openFromClient(this.windowId, this.factory, this.data, context.player());
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

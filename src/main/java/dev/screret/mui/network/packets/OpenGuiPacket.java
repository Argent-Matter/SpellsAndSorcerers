package dev.screret.mui.network.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import dev.screret.mui.ModularUI;
import dev.screret.mui.api.UIFactory;
import dev.screret.mui.factory.GuiData;
import dev.screret.mui.factory.GuiManager;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public record OpenGuiPacket<T extends GuiData>(int windowId, UIFactory<T> factory, FriendlyByteBuf data) implements CustomPacketPayload {

    public static final ResourceLocation ID = ModularUI.id("cape_changed");
    public static final Type<OpenGuiPacket<?>> TYPE = new Type<>(ID);
    public static final StreamCodec<FriendlyByteBuf, OpenGuiPacket<?>> CODEC = StreamCodec
            .ofMember(OpenGuiPacket::encode, OpenGuiPacket::new);

    public OpenGuiPacket(FriendlyByteBuf buf) {
        this.windowId = buf.readVarInt();
        //noinspection unchecked
        this.factory = (UIFactory<T>) GuiManager.getFactory(buf.readResourceLocation());
        this.data = NetworkUtils.readFriendlyByteBuf(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.windowId);
        buf.writeResourceLocation(this.factory.getFactoryName());
        NetworkUtils.writeByteBuf(buf, this.data);
    }

    @Override
    public void execute(NetworkEvent.Context handler) {
        if (handler.getDirection() == NetworkDirection.PLAY_TO_CLIENT) {
            GuiManager.openFromClient(this.windowId, this.factory, this.data, Minecraft.getInstance().player);
        } else if (handler.getDirection() == NetworkDirection.PLAY_TO_SERVER) {
            T guiData = this.factory.readGuiData(handler.getSender(), this.data);
            GuiManager.open(this.factory, guiData, handler.getSender());
        }
    }

    @Override
    public Type<OpenGuiPacket<?>> type() {
        return null;
    }
}

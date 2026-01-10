package dev.screret.modularui.network.packets;

import dev.screret.modularui.ModularUI;
import dev.screret.modularui.api.MCHelper;
import dev.screret.modularui.network.ModularNetwork;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import io.netty.buffer.ByteBuf;

public record CloseGuiPacket(int networkId, boolean dispose) implements CustomPacketPayload {

    // spotless:off
    public static final ResourceLocation ID = ModularUI.id("close_gui");
    public static final Type<CloseGuiPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<ByteBuf, CloseGuiPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, CloseGuiPacket::networkId,
            ByteBufCodecs.BOOL, CloseGuiPacket::dispose,
            CloseGuiPacket::new
    );
    // spotless:on

    public void execute(IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            ModularNetwork.CLIENT.closeContainer(this.networkId, this.dispose, MCHelper.getPlayer(), false);
        } else {
            ModularNetwork.SERVER.closeContainer(this.networkId, this.dispose, context.player(), false);
        }
    }

    @Override
    public Type<CloseGuiPacket> type() {
        return TYPE;
    }
}

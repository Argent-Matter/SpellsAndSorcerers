package dev.screret.modularui.network.packets;

import dev.screret.modularui.ModularUI;
import dev.screret.modularui.network.ModularNetwork;

import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import io.netty.buffer.ByteBuf;


public record ReopenGuiPacket(int networkId) implements CustomPacketPayload {

    public static final ResourceLocation ID = ModularUI.id("reopen_gui");
    public static final Type<ReopenGuiPacket> TYPE = new Type<>(ID);
public static final StreamCodec<ByteBuf, ReopenGuiPacket> CODEC = ByteBufCodecs.VAR_INT
        .map(ReopenGuiPacket::new, ReopenGuiPacket::networkId);

    public void execute(IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            ModularNetwork.CLIENT.reopen(context.player(), this.networkId, false);
        } else {
            ModularNetwork.SERVER.reopen(context.player(), this.networkId, false);
        }
    }

    @Override
    public Type<ReopenGuiPacket> type() {
        return TYPE;
    }
}

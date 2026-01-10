package dev.screret.modularui.network.packets;

import dev.screret.modularui.ModularUI;
import dev.screret.modularui.api.MCHelper;
import dev.screret.modularui.network.ModularNetwork;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CloseAllGuisPacket() implements CustomPacketPayload {

    public static final CloseAllGuisPacket INSTANCE = new CloseAllGuisPacket();

    public static final ResourceLocation ID = ModularUI.id("close_all_guis");
    public static final Type<CloseAllGuisPacket> TYPE = new Type<>(ID);
    public static final StreamCodec<ByteBuf, CloseAllGuisPacket> CODEC = StreamCodec.unit(INSTANCE);

    public void execute(IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            ModularNetwork.CLIENT.closeAll(MCHelper.getPlayer(), false);
        } else {
            ModularNetwork.SERVER.closeAll(context.player(), false);
        }
    }

    @Override
    public Type<CloseAllGuisPacket> type() {
        return TYPE;
    }
}

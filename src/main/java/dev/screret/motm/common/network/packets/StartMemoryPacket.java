package dev.screret.motm.common.network.packets;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.common.memory.MemoryHandler;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record StartMemoryPacket(BlockPos pos, Holder<Memory> memory) implements CustomPacketPayload {

    // spotless:off
    public static final ResourceLocation ID = MOTMUtil.id("start_memory");
    public static final Type<StartMemoryPacket> TYPE = new Type<>(ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, StartMemoryPacket> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, StartMemoryPacket::pos,
            Memory.STREAM_CODEC, StartMemoryPacket::memory,
            StartMemoryPacket::new
    );
    // spotless:on

    public void execute(IPayloadContext context) {
        Player player = context.player();
        // on client
        MemoryHandler.startMemory(this.pos, this.memory, player);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

package dev.screret.motm.common.memory;

import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.common.network.packets.StartMemoryPacket;
import dev.screret.motm.common.util.ClientBouncer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class MemoryHandler {

    public static void startMemory(BlockPos pos, Holder<Memory> memory, Player player) {
        if (player.level().isClientSide()) {
            ClientBouncer.startClientMemory(pos, memory);
        } else if (player instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new StartMemoryPacket(pos, memory));
        }
    }
}

package dev.screret.mui.api;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * A function that can write any data to an {@link FriendlyByteBuf}.
 */
public interface IPacketWriter<B extends ByteBuf> {

    /**
     * Writes any data to a packet buffer
     *
     * @param buffer buffer to write to
     */
    void write(B buffer);
}

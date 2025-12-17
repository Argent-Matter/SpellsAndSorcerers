package dev.screret.motm.common.util;

import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.client.memory.MemoryRenderer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;

import org.jetbrains.annotations.ApiStatus;

/**
 * Do not use any of these methods directly, as they'll crash on a server!<br>
 * Check the methods' references and use those instead.
 */
public final class ClientBouncer {

    @ApiStatus.Internal
    public static void startClientMemory(BlockPos pos, Holder<Memory> memory) {
        MemoryRenderer.INSTANCE.startMemory(pos, memory);
    }
}

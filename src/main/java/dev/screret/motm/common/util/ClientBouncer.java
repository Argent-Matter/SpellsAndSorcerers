package dev.screret.motm.common.util;

import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.client.memory.MemoryRenderer;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelWriter;

import org.jetbrains.annotations.ApiStatus;

/**
 * Do not use any of these methods directly, as they'll crash on a server!<br>
 * Check the methods' references and use those instead.
 */
@ApiStatus.Internal
public final class ClientBouncer {

    public static void startClientMemory(BlockPos pos, Holder<Memory> memory) {
        MemoryRenderer.INSTANCE.startMemory(pos, memory);
    }

    public static void addEntity(LevelWriter level, Entity entity) {
        if (level instanceof ClientLevel clientLevel) {
            clientLevel.addEntity(entity);
        } else {
            level.addFreshEntity(entity);
        }
    }
}

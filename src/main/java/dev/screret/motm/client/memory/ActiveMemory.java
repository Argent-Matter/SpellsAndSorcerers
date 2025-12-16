package dev.screret.motm.client.memory;

import dev.screret.motm.api.memory.Memory;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

public record ActiveMemory(Holder<Memory> memory, Map<String, ? extends LivingEntity> entities) {

    public void startMemory(BlockPos pos) {
        MemoryRenderer.INSTANCE.startMemory(pos, this);
    }
}

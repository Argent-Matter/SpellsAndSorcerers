package dev.screret.motm.data.memory;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.memory.animation.MemoryKeyframe;
import dev.screret.motm.api.registry.MOTMRegistries;
import dev.screret.motm.common.memory.animation.keyframe.RenderStructureKeyframe;

import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MOTMMemoryAnimationKeyframeTypes {

    // spotless:off
    public static final DeferredRegister<DataComponentType<? extends MemoryKeyframe>> MEMORY_ANIMATION_KEYFRAME_TYPES = DeferredRegister.create(MOTMRegistries.MEMORY_ANIMATION_KEYFRAME_TYPE_REGISTRY, MagicOfTheMind.MOD_ID);

    public static final DeferredHolder<DataComponentType<? extends MemoryKeyframe>, DataComponentType<RenderStructureKeyframe>> RENDER_STRUCTURE = MEMORY_ANIMATION_KEYFRAME_TYPES.register("render_structure",
            () -> DataComponentType.<RenderStructureKeyframe>builder()
                    .persistent(RenderStructureKeyframe.CODEC).networkSynchronized(RenderStructureKeyframe.STREAM_CODEC)
                    .build());

    // spotless:on
}

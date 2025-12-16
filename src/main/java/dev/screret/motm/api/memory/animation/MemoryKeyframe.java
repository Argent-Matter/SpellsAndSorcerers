package dev.screret.motm.api.memory.animation;

import dev.screret.motm.api.memory.Memory;

import software.bernie.geckolib.animation.keyframe.event.CustomInstructionKeyframeEvent;

import org.jetbrains.annotations.NotNull;

public interface MemoryKeyframe {

    void handleEvent(@NotNull CustomInstructionKeyframeEvent<Memory> event, long animatableId);
}

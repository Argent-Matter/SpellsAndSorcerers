package dev.screret.motm.api.memory.animation;

import dev.screret.motm.api.memory.Memory;

import software.bernie.geckolib.animation.keyframe.event.CustomInstructionKeyframeEvent;

public interface MemoryKeyframe {

    void handleEvent(CustomInstructionKeyframeEvent<Memory> event, long animatableId);
}

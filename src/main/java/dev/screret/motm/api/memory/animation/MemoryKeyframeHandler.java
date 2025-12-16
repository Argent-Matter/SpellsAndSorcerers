package dev.screret.motm.api.memory.animation;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.commands.arguments.ComponentParser;
import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.api.registry.MOTMRegistries;

import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.keyframe.event.CustomInstructionKeyframeEvent;
import software.bernie.geckolib.animation.keyframe.event.data.CustomInstructionKeyframeData;

import net.minecraft.core.component.DataComponentPatch;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class MemoryKeyframeHandler implements AnimationController.CustomKeyframeHandler<Memory> {

    public static final ComponentParser.DataComponents<? extends MemoryKeyframe> PARSER = ComponentParser.noContextDataComponents(
            MOTMRegistries.MEMORY_ANIMATION_KEYFRAME_TYPES, false, false);

    private final long animatableId;

    public MemoryKeyframeHandler(long animatableId) {
        this.animatableId = animatableId;
    }

    @Override
    public void handle(CustomInstructionKeyframeEvent<Memory> event) {
        CustomInstructionKeyframeData data = event.getKeyframeData();

        DataComponentPatch instructions;
        try {
            instructions = PARSER.parse(new StringReader(data.getInstructions()));
        } catch (CommandSyntaxException error) {
            MagicOfTheMind.LOGGER.error("Keyframe instruction {} at tick {} in animation {} is invalid",
                    data.getInstructions(), data.getStartTick(), event.getAnimatable(), error);
            return;
        }
        for (var entry : instructions.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }
            MemoryKeyframe keyframe = (MemoryKeyframe) entry.getValue().get();
            keyframe.handleEvent(event, this.animatableId);
        }
    }
}

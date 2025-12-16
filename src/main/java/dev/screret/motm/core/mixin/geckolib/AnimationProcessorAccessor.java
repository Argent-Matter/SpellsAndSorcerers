package dev.screret.motm.core.mixin.geckolib;

import software.bernie.geckolib.animation.AnimationProcessor;
import software.bernie.geckolib.cache.object.GeoBone;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = AnimationProcessor.class, remap = false)
public interface AnimationProcessorAccessor {

    @Accessor("bones")
    Map<String, GeoBone> motm$getBones();
}

package dev.screret.motm.core.mixin.geckolib;

import software.bernie.geckolib.animation.AnimatableManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AnimatableManager.class, remap = false)
public interface AnimatableManagerAccessor {

    @Invoker("finishFirstTick")
    void motm$finishFirstTick();
}

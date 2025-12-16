package dev.screret.motm.core.mixin.geckolib;

import software.bernie.geckolib.animation.AnimationController;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = AnimationController.class, remap = false)
public interface AnimationControllerAccessor {

    @Accessor("isJustStarting")
    void motm$setIsJustStarting(boolean isJustStarting);

    @Invoker("adjustTick")
    double motm$adjustTick(double tick);
}

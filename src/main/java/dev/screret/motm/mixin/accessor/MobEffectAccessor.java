package dev.screret.motm.mixin.accessor;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MobEffect.class)
public interface MobEffectAccessor {

    @Invoker("<init>")
    static MobEffect callInit(MobEffectCategory category, int color) {
        throw new AssertionError();
    }
}

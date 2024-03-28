package screret.sas.mixin;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MobEffect.class)
public interface MobEffectAccessor {
    @Invoker("<init>")
    static MobEffect callInit(MobEffectCategory pCategory, int pColor) {
        throw new AssertionError();
    }
}

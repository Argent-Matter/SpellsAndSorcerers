package screret.sas.alchemy.effect;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import screret.sas.SpellsAndSorcerers;
import screret.sas.attributes.ModAttributes;
import screret.sas.mixin.MobEffectAccessor;

import java.util.function.Supplier;

public class ModMobEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, SpellsAndSorcerers.MODID);

    public static final Supplier<MobEffect> MANA = EFFECTS.register("mana", () -> MobEffectAccessor.callInit(MobEffectCategory.BENEFICIAL, 0x00e180)
            .addAttributeModifier(ModAttributes.MANA.get(), "0aa4b6da-5b3c-47a5-a77c-8a3c0e8f5b2d", 25, AttributeModifier.Operation.ADDITION));
}

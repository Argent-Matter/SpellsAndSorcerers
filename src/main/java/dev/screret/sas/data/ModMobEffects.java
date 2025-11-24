package dev.screret.sas.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import dev.screret.sas.SpellsAndSorcerers;
import dev.screret.sas.Util;
import dev.screret.sas.mixin.accessor.MobEffectAccessor;

import java.util.function.Supplier;

public class ModMobEffects {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, SpellsAndSorcerers.MODID);

    public static final Supplier<MobEffect> MANA = EFFECTS.register("mana", () -> MobEffectAccessor.callInit(MobEffectCategory.BENEFICIAL, 0x00e180)
            .addAttributeModifier(ModAttributes.MANA, Util.id("mana_potion"), 25, AttributeModifier.Operation.ADD_VALUE));
}

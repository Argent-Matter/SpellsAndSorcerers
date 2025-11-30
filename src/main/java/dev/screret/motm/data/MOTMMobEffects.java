package dev.screret.motm.data;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.mixin.accessor.MobEffectAccessor;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MOTMMobEffects {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT,
            MagicOfTheMind.MODID);

    public static final DeferredHolder<MobEffect, MobEffect> MANA = EFFECTS.register("mana",
            () -> MobEffectAccessor.callInit(MobEffectCategory.BENEFICIAL, 0x00e180)
                    .addAttributeModifier(MOTMAttributes.MANA, MOTMUtil.id("mana_potion"), 25,
                            AttributeModifier.Operation.ADD_VALUE));
}

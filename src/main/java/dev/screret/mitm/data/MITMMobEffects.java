package dev.screret.mitm.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.mixin.accessor.MobEffectAccessor;

import java.util.function.Supplier;

public class MITMMobEffects {

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, MagicOfTheMind.MODID);

    public static final Supplier<MobEffect> MANA = EFFECTS.register("mana", () -> MobEffectAccessor.callInit(MobEffectCategory.BENEFICIAL, 0x00e180)
            .addAttributeModifier(MITMAttributes.MANA, MITMUtil.id("mana_potion"), 25, AttributeModifier.Operation.ADD_VALUE));
}

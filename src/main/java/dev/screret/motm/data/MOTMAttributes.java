package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MOTMAttributes {

    // spotless:off
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, MagicOfTheMind.MOD_ID);

    public static final DeferredHolder<Attribute, Attribute> MANA = ATTRIBUTES.register("generic.mana", () -> new RangedAttribute("attribute.name.motm.generic.mana", 100, 0.0, 1024.0).setSyncable(true));

    // spotless:on
}

package dev.screret.mitm.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import dev.screret.mitm.MagicOfTheMind;

public class MITMAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, MagicOfTheMind.MODID);

    public static final DeferredHolder<Attribute, Attribute> MANA = ATTRIBUTES.register("generic.mana",
            () -> new RangedAttribute("attribute.name.generic.mana", 100, 0.0, 1024.0).setSyncable(true));
}

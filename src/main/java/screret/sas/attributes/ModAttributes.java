package screret.sas.attributes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;
import screret.sas.SpellsAndSorcerers;

import java.util.function.Supplier;

public class ModAttributes {
    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(BuiltInRegistries.ATTRIBUTE, SpellsAndSorcerers.MODID);

    public static final Supplier<Attribute> MANA = ATTRIBUTES.register("generic.mana", () -> new RangedAttribute("attribute.name.generic.mana", 100, 0.0, 1024.0).setSyncable(true));
}

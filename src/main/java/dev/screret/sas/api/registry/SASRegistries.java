package dev.screret.sas.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

import dev.screret.sas.Util;
import dev.screret.sas.api.wand.ability.WandAbility;

public class SASRegistries {

    public static final ResourceKey<Registry<WandAbility>> WAND_ABILITY_REGISTRY = ResourceKey.createRegistryKey(Util.id("wand_abilities"));
    public static final Registry<WandAbility> WAND_ABILITIES = new RegistryBuilder<>(WAND_ABILITY_REGISTRY)
            .defaultKey(Util.id("dummy"))
            .create();

    public static void init() {}
}

package dev.screret.motm.api.registry;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.api.ability.WandAbility;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class MOTMRegistries {

    public static final ResourceKey<Registry<WandAbility<?>>> WAND_ABILITY_REGISTRY = ResourceKey
            .createRegistryKey(MOTMUtil.id("wand_abilities"));
    public static final Registry<WandAbility<?>> WAND_ABILITIES = new RegistryBuilder<>(WAND_ABILITY_REGISTRY)
            .defaultKey(MOTMUtil.id("dummy"))
            .create();
}

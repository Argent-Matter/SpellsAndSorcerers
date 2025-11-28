package dev.screret.mitm.api.registry;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.api.ability.WandAbility;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

public class MITMRegistries {

    public static final ResourceKey<Registry<WandAbility<?>>> WAND_ABILITY_REGISTRY = ResourceKey
            .createRegistryKey(MITMUtil.id("wand_abilities"));
    public static final Registry<WandAbility<?>> WAND_ABILITIES = new RegistryBuilder<>(WAND_ABILITY_REGISTRY)
            .defaultKey(MITMUtil.id("dummy"))
            .create();
}

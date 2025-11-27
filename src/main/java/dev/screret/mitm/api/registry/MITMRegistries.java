package dev.screret.mitm.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.RegistryBuilder;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.api.wand.ability.WandAbility;

public class MITMRegistries {

    public static final ResourceKey<Registry<WandAbility>> WAND_ABILITY_REGISTRY = ResourceKey.createRegistryKey(MITMUtil.id("wand_abilities"));
    public static final Registry<WandAbility> WAND_ABILITIES = new RegistryBuilder<>(WAND_ABILITY_REGISTRY)
            .defaultKey(MITMUtil.id("dummy"))
            .create();
}

package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.world.generation.DeferringHeightProvider;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MOTMHeightProviders {

    // spotless:off
    public static final DeferredRegister<HeightProviderType<?>> HEIGHT_PROVIDER_TYPES = DeferredRegister.create(BuiltInRegistries.HEIGHT_PROVIDER_TYPE, MagicOfTheMind.MOD_ID);

    public static final DeferredHolder<HeightProviderType<?>, HeightProviderType<DeferringHeightProvider>> DEFERRING = HEIGHT_PROVIDER_TYPES.register("deferring", () -> () -> DeferringHeightProvider.CODEC);

    // spotless:on
}

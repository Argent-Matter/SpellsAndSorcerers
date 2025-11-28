package dev.screret.mitm.data;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.api.ability.WandAbilityInstance;
import dev.screret.mitm.common.item.component.WandComponent;

public class MITMDataComponents {

    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, MagicOfTheMind.MODID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WandComponent>> WAND = DATA_COMPONENTS.register("wand",
            () -> DataComponentType.<WandComponent>builder()
                    .persistent(WandComponent.CODEC).networkSynchronized(WandComponent.STREAM_CODEC)
                    .build());
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WandAbilityInstance>> WAND_CORE = DATA_COMPONENTS.register("wand_core",
            () -> DataComponentType.<WandAbilityInstance>builder()
                    .persistent(WandAbilityInstance.CODEC).networkSynchronized(WandAbilityInstance.STREAM_CODEC)
                    .build());
}

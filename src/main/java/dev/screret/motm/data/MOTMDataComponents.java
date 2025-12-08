package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.ability.WandAbilityInstance;
import dev.screret.motm.common.item.component.WandComponent;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MOTMDataComponents {

    // spotless:off
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MagicOfTheMind.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WandComponent>> WAND = DATA_COMPONENTS.registerComponentType("wand",
            builder -> builder.persistent(WandComponent.CODEC).networkSynchronized(WandComponent.STREAM_CODEC));
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<WandAbilityInstance>> WAND_CORE = DATA_COMPONENTS.registerComponentType("wand_core",
                    builder -> builder.persistent(WandAbilityInstance.CODEC).networkSynchronized(WandAbilityInstance.STREAM_CODEC));
    // spotless:on
}

package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.item.component.PortStoneRunes;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MOTMDataComponents {

    // spotless:off
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MagicOfTheMind.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PortStoneRunes>> PORT_STONE_RUNES = DATA_COMPONENTS.registerComponentType("port_stone_runes",
            builder -> builder.persistent(PortStoneRunes.CODEC).networkSynchronized(PortStoneRunes.STREAM_CODEC).cacheEncoding());
    // spotless:on
}

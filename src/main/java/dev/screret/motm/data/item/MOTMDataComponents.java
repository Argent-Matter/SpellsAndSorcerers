package dev.screret.motm.data.item;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.common.item.component.PortStoneRunes;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MOTMDataComponents {

    // spotless:off
    public static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MagicOfTheMind.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<PortStoneRunes>> PORT_STONE_RUNES = DATA_COMPONENTS.registerComponentType("port_stone_runes",
            builder -> builder.persistent(PortStoneRunes.CODEC).networkSynchronized(PortStoneRunes.STREAM_CODEC).cacheEncoding());

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Holder<Memory>>> CONTAINED_MEMORY = DATA_COMPONENTS.registerComponentType("contained_memory",
            builder -> builder.persistent(Memory.CODEC).networkSynchronized(Memory.STREAM_CODEC).cacheEncoding());
    // spotless:on
}

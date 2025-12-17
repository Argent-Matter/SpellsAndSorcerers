package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import com.google.common.collect.ImmutableSet;

import java.util.Set;
import java.util.function.Supplier;

public class MOTMPoiTypes {

    // spotless:off
    public static final DeferredRegister<PoiType> POINT_OF_INTEREST_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, MagicOfTheMind.MOD_ID);

    public static final DeferredHolder<PoiType, PoiType> PORT_STONE = POINT_OF_INTEREST_TYPES.register("port_stone", () -> new PoiType(getBlockStates(MOTMBlocks.PORT_STONE), 0, 1));

    // spotless:on

    private static Set<BlockState> getBlockStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

    private static Set<BlockState> getBlockStates(Supplier<? extends Block> block) {
        return getBlockStates(block.get());
    }
}

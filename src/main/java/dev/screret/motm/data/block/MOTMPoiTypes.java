package dev.screret.motm.data.block;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.block.PortStoneBlock;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MOTMPoiTypes {

    // spotless:off
    public static final DeferredRegister<PoiType> POINT_OF_INTEREST_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, MagicOfTheMind.MOD_ID);

    public static final DeferredHolder<PoiType, PoiType> PORT_STONE = POINT_OF_INTEREST_TYPES.register("port_stone",
            () -> new PoiType(getBlockStates(MOTMBlocks.PORT_STONE, state -> state.getValue(PortStoneBlock.PART) == PortStoneBlock.Part.BOTTOM), 0, 1));

    // spotless:on

    private static Set<BlockState> getBlockStates(Block block) {
        // no need to care about immutability, the contents are copied to another set in PoiType.new anyway.
        return new HashSet<>(block.getStateDefinition().getPossibleStates());
    }

    private static Set<BlockState> getBlockStates(Supplier<? extends Block> block) {
        return getBlockStates(block.get());
    }

    private static Set<BlockState> getBlockStates(Block block, Predicate<BlockState> filter) {
        return block.getStateDefinition().getPossibleStates().stream().filter(filter).collect(Collectors.toSet());
    }

    private static Set<BlockState> getBlockStates(Supplier<? extends Block> block, Predicate<BlockState> filter) {
        return getBlockStates(block.get(), filter);
    }
}

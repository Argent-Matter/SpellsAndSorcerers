package dev.screret.motm.data.block;

import dev.screret.motm.common.data.util.BlockFamily;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class MOTMBlockFamilies {

    private static final Map<Holder<Block>, BlockFamily> MAP = new IdentityHashMap<>();

    public static final BlockFamily MEMORYSTONE = familyBuilder(MOTMBlocks.MEMORYSTONE)
            .wall(MOTMBlocks.MEMORYSTONE_WALL)
            .stairs(MOTMBlocks.MEMORYSTONE_STAIRS)
            .slab(MOTMBlocks.MEMORYSTONE_SLAB)
            .polished(MOTMBlocks.POLISHED_MEMORYSTONE)
            .build();
    public static final BlockFamily POLISHED_MEMORYSTONE = familyBuilder(MOTMBlocks.POLISHED_MEMORYSTONE)
            .wall(MOTMBlocks.POLISHED_MEMORYSTONE_WALL)
            .pressurePlate(MOTMBlocks.POLISHED_MEMORYSTONE_PRESSURE_PLATE)
            .button(MOTMBlocks.POLISHED_MEMORYSTONE_BUTTON)
            .stairs(MOTMBlocks.POLISHED_MEMORYSTONE_STAIRS)
            .slab(MOTMBlocks.POLISHED_MEMORYSTONE_SLAB)
            .polished(MOTMBlocks.POLISHED_MEMORYSTONE_BRICKS)
            .chiseled(MOTMBlocks.CHISELED_POLISHED_MEMORYSTONE)
            .build();
    public static final BlockFamily POLISHED_MEMORYSTONE_BRICKS = familyBuilder(MOTMBlocks.POLISHED_MEMORYSTONE_BRICKS)
            .wall(MOTMBlocks.POLISHED_MEMORYSTONE_BRICK_WALL)
            .stairs(MOTMBlocks.POLISHED_MEMORYSTONE_BRICK_STAIRS)
            .slab(MOTMBlocks.POLISHED_MEMORYSTONE_BRICK_SLAB)
            .cracked(MOTMBlocks.CRACKED_POLISHED_MEMORYSTONE_BRICKS)
            .build();

    private static BlockFamily.Builder familyBuilder(Holder<Block> baseBlock) {
        BlockFamily.Builder builder = new BlockFamily.Builder(baseBlock);
        BlockFamily blockfamily = MAP.put(baseBlock, builder.family());

        if (blockfamily != null) {
            throw new IllegalStateException("Duplicate family definition for " + baseBlock.getRegisteredName());
        } else {
            return builder;
        }
    }

    public static Stream<BlockFamily> getAllFamilies() {
        return MAP.values().stream();
    }
}

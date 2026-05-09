package dev.screret.motm.data.worldgen;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.data.block.MOTMBlocks;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;

import com.google.common.collect.ImmutableList;

public class MOTMProcessorLists {

    public static final ResourceKey<StructureProcessorList> DEGRADE_MEMORYSTONE = createKey("degrade_memorystone");

    public static void bootstrap(BootstrapContext<StructureProcessorList> context) {
        HolderGetter<Block> blockRegistry = context.lookup(Registries.BLOCK);

        // spotless:off

        register(context, DEGRADE_MEMORYSTONE,
                makeDegradeProcessor(MOTMBlocks.POLISHED_MEMORYSTONE_BRICKS.get(), MOTMBlocks.CRACKED_POLISHED_MEMORYSTONE_BRICKS.get(), 0.35f, 0.15f),
                makeDegradeProcessor(MOTMBlocks.MEMORYSTONE.get(), MOTMBlocks.UNAWAKENED_MEMORYSTONE.get(), 0.5f, 0.01f),
                makeDegradeProcessor(MOTMBlocks.MEMORYSTONE_STAIRS.get(), MOTMBlocks.UNAWAKENED_MEMORYSTONE_STAIRS.get(), 0.5f, 0.01f),
                makeDegradeProcessor(MOTMBlocks.MEMORYSTONE_SLAB.get(), MOTMBlocks.UNAWAKENED_MEMORYSTONE_SLAB.get(), 0.5f, 0.01f),
                makeDegradeProcessor(MOTMBlocks.MEMORYSTONE_WALL.get(), MOTMBlocks.UNAWAKENED_MEMORYSTONE_WALL.get(), 0.5f, 0.01f)
        );

        // spotless:on
    }

    // region utility functions

    private static void register(BootstrapContext<StructureProcessorList> context, ResourceKey<StructureProcessorList> key,
                                 StructureProcessor... processors) {
        context.register(key, new StructureProcessorList(ImmutableList.copyOf(processors)));
    }

    private static ResourceKey<StructureProcessorList> createKey(String name) {
        return ResourceKey.create(Registries.PROCESSOR_LIST, MOTMUtil.id(name));
    }

    private static RuleProcessor makeDegradeProcessor(Block originalBlock, Block degradedBlock,
                                                      float degradeChance, float unDegradeChance) {
        // spotless:off
        return new RuleProcessor(ImmutableList.of(
                new ProcessorRule(
                        new RandomBlockMatchTest(originalBlock, degradeChance),
                        AlwaysTrueTest.INSTANCE,
                        degradedBlock.defaultBlockState()
                ),
                new ProcessorRule(
                        new RandomBlockMatchTest(degradedBlock, unDegradeChance),
                        AlwaysTrueTest.INSTANCE,
                        originalBlock.defaultBlockState()
                )
        ));
        // spotless:on
    }

    // endregion
}

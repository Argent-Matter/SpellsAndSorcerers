package dev.screret.motm.common.data.provider.tag;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.data.MOTMBlocks;
import dev.screret.motm.data.MOTMTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.Nullable;

public class MOTMBlockTagsProvider extends BlockTagsProvider {

    public MOTMBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                 @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MagicOfTheMind.MOD_ID, existingFileHelper);
    }

    // spotless:off
    protected void addTags(HolderLookup.Provider registries) {
        tag(MOTMTags.Blocks.GLINT_ORES).add(MOTMBlocks.GLINT_ORE.get());
        tag(MOTMTags.Blocks.SOULSTEEL_BLOCKS).add(MOTMBlocks.SOULSTEEL_BLOCK.get());

        tag(Tags.Blocks.NEEDS_NETHERITE_TOOL).add(
                MOTMBlocks.GLINT_ORE.get()
        );
        tag(BlockTags.NEEDS_DIAMOND_TOOL).add(
                MOTMBlocks.MEMORYSTONE.get(),
                MOTMBlocks.UNAWAKENED_MEMORYSTONE.get(),
                MOTMBlocks.PORT_STONE.get()
        );

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(
                MOTMBlocks.GLINT_ORE.get(),
                MOTMBlocks.MEMORYSTONE.get(),
                MOTMBlocks.UNAWAKENED_MEMORYSTONE.get(),
                MOTMBlocks.PORT_STONE.get()
        );
    }
    // spotless:on
}

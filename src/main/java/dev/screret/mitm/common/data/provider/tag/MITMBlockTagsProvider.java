package dev.screret.mitm.common.data.provider.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import dev.screret.mitm.data.MITMTags;
import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.data.MITMBlocks;

import java.util.concurrent.CompletableFuture;

public class MITMBlockTagsProvider extends BlockTagsProvider {

    public MITMBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MagicOfTheMind.MODID, existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider pProvider) {
        tag(MITMTags.Blocks.GLINT_ORES).add(MITMBlocks.GLINT_ORE.get());
        tag(MITMTags.Blocks.SOULSTEEL_BLOCKS).add(MITMBlocks.SOULSTEEL_BLOCK.get());

        tag(Tags.Blocks.NEEDS_NETHERITE_TOOL).add(MITMBlocks.GLINT_ORE.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(MITMBlocks.GLINT_ORE.get());
    }
}

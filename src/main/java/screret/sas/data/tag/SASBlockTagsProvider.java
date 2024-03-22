package screret.sas.data.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import screret.sas.ModTags;
import screret.sas.SpellsAndSorcerers;
import screret.sas.block.ModBlocks;

import java.util.concurrent.CompletableFuture;

public class SASBlockTagsProvider extends BlockTagsProvider {

    public SASBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, SpellsAndSorcerers.MODID, existingFileHelper);
    }

    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ModTags.Blocks.GLINT_ORES).add(ModBlocks.GLINT_ORE.get());
        tag(ModTags.Blocks.SOULSTEEL_BLOCKS).add(ModBlocks.SOULSTEEL_BLOCK.get());

        tag(Tags.Blocks.NEEDS_NETHERITE_TOOL).add(ModBlocks.GLINT_ORE.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.GLINT_ORE.get());
    }
}

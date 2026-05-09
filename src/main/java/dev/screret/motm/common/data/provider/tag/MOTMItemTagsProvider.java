package dev.screret.motm.common.data.provider.tag;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.data.item.MOTMItems;
import dev.screret.motm.data.MOTMTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class MOTMItemTagsProvider extends ItemTagsProvider {

    public MOTMItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, MagicOfTheMind.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(MOTMTags.Items.GLASS_BOTTLES).add(Items.GLASS_BOTTLE);
        tag(MOTMTags.Items.GLINT_ORES).add(MOTMItems.GLINT_ORE.get());
        tag(MOTMTags.Items.GLINT_GEMS).add(MOTMItems.GLINT.get());
        tag(MOTMTags.Items.SOULSTEEL_INGOTS).add(MOTMItems.SOULSTEEL_INGOT.get());
        tag(MOTMTags.Items.SOULSTEEL_BLOCKS).add(MOTMItems.SOULSTEEL_BLOCK.get());
        tag(MOTMTags.Items.SOULSTEEL_NUGGETS).add(MOTMItems.SOULSTEEL_NUGGET.get());
    }
}

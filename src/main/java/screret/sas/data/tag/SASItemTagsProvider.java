package screret.sas.data.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import screret.sas.ModTags;
import screret.sas.SpellsAndSorcerers;
import screret.sas.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class SASItemTagsProvider extends ItemTagsProvider {

    public SASItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, TagsProvider<Block> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, SpellsAndSorcerers.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ModTags.Items.GLASS_BOTTLES).add(Items.GLASS_BOTTLE);
        tag(ModTags.Items.BOSS_SUMMON_ITEMS).add(ModItems.WAND_CORE.get(), Items.GLOWSTONE_DUST, Items.LAPIS_BLOCK, Items.ZOMBIE_HEAD);
        tag(ModTags.Items.GLINT_ORES).add(ModItems.GLINT_ORE.get());
        tag(ModTags.Items.GLINT_GEMS).add(ModItems.GLINT.get());
        tag(ModTags.Items.SOULSTEEL_INGOTS).add(ModItems.SOULSTEEL_INGOT.get());
        tag(ModTags.Items.SOULSTEEL_BLOCKS).add(ModItems.SOULSTEEL_BLOCK.get());
        tag(ModTags.Items.SOULSTEEL_NUGGETS).add(ModItems.SOULSTEEL_NUGGET.get());
    }
}

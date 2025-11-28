package dev.screret.mitm.common.data.provider.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import dev.screret.mitm.data.MITMTags;
import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.data.MITMItems;

import java.util.concurrent.CompletableFuture;

public class MITMItemTagsProvider extends ItemTagsProvider {

    public MITMItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTagProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider, MagicOfTheMind.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider registries) {
        tag(MITMTags.Items.GLASS_BOTTLES).add(Items.GLASS_BOTTLE);
        tag(MITMTags.Items.BOSS_SUMMON_ITEMS).add(MITMItems.WAND_CORE.get(), Items.GLOWSTONE_DUST, Items.LAPIS_BLOCK, Items.ZOMBIE_HEAD);
        tag(MITMTags.Items.GLINT_ORES).add(MITMItems.GLINT_ORE.get());
        tag(MITMTags.Items.GLINT_GEMS).add(MITMItems.GLINT.get());
        tag(MITMTags.Items.SOULSTEEL_INGOTS).add(MITMItems.SOULSTEEL_INGOT.get());
        tag(MITMTags.Items.SOULSTEEL_BLOCKS).add(MITMItems.SOULSTEEL_BLOCK.get());
        tag(MITMTags.Items.SOULSTEEL_NUGGETS).add(MITMItems.SOULSTEEL_NUGGET.get());

        tag(MITMTags.Items.ENCHANTABLE_WITH_QUICK_CHARGE).addTag(ItemTags.CROSSBOW_ENCHANTABLE).add(MITMItems.WAND.get());
        tag(MITMTags.Items.ENCHANTABLE_WITH_POWER).addTag(ItemTags.BOW_ENCHANTABLE).add(MITMItems.WAND.get());
    }
}

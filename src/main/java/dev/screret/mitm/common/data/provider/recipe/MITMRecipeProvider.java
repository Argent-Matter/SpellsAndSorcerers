package dev.screret.mitm.common.data.provider.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import dev.screret.mitm.data.MITMTags;
import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.data.MITMItems;

import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

public class MITMRecipeProvider extends RecipeProvider {

    public MITMRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput provider) {
        MITMWandRecipes.buildRecipes(provider);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MITMItems.WAND_TABLE.get())
                .define('B', Items.BLAZE_POWDER)
                .define('#', Blocks.END_STONE_BRICKS)
                .define('D', Items.EMERALD)
                .pattern(" B ")
                .pattern("D#D")
                .pattern("###")
                .unlockedBy("has_endstone", has(Blocks.END_STONE))
                .unlockedBy("has_wand_core", has(MITMItems.WAND_CORE.get()))
                .unlockedBy("has_wand", has(MITMItems.WAND.get()))
                .save(provider, MITMUtil.id("wand_table"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MITMItems.SOULSTEEL_INGOT.get())
                .requires(MITMTags.Items.GLINT_GEMS)
                .requires(MITMTags.Items.GLINT_GEMS)
                .requires(MITMItems.SOUL_BOTTLE.get(), 2)
                .group("soulsteel_ingot")
                .unlockedBy("has_glint", has(MITMItems.GLINT.get()))
                .save(provider);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MITMItems.PALANTIR.get())
                .define('E', MITMItems.CTHULHU_EYE.get())
                .define('G', Tags.Items.GLASS_BLOCKS_TINTED)
                .define('B', Items.POLISHED_BLACKSTONE_BRICKS)
                .pattern("GGG")
                .pattern("GEG")
                .pattern("BBB")
                .unlockedBy("has_eye", has(MITMItems.CTHULHU_EYE.get()))
                .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS_TINTED))
                .save(provider);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MITMItems.SOULSTEEL_BOOTS.get()).define('X', MITMTags.Items.SOULSTEEL_INGOTS).pattern("X X").pattern("X X").unlockedBy("has_diamond", has(MITMTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MITMItems.SOULSTEEL_CHESTPLATE.get()).define('X', MITMTags.Items.SOULSTEEL_INGOTS).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_diamond", has(MITMTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MITMItems.SOULSTEEL_HELMET.get()).define('X', MITMTags.Items.SOULSTEEL_INGOTS).pattern("XXX").pattern("X X").unlockedBy("has_diamond", has(MITMTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MITMItems.SOULSTEEL_HOE.get()).define('#', MITMItems.HANDLE.get()).define('X', MITMTags.Items.SOULSTEEL_INGOTS).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_diamond", has(MITMTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MITMItems.SOULSTEEL_LEGGINGS.get()).define('X', MITMTags.Items.SOULSTEEL_INGOTS).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_diamond", has(MITMTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MITMItems.SOULSTEEL_PICKAXE.get()).define('#', MITMItems.HANDLE.get()).define('X', MITMTags.Items.SOULSTEEL_INGOTS).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_diamond", has(MITMTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MITMItems.SOULSTEEL_SHOVEL.get()).define('#', MITMItems.HANDLE.get()).define('X', MITMTags.Items.SOULSTEEL_INGOTS).pattern("X").pattern("#").pattern("#").unlockedBy("has_diamond", has(MITMTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MITMItems.SOULSTEEL_SWORD.get()).define('#', MITMItems.HANDLE.get()).define('X', MITMTags.Items.SOULSTEEL_INGOTS).pattern("X").pattern("X").pattern("#").unlockedBy("has_diamond", has(MITMTags.Items.SOULSTEEL_INGOTS)).save(provider);


        nineBlockStorageRecipesRecipesWithCustomUnpacking(provider, MITMTags.Items.SOULSTEEL_INGOTS, MITMItems.SOULSTEEL_INGOT.get(), MITMTags.Items.SOULSTEEL_BLOCKS, MITMItems.SOULSTEEL_BLOCK.get(), MITMUtil.id("soulsteel_ingot_from_soulsteel_block"), "soulsteel_ingot");
        nineBlockStorageRecipesWithCustomPacking(provider, MITMTags.Items.SOULSTEEL_NUGGETS, MITMItems.SOULSTEEL_NUGGET.get(), MITMTags.Items.SOULSTEEL_INGOTS, MITMItems.SOULSTEEL_INGOT.get(), MITMUtil.id("soulsteel_ingot_from_nuggets"), "soulsteel_ingot");
        oreSmelting(Ingredient.of(MITMTags.Items.GLINT_ORES), MITMItems.GLINT.get(), 1.5F, 200);
        oreBlasting(Ingredient.of(MITMTags.Items.GLINT_ORES), MITMItems.GLINT.get(), 1.5F, 100);

    }

    protected static void nineBlockStorageRecipesWithCustomPacking(RecipeOutput provider, TagKey<Item> pUnpacked, ItemLike unpackedResult, TagKey<Item> pPacked, ItemLike packedResult, ResourceLocation pPackingRecipeName, String pPackingRecipeGroup) {
        nineBlockStorageRecipes(provider, pUnpacked, unpackedResult, pPacked, packedResult, pPackingRecipeName, pPackingRecipeGroup, getItemLocation(unpackedResult), null);
    }

    protected static void nineBlockStorageRecipesRecipesWithCustomUnpacking(RecipeOutput provider, TagKey<Item> pUnpacked, ItemLike unpackedResult, TagKey<Item> pPacked, ItemLike packedResult, ResourceLocation pUnpackingRecipeName, String pUnpackingRecipeGroup) {
        nineBlockStorageRecipes(provider, pUnpacked, unpackedResult, pPacked, packedResult, getItemLocation(packedResult), null, pUnpackingRecipeName, pUnpackingRecipeGroup);
    }

    protected static void nineBlockStorageRecipes(RecipeOutput provider, TagKey<Item> pUnpacked, ItemLike unpackedResult, TagKey<Item> pPacked, ItemLike packedResult, ResourceLocation pPackingRecipeName, @Nullable String pPackingRecipeGroup, ResourceLocation pUnpackingRecipeName, @Nullable String pUnpackingRecipeGroup) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, unpackedResult, 9)
                .requires(pPacked)
                .group(pUnpackingRecipeGroup)
                .unlockedBy(getHasName(pPacked), has(pPacked))
                .save(provider, pUnpackingRecipeName);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, packedResult)
                .define('#', pUnpacked)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .group(pPackingRecipeGroup)
                .unlockedBy(getHasName(pUnpacked), has(pUnpacked))
                .save(provider, pPackingRecipeName);
    }

    protected static void oreSmelting(Ingredient ingredient, ItemLike pResult, float pExperience, int pCookingTime) {
        SimpleCookingRecipeBuilder.smelting(ingredient, RecipeCategory.MISC, pResult, pExperience, pCookingTime);
    }

    protected static void oreBlasting(Ingredient ingredient, ItemLike pResult, float pExperience, int pCookingTime) {
        SimpleCookingRecipeBuilder.blasting(ingredient, RecipeCategory.MISC, pResult, pExperience, pCookingTime);
    }

    protected static ResourceLocation getItemLocation(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem());
    }

    protected static String getHasName(TagKey<Item> tag) {
        return "has_" + tag.location();
    }
}

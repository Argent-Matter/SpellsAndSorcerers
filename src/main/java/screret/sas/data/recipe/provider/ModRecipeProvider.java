package screret.sas.data.recipe.provider;

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
import screret.sas.ModTags;
import screret.sas.Util;
import screret.sas.item.ModItems;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {

    public ModRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(RecipeOutput provider) {
        WandRecipeProvider.buildRecipes(provider);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.WAND_TABLE.get())
                .define('B', Items.BLAZE_POWDER)
                .define('#', Blocks.END_STONE_BRICKS)
                .define('D', Items.EMERALD)
                .pattern(" B ")
                .pattern("D#D")
                .pattern("###")
                .unlockedBy("has_endstone", has(Blocks.END_STONE))
                .unlockedBy("has_wand_core", has(ModItems.WAND_CORE.get()))
                .unlockedBy("has_wand", has(ModItems.WAND.get()))
                .save(provider, Util.id("wand_table"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SOULSTEEL_INGOT.get())
                .requires(ModTags.Items.GLINT_GEMS)
                .requires(ModTags.Items.GLINT_GEMS)
                .requires(ModItems.SOUL_BOTTLE.get(), 2)
                .group("soulsteel_ingot")
                .unlockedBy("has_glint", has(ModItems.GLINT.get()))
                .save(provider);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PALANTIR.get())
                .define('E', ModItems.CTHULHU_EYE.get())
                .define('G', Tags.Items.GLASS_TINTED)
                .define('B', Items.POLISHED_BLACKSTONE_BRICKS)
                .pattern("GGG")
                .pattern("GEG")
                .pattern("BBB")
                .unlockedBy("has_eye", has(ModItems.CTHULHU_EYE.get()))
                .unlockedBy("has_glass", has(Tags.Items.GLASS_TINTED))
                .save(provider);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOULSTEEL_BOOTS.get()).define('X', ModTags.Items.SOULSTEEL_INGOTS).pattern("X X").pattern("X X").unlockedBy("has_diamond", has(ModTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOULSTEEL_CHESTPLATE.get()).define('X', ModTags.Items.SOULSTEEL_INGOTS).pattern("X X").pattern("XXX").pattern("XXX").unlockedBy("has_diamond", has(ModTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOULSTEEL_HELMET.get()).define('X', ModTags.Items.SOULSTEEL_INGOTS).pattern("XXX").pattern("X X").unlockedBy("has_diamond", has(ModTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOULSTEEL_HOE.get()).define('#', ModItems.HANDLE.get()).define('X', ModTags.Items.SOULSTEEL_INGOTS).pattern("XX").pattern(" #").pattern(" #").unlockedBy("has_diamond", has(ModTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOULSTEEL_LEGGINGS.get()).define('X', ModTags.Items.SOULSTEEL_INGOTS).pattern("XXX").pattern("X X").pattern("X X").unlockedBy("has_diamond", has(ModTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOULSTEEL_PICKAXE.get()).define('#', ModItems.HANDLE.get()).define('X', ModTags.Items.SOULSTEEL_INGOTS).pattern("XXX").pattern(" # ").pattern(" # ").unlockedBy("has_diamond", has(ModTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOULSTEEL_SHOVEL.get()).define('#', ModItems.HANDLE.get()).define('X', ModTags.Items.SOULSTEEL_INGOTS).pattern("X").pattern("#").pattern("#").unlockedBy("has_diamond", has(ModTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SOULSTEEL_SWORD.get()).define('#', ModItems.HANDLE.get()).define('X', ModTags.Items.SOULSTEEL_INGOTS).pattern("X").pattern("X").pattern("#").unlockedBy("has_diamond", has(ModTags.Items.SOULSTEEL_INGOTS)).save(provider);


        nineBlockStorageRecipesRecipesWithCustomUnpacking(provider, ModTags.Items.SOULSTEEL_INGOTS, ModItems.SOULSTEEL_INGOT.get(), ModTags.Items.SOULSTEEL_BLOCKS, ModItems.SOULSTEEL_BLOCK.get(), Util.id("soulsteel_ingot_from_soulsteel_block"), "soulsteel_ingot");
        nineBlockStorageRecipesWithCustomPacking(provider, ModTags.Items.SOULSTEEL_NUGGETS, ModItems.SOULSTEEL_NUGGET.get(), ModTags.Items.SOULSTEEL_INGOTS, ModItems.SOULSTEEL_INGOT.get(), Util.id("soulsteel_ingot_from_nuggets"), "soulsteel_ingot");
        oreSmelting(Ingredient.of(ModTags.Items.GLINT_ORES), ModItems.GLINT.get(), 1.5F, 200);
        oreBlasting(Ingredient.of(ModTags.Items.GLINT_ORES), ModItems.GLINT.get(), 1.5F, 100);

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

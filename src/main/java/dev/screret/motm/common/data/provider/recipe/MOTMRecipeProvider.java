package dev.screret.motm.common.data.provider.recipe;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.common.data.util.BlockFamily;
import dev.screret.motm.data.MOTMTags;
import dev.screret.motm.data.block.MOTMBlockFamilies;
import dev.screret.motm.data.item.MOTMItems;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

import org.jetbrains.annotations.Nullable;

public class MOTMRecipeProvider extends RecipeProvider {

    public MOTMRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput provider) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, MOTMItems.SOULSTEEL_INGOT.get())
                .requires(MOTMTags.Items.GLINT_GEMS)
                .requires(MOTMTags.Items.GLINT_GEMS)
                .requires(MOTMItems.SOUL_BOTTLE.get(), 2)
                .group("soulsteel_ingot")
                .unlockedBy("has_glint", has(MOTMItems.GLINT.get()))
                .save(provider);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MOTMItems.SOULSTEEL_BOOTS.get())
                .define('X', MOTMTags.Items.SOULSTEEL_INGOTS).pattern("X X").pattern("X X")
                .unlockedBy("has_diamond", has(MOTMTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MOTMItems.SOULSTEEL_CHESTPLATE.get())
                .define('X', MOTMTags.Items.SOULSTEEL_INGOTS).pattern("X X").pattern("XXX").pattern("XXX")
                .unlockedBy("has_diamond", has(MOTMTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MOTMItems.SOULSTEEL_HELMET.get())
                .define('X', MOTMTags.Items.SOULSTEEL_INGOTS).pattern("XXX").pattern("X X")
                .unlockedBy("has_diamond", has(MOTMTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MOTMItems.SOULSTEEL_HOE.get()).define('#', MOTMItems.HANDLE.get())
                .define('X', MOTMTags.Items.SOULSTEEL_INGOTS).pattern("XX").pattern(" #").pattern(" #")
                .unlockedBy("has_diamond", has(MOTMTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MOTMItems.SOULSTEEL_LEGGINGS.get())
                .define('X', MOTMTags.Items.SOULSTEEL_INGOTS).pattern("XXX").pattern("X X").pattern("X X")
                .unlockedBy("has_diamond", has(MOTMTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MOTMItems.SOULSTEEL_PICKAXE.get()).define('#', MOTMItems.HANDLE.get())
                .define('X', MOTMTags.Items.SOULSTEEL_INGOTS).pattern("XXX").pattern(" # ").pattern(" # ")
                .unlockedBy("has_diamond", has(MOTMTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MOTMItems.SOULSTEEL_SHOVEL.get()).define('#', MOTMItems.HANDLE.get())
                .define('X', MOTMTags.Items.SOULSTEEL_INGOTS).pattern("X").pattern("#").pattern("#")
                .unlockedBy("has_diamond", has(MOTMTags.Items.SOULSTEEL_INGOTS)).save(provider);
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, MOTMItems.SOULSTEEL_SWORD.get()).define('#', MOTMItems.HANDLE.get())
                .define('X', MOTMTags.Items.SOULSTEEL_INGOTS).pattern("X").pattern("X").pattern("#")
                .unlockedBy("has_diamond", has(MOTMTags.Items.SOULSTEEL_INGOTS)).save(provider);

        nineBlockStorageRecipesWithCustomUnpacking(provider,
                null, MOTMTags.Items.SOULSTEEL_INGOTS, MOTMItems.SOULSTEEL_INGOT.get(),
                null, MOTMTags.Items.SOULSTEEL_BLOCKS, MOTMItems.SOULSTEEL_BLOCK.get(),
                "soulsteel_ingot_from_soulsteel_block", "soulsteel_ingot");
        nineBlockStorageRecipesWithCustomPacking(provider,
                null, MOTMTags.Items.SOULSTEEL_NUGGETS, MOTMItems.SOULSTEEL_NUGGET.get(),
                RecipeCategory.MISC, MOTMTags.Items.SOULSTEEL_INGOTS, MOTMItems.SOULSTEEL_INGOT.get(),
                "soulsteel_ingot_from_nuggets", "soulsteel_ingot");

        oreSmelting(Ingredient.of(MOTMTags.Items.GLINT_ORES), MOTMItems.GLINT.get(), 1.5F, 200);
        oreBlasting(Ingredient.of(MOTMTags.Items.GLINT_ORES), MOTMItems.GLINT.get(), 1.5F, 100);
    }

    @Override
    protected void generateForEnabledBlockFamilies(RecipeOutput provider, FeatureFlagSet enabledFeatures) {
        MOTMBlockFamilies.getAllFamilies().filter(BlockFamily::shouldGenerateRecipe)
                .forEach(family -> generateRecipes(provider, family, enabledFeatures));
    }

    protected static void nineBlockStorageRecipesWithCustomPacking(
                                                                   RecipeOutput provider,
                                                                   @Nullable RecipeCategory unpackedCategory,
                                                                   TagKey<Item> unpacked, ItemLike unpackedResult,
                                                                   @Nullable RecipeCategory packedCategory, TagKey<Item> packed,
                                                                   ItemLike packedResult,
                                                                   String packingRecipeName,
                                                                   @Nullable String packingRecipeGroup) {
        nineBlockStorageRecipes(provider,
                unpackedCategory, unpacked, unpackedResult,
                packedCategory, packed, packedResult,
                MOTMUtil.id(packingRecipeName), packingRecipeGroup,
                getItemId(unpackedResult), null);
    }

    protected static void nineBlockStorageRecipesWithCustomUnpacking(
                                                                     RecipeOutput provider,
                                                                     @Nullable RecipeCategory unpackedCategory,
                                                                     TagKey<Item> unpacked, ItemLike unpackedResult,
                                                                     @Nullable RecipeCategory packedCategory, TagKey<Item> packed,
                                                                     ItemLike packedResult,
                                                                     String unpackingRecipeName,
                                                                     @Nullable String unpackingRecipeGroup) {
        nineBlockStorageRecipes(provider, unpackedCategory, unpacked, unpackedResult,
                packedCategory, packed, packedResult,
                getItemId(packedResult), null,
                MOTMUtil.id(unpackingRecipeName), unpackingRecipeGroup);
    }

    protected static void nineBlockStorageRecipes(
                                                  RecipeOutput recipeOutput,
                                                  @Nullable RecipeCategory unpackedCategory, TagKey<Item> unpacked,
                                                  ItemLike unpackedResult,
                                                  @Nullable RecipeCategory packedCategory, TagKey<Item> packed,
                                                  ItemLike packedResult) {
        nineBlockStorageRecipes(recipeOutput,
                unpackedCategory, unpacked, unpackedResult, packedCategory, packed, packedResult,
                getItemId(packedResult), null, getItemId(unpackedResult), null);
    }

    protected static void nineBlockStorageRecipes(
                                                  RecipeOutput provider,
                                                  @Nullable RecipeCategory unpackedCategory, TagKey<Item> unpacked,
                                                  ItemLike unpackedResult,
                                                  @Nullable RecipeCategory packedCategory, TagKey<Item> packed,
                                                  ItemLike packedResult,
                                                  ResourceLocation packingRecipeName, @Nullable String packingRecipeGroup,
                                                  ResourceLocation unpackingRecipeName, @Nullable String unpackingRecipeGroup) {
        if (unpackedCategory == null) unpackedCategory = RecipeCategory.MISC;
        if (packedCategory == null) packedCategory = RecipeCategory.BUILDING_BLOCKS;

        ShapelessRecipeBuilder.shapeless(unpackedCategory, unpackedResult, 9)
                .requires(packed)
                .group(unpackingRecipeGroup)
                .unlockedBy(getHasName(packed), has(packed))
                .save(provider, unpackingRecipeName);
        ShapedRecipeBuilder.shaped(packedCategory, packedResult)
                .define('#', unpacked)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .group(packingRecipeGroup)
                .unlockedBy(getHasName(unpacked), has(unpacked))
                .save(provider, packingRecipeName);
    }

    protected static void oreSmelting(Ingredient ingredient, ItemLike result, float experience, int cookingTime) {
        SimpleCookingRecipeBuilder.smelting(ingredient, RecipeCategory.MISC, result, experience, cookingTime);
    }

    protected static void oreBlasting(Ingredient ingredient, ItemLike result, float experience, int cookingTime) {
        SimpleCookingRecipeBuilder.blasting(ingredient, RecipeCategory.MISC, result, experience, cookingTime);
    }

    protected static ResourceLocation getItemId(ItemLike itemLike) {
        return BuiltInRegistries.ITEM.getKey(itemLike.asItem());
    }

    protected static String getHasName(TagKey<Item> tag) {
        return "has_" + tag.location();
    }

    protected static void generateRecipes(RecipeOutput provider, BlockFamily blockFamily, FeatureFlagSet requiredFeatures) {
        blockFamily.getVariants().forEach((variant, block) -> {
            if (!block.value().requiredFeatures().isSubsetOf(requiredFeatures)) {
                return;
            }
            BiFunction<ItemLike, Ingredient, RecipeBuilder> recipeFunction = SHAPE_BUILDERS.get(variant);
            if (recipeFunction == null) {
                return;
            }

            ItemLike baseBlock = getBaseBlock(blockFamily, variant);

            RecipeBuilder recipeBuilder = recipeFunction.apply(block.value(), Ingredient.of(baseBlock));

            if (blockFamily.getRecipeGroupPrefix().isPresent()) {
                String recipeGroup = blockFamily.getRecipeGroupPrefix().get();
                if (variant != BlockFamily.Variant.CUT) {
                    recipeGroup += "_" + variant.getVariantName();
                }
                recipeBuilder.group(recipeGroup);
            }

            recipeBuilder.unlockedBy(blockFamily.getRecipeUnlockedBy().orElseGet(() -> getHasName(baseBlock)), has(baseBlock));
            recipeBuilder.save(provider);
        });
    }

    protected static Block getBaseBlock(BlockFamily family, BlockFamily.Variant variant) {
        if (variant == BlockFamily.Variant.CHISELED) {
            if (family.getVariants().containsKey(BlockFamily.Variant.SLAB)) {
                return family.get(BlockFamily.Variant.SLAB).value();
            } else {
                throw new IllegalStateException("Slab is not defined for the family.");
            }
        } else {
            return family.getBaseBlock().value();
        }
    }

    // spotless:off
    private static final Map<BlockFamily.Variant, BiFunction<ItemLike, Ingredient, RecipeBuilder>> SHAPE_BUILDERS = ImmutableMap.<BlockFamily.Variant, BiFunction<ItemLike, Ingredient, RecipeBuilder>>builder()
            .put(BlockFamily.Variant.BUTTON, RecipeProvider::buttonBuilder)
            .put(BlockFamily.Variant.CHISELED, (result, ingredient) -> chiseledBuilder(RecipeCategory.BUILDING_BLOCKS, result, ingredient))
            .put(BlockFamily.Variant.CRACKED, (result, ingredient) -> SimpleCookingRecipeBuilder.smelting(ingredient, RecipeCategory.BUILDING_BLOCKS, result, 0.1f, 200))
            .put(BlockFamily.Variant.CUT, (result, ingredient) -> cutBuilder(RecipeCategory.BUILDING_BLOCKS, result, ingredient))
            .put(BlockFamily.Variant.DOOR, RecipeProvider::doorBuilder)
            .put(BlockFamily.Variant.CUSTOM_FENCE, RecipeProvider::fenceBuilder)
            .put(BlockFamily.Variant.FENCE, RecipeProvider::fenceBuilder)
            .put(BlockFamily.Variant.CUSTOM_FENCE_GATE, RecipeProvider::fenceGateBuilder)
            .put(BlockFamily.Variant.FENCE_GATE, RecipeProvider::fenceGateBuilder)
            .put(BlockFamily.Variant.SIGN, RecipeProvider::signBuilder)
            .put(BlockFamily.Variant.SLAB, (result, ingredient) -> slabBuilder(RecipeCategory.BUILDING_BLOCKS, result, ingredient))
            .put(BlockFamily.Variant.STAIRS, RecipeProvider::stairBuilder)
            .put(BlockFamily.Variant.PRESSURE_PLATE, (result, ingredient) -> pressurePlateBuilder(RecipeCategory.REDSTONE, result, ingredient))
            .put(BlockFamily.Variant.POLISHED, (result, ingredient) -> polishedBuilder(RecipeCategory.BUILDING_BLOCKS, result, ingredient))
            .put(BlockFamily.Variant.TRAPDOOR, RecipeProvider::trapdoorBuilder)
            .put(BlockFamily.Variant.WALL, (result, ingredient) -> wallBuilder(RecipeCategory.DECORATIONS, result, ingredient))
            .build();
    // spotless:on
}

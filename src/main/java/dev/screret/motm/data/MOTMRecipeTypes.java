package dev.screret.motm.data;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.recipe.PotionDistillingRecipe;
import dev.screret.motm.common.recipe.wand.ShapedWandRecipe;
import dev.screret.motm.common.recipe.wand.ShapelessWandRecipe;
import dev.screret.motm.common.recipe.wand.WandRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MOTMRecipeTypes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister
            .create(BuiltInRegistries.RECIPE_SERIALIZER, MagicOfTheMind.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE,
            MagicOfTheMind.MODID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapedWandRecipe>> SHAPED_WAND_RECIPE_SERIALIZER = RECIPE_SERIALIZERS
            .register("shaped_wand", ShapedWandRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapelessWandRecipe>> SHAPELESS_WAND_RECIPE_SERIALIZER = RECIPE_SERIALIZERS
            .register("shapeless_wand", ShapelessWandRecipe.Serializer::new);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<PotionDistillingRecipe>> POTION_DISTILLING_SERIALIZER = RECIPE_SERIALIZERS
            .register("potion_distilling", PotionDistillingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<WandRecipe>> WAND_RECIPE = RECIPE_TYPES.register("wand",
            () -> RecipeType.simple(MOTMUtil.id("wand")));
    public static final DeferredHolder<RecipeType<?>, RecipeType<PotionDistillingRecipe>> POTION_DISTILLING_RECIPE = RECIPE_TYPES
            .register("potion_distilling",
                    () -> RecipeType.simple(MOTMUtil.id("potion_distilling")));
}

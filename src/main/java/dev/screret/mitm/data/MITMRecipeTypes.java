package dev.screret.mitm.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.common.recipe.PotionDistillingRecipe;
import dev.screret.mitm.common.recipe.wand.ShapedWandRecipe;
import dev.screret.mitm.common.recipe.wand.ShapelessWandRecipe;
import dev.screret.mitm.common.recipe.wand.WandRecipe;

import java.util.function.Supplier;

public class MITMRecipeTypes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, MagicOfTheMind.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, MagicOfTheMind.MODID);

    public static final Supplier<RecipeSerializer<ShapedWandRecipe>> SHAPED_WAND_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(ShapedWandRecipe.TYPE_ID_NAME, ShapedWandRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<ShapelessWandRecipe>> SHAPELESS_WAND_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(ShapelessWandRecipe.TYPE_ID_NAME, ShapelessWandRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<PotionDistillingRecipe>> POTION_DISTILLING_SERIALIZER = RECIPE_SERIALIZERS.register(PotionDistillingRecipe.TYPE_ID_NAME, PotionDistillingRecipe.Serializer::new);

    public static final Supplier<RecipeType<WandRecipe>> WAND_RECIPE = RECIPE_TYPES.register("wand", () -> new RecipeType<>() {
        private static final ResourceLocation RECIPE_TYPE_ID = MITMUtil.id("wand");

        @Override
        public String toString() {
            return RECIPE_TYPE_ID.toString();
        }
    });
    public static final Supplier<RecipeType<PotionDistillingRecipe>> POTION_DISTILLING_RECIPE = RECIPE_TYPES.register("potion_distilling", () -> new RecipeType<>() {
        private static final ResourceLocation RECIPE_TYPE_ID = MITMUtil.id("potion_distilling");

        @Override
        public String toString() {
            return RECIPE_TYPE_ID.toString();
        }
    });
}

package screret.sas.recipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredRegister;
import screret.sas.SpellsAndSorcerers;
import screret.sas.Util;
import screret.sas.recipe.recipe.PotionDistillingRecipe;
import screret.sas.recipe.recipe.ShapedWandRecipe;
import screret.sas.recipe.recipe.ShapelessWandRecipe;
import screret.sas.recipe.recipe.WandRecipe;

import java.util.function.Supplier;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, SpellsAndSorcerers.MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(BuiltInRegistries.RECIPE_TYPE, SpellsAndSorcerers.MODID);

    public static final Supplier<RecipeSerializer<ShapedWandRecipe>> SHAPED_WAND_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(ShapedWandRecipe.TYPE_ID_NAME, ShapedWandRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<ShapelessWandRecipe>> SHAPELESS_WAND_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register(ShapelessWandRecipe.TYPE_ID_NAME, ShapelessWandRecipe.Serializer::new);
    public static final Supplier<RecipeSerializer<PotionDistillingRecipe>> POTION_DISTILLING_SERIALIZER = RECIPE_SERIALIZERS.register(PotionDistillingRecipe.TYPE_ID_NAME, PotionDistillingRecipe.Serializer::new);

    public static final Supplier<RecipeType<WandRecipe>> WAND_RECIPE = RECIPE_TYPES.register("wand", () -> new RecipeType<>() {
        private static final ResourceLocation RECIPE_TYPE_ID = Util.id("wand");
        @Override
        public String toString(){
            return RECIPE_TYPE_ID.toString();
        }
    });
    public static final Supplier<RecipeType<PotionDistillingRecipe>> POTION_DISTILLING_RECIPE = RECIPE_TYPES.register("potion_distilling", () -> new RecipeType<>() {
        private static final ResourceLocation RECIPE_TYPE_ID = Util.id("potion_distilling");
        @Override
        public String toString(){
            return RECIPE_TYPE_ID.toString();
        }
    });
}

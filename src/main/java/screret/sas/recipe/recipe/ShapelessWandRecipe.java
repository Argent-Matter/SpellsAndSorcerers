package screret.sas.recipe.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import screret.sas.Util;
import screret.sas.recipe.ModRecipes;
import screret.sas.recipe.ingredient.WandAbilityIngredient;

public class ShapelessWandRecipe implements WandRecipe {

    public static final String TYPE_ID_NAME = "shapeless_wand";
    public static final ResourceLocation TYPE_ID = Util.id(TYPE_ID_NAME);
    public static final int MAX_SIZE_X = 3, MAX_SIZE_Y = 2;

    private final ResourceLocation id;
    final String group;
    final WandAbilityIngredient result;
    final NonNullList<Ingredient> ingredients;

    public ShapelessWandRecipe(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, WandAbilityIngredient result) {
        this.id = id;
        this.group = group;
        this.result = result;
        this.ingredients = ingredients;
    }

    @Override
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        for(int i = 0; i <= pInv.getWidth() - MAX_SIZE_X; ++i) {
            for(int j = 0; j <= pInv.getHeight() - MAX_SIZE_Y; ++j) {
                if (this.matches(pInv, i, j, true)) {
                    return true;
                }

                if (this.matches(pInv, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matches(CraftingContainer pCraftingInventory, int pWidth, int pHeight, boolean pMirrored) {
        for(int i = 0; i < pCraftingInventory.getWidth(); ++i) {
            for(int j = 0; j < pCraftingInventory.getHeight(); ++j) {
                int k = i - pWidth;
                int l = j - pHeight;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < MAX_SIZE_X && l < MAX_SIZE_Y) {
                    if (pMirrored) {
                        ingredient = this.ingredients.get(MAX_SIZE_X - k - 1 + l * MAX_SIZE_X);
                    } else {
                        ingredient = this.ingredients.get(k + l * MAX_SIZE_X);
                    }
                }

                if (!ingredient.test(pCraftingInventory.getItem(i + j * pCraftingInventory.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess pRegistryAccess) {
            return this.result.getStack();
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x <= MAX_SIZE_X && y <= MAX_SIZE_Y;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return result.getStack();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SHAPELESS_WAND_RECIPE_SERIALIZER.get();
    }

    private static NonNullList<Ingredient> itemsFromJson(JsonArray pIngredientArray) {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        for(int i = 0; i < pIngredientArray.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(pIngredientArray.get(i));
            ingredients.add(ingredient);
        }

        return ingredients;
    }

    @Override
    public boolean isShapeless() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<ShapelessWandRecipe> {
        @Override
        public ShapelessWandRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String s = GsonHelper.getAsString(json, "group", "");
            NonNullList<Ingredient> ingredients = itemsFromJson(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (ingredients.isEmpty()) {
                throw new JsonParseException("No ingredients for shapeless wand recipe");
            } else if (ingredients.size() > ShapedWandRecipe.MAX_SIZE_X * ShapedWandRecipe.MAX_SIZE_Y) {
                throw new JsonParseException("Too many ingredients for shapeless wand recipe. The maximum is " + (ShapedWandRecipe.MAX_SIZE_X * ShapedWandRecipe.MAX_SIZE_Y));
            } else {
                WandAbilityIngredient itemstack = WandAbilityIngredient.Serializer.INSTANCE.parse(GsonHelper.getAsJsonObject(json, "result"));
                return new ShapelessWandRecipe(recipeId, s, ingredients, itemstack);
            }
        }

        @Override
        public ShapelessWandRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            int count = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(count, Ingredient.EMPTY);

            for(int j = 0; j < ingredients.size(); ++j) {
                ingredients.set(j, Ingredient.fromNetwork(buffer));
            }

            WandAbilityIngredient result = WandAbilityIngredient.Serializer.INSTANCE.parse(buffer);
            return new ShapelessWandRecipe(recipeId, group, ingredients, result);
        }

        @Override
        public Codec<ShapelessWandRecipe> codec() {
            return null;
        }

        @Override
        public ShapelessWandRecipe fromNetwork(FriendlyByteBuf pBuffer) {
            return null;
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapelessWandRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeVarInt(recipe.ingredients.size());

            for(Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buffer);
            }

            WandAbilityIngredient.Serializer.INSTANCE.write(buffer, recipe.result);
        }
    }
}
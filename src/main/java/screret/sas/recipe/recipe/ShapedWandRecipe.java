package screret.sas.recipe.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import screret.sas.Util;
import screret.sas.recipe.ModRecipes;
import screret.sas.recipe.ingredient.WandAbilityIngredient;

public class ShapedWandRecipe implements WandRecipe {

    public static final String TYPE_ID_NAME = "shaped_wand";
    public static final int MAX_SIZE_X = 3, MAX_SIZE_Y = 2;

    private final ResourceLocation id;
    final String group;
    final WandAbilityIngredient result;
    final NonNullList<Ingredient> ingredients;

    public ShapedWandRecipe(ResourceLocation id, String group, NonNullList<Ingredient> ingredients, WandAbilityIngredient result) {
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
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SHAPED_WAND_RECIPE_SERIALIZER.get();
    }

    private static String[] patternFromJson(JsonArray jsonArr) {
        var astring = new String[jsonArr.size()];
        for (int i = 0; i < astring.length; ++i) {
            var s = GsonHelper.convertToString(jsonArr.get(i), "pattern[" + i + "]");

            if (i > 0 && astring[0].length() != s.length()) {
                throw new JsonSyntaxException("Invalid pattern: each row must be the same width");
            }

            astring[i] = s;
        }

        return astring;
    }

    public String toString(){
        return getId().toString();
    }

    @Override
    public boolean isShapeless() {
        return false;
    }

    public static class Serializer implements RecipeSerializer<ShapedWandRecipe> {
        @Override
        public ShapedWandRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            var map = ShapedRecipe.keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
            var pattern = ShapedRecipe.shrink(ShapedWandRecipe.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
            var inputs = ShapedRecipe.dissolvePattern(pattern, map, MAX_SIZE_X, MAX_SIZE_Y);
            var output = WandAbilityIngredient.Serializer.INSTANCE.parse(GsonHelper.getAsJsonObject(json, "result"));

            return new ShapedWandRecipe(recipeId, group, inputs, output);
        }

        @Override
        public ShapedWandRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            var inputs = NonNullList.withSize(MAX_SIZE_X * MAX_SIZE_Y, Ingredient.EMPTY);

            inputs.replaceAll(ignored -> Ingredient.fromNetwork(buffer));

            var result = WandAbilityIngredient.Serializer.INSTANCE.parse(buffer);

            return new ShapedWandRecipe(recipeId, group, inputs, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapedWandRecipe recipe) {
            buffer.writeUtf(recipe.group);
            for (var ingredient : recipe.ingredients) {
                ingredient.toNetwork(buffer);
            }

            WandAbilityIngredient.Serializer.INSTANCE.write(buffer, recipe.result);
        }
    }
}
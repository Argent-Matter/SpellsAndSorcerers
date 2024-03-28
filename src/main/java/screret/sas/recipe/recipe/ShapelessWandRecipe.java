package screret.sas.recipe.recipe;

import com.google.gson.JsonArray;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import screret.sas.Util;
import screret.sas.recipe.ModRecipeTypes;
import screret.sas.recipe.ingredient.WandAbilityIngredient;

public class ShapelessWandRecipe implements WandRecipe {

    public static final String TYPE_ID_NAME = "shapeless_wand";
    public static final ResourceLocation TYPE_ID = Util.id(TYPE_ID_NAME);
    public static final int MAX_SIZE_X = 3, MAX_SIZE_Y = 2;

    final String group;
    final WandAbilityIngredient result;
    final NonNullList<Ingredient> ingredients;

    public ShapelessWandRecipe(String group, NonNullList<Ingredient> ingredients, WandAbilityIngredient result) {
        this.group = group;
        this.result = result;
        this.ingredients = ingredients;
    }

    @Override
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        for (int i = 0; i <= pInv.getWidth() - MAX_SIZE_X; ++i) {
            for (int j = 0; j <= pInv.getHeight() - MAX_SIZE_Y; ++j) {
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
        for (int i = 0; i < pCraftingInventory.getWidth(); ++i) {
            for (int j = 0; j < pCraftingInventory.getHeight(); ++j) {
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
        return ModRecipeTypes.SHAPELESS_WAND_RECIPE_SERIALIZER.get();
    }

    private static NonNullList<Ingredient> itemsFromJson(JsonArray pIngredientArray) {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        for (int i = 0; i < pIngredientArray.size(); ++i) {
            Ingredient ingredient = Ingredient.fromJson(pIngredientArray.get(i), false);
            ingredients.add(ingredient);
        }

        return ingredients;
    }

    @Override
    public boolean isShapeless() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<ShapelessWandRecipe> {
        public static final Codec<ShapelessWandRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(val -> val.group),
                Ingredient.CODEC_NONEMPTY
                        .listOf()
                        .fieldOf("ingredients")
                        .flatXmap(
                                ingredients -> {
                                    Ingredient[] aingredient = ingredients
                                            .toArray(Ingredient[]::new); //Forge skip the empty check and immediately create the array.
                                    if (aingredient.length == 0) {
                                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                                    } else {
                                        return aingredient.length > MAX_SIZE_Y * MAX_SIZE_X
                                                ? DataResult.error(() -> "Too many ingredients for shapeless wand recipe. The maximum is: %s".formatted(MAX_SIZE_Y * MAX_SIZE_X))
                                                : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                                    }
                                },
                                DataResult::success
                        )
                        .forGetter(val -> val.ingredients),
                WandAbilityIngredient.CODEC.fieldOf("result").forGetter(val -> val.result)

        ).apply(instance, ShapelessWandRecipe::new));

        @Override
        public Codec<ShapelessWandRecipe> codec() {
            return CODEC;
        }

        @Override
        public ShapelessWandRecipe fromNetwork(FriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            int count = buffer.readVarInt();
            NonNullList<Ingredient> ingredients = NonNullList.withSize(count, Ingredient.EMPTY);
            ingredients.replaceAll(ignored -> Ingredient.fromNetwork(buffer));

            WandAbilityIngredient result = (WandAbilityIngredient) WandAbilityIngredient.fromNetwork(buffer);
            return new ShapelessWandRecipe(group, ingredients, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapelessWandRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeVarInt(recipe.ingredients.size());
            for (Ingredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buffer);
            }

            recipe.result.toNetwork(buffer);
        }
    }
}
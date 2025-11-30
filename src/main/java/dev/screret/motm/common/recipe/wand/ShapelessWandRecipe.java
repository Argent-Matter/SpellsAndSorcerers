package dev.screret.motm.common.recipe.wand;

import dev.screret.motm.common.recipe.ingredient.WandAbilityIngredient;
import dev.screret.motm.data.MOTMRecipeTypes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ShapelessWandRecipe implements WandRecipe {

    public static final int MAX_SIZE_X = 3, MAX_SIZE_Y = 2;

    @Getter
    private final String group;
    @Getter
    private final NonNullList<Ingredient> ingredients;
    @Getter(AccessLevel.PRIVATE)
    private final WandAbilityIngredient result;

    public ShapelessWandRecipe(String group, NonNullList<Ingredient> ingredients, WandAbilityIngredient result) {
        this.group = group;
        this.result = result;
        this.ingredients = ingredients;
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        for (int i = 0; i <= inv.width() - MAX_SIZE_X; ++i) {
            for (int j = 0; j <= inv.height() - MAX_SIZE_Y; ++j) {
                if (this.matches(inv, i, j, true)) {
                    return true;
                }

                if (this.matches(inv, i, j, false)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean matches(CraftingInput craftingInventory, int width, int height, boolean mirrored) {
        for (int i = 0; i < craftingInventory.width(); ++i) {
            for (int j = 0; j < craftingInventory.height(); ++j) {
                int k = i - width;
                int l = j - height;
                Ingredient ingredient = Ingredient.EMPTY;
                if (k >= 0 && l >= 0 && k < MAX_SIZE_X && l < MAX_SIZE_Y) {
                    if (mirrored) {
                        ingredient = this.ingredients.get(MAX_SIZE_X - k - 1 + l * MAX_SIZE_X);
                    } else {
                        ingredient = this.ingredients.get(k + l * MAX_SIZE_X);
                    }
                }

                if (!ingredient.test(craftingInventory.getItem(i + j * craftingInventory.width()))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider registries) {
        return this.result.getStack();
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x <= MAX_SIZE_X && y <= MAX_SIZE_Y;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.getStack();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MOTMRecipeTypes.SHAPELESS_WAND_RECIPE_SERIALIZER.get();
    }

    @Override
    public boolean isShapeless() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<ShapelessWandRecipe> {

        // spotless:off
        private static final MapCodec<ShapelessWandRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(ShapelessWandRecipe::getGroup),
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients")
                        .flatXmap(ingredients -> {
                            // Neo: skip the empty check and immediately create the array.
                            Ingredient[] array = ingredients.toArray(Ingredient[]::new);
                            if (array.length == 0) {
                                return DataResult.error(() -> "No ingredients for shapeless recipe");
                            } else {
                                return array.length > MAX_SIZE_Y * MAX_SIZE_X
                                        ? DataResult.error(() -> "Too many ingredients for shapeless wand recipe. The maximum is: %s".formatted(MAX_SIZE_Y * MAX_SIZE_X))
                                        : DataResult.success(NonNullList.of(Ingredient.EMPTY, array));
                            }
                            }, DataResult::success
                        ).forGetter(ShapelessWandRecipe::getIngredients),
                WandAbilityIngredient.CODEC.fieldOf("result").forGetter(ShapelessWandRecipe::getResult)
        ).apply(instance, ShapelessWandRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, NonNullList<Ingredient>> INGREDIENT_STREAM_CODEC = ByteBufCodecs.collection(
                size -> NonNullList.withSize(size, Ingredient.EMPTY), Ingredient.CONTENTS_STREAM_CODEC, MAX_SIZE_Y * MAX_SIZE_X);
        private static final StreamCodec<RegistryFriendlyByteBuf, ShapelessWandRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, ShapelessWandRecipe::getGroup,
                INGREDIENT_STREAM_CODEC, ShapelessWandRecipe::getIngredients,
                WandAbilityIngredient.STREAM_CODEC, ShapelessWandRecipe::getResult,
                ShapelessWandRecipe::new
        );
        // spotless:on

        @Override
        public MapCodec<ShapelessWandRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapelessWandRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}

package dev.screret.mitm.common.data.builder.conversion;

import dev.screret.mitm.common.recipe.ingredient.BlockIngredient;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.util.function.Consumer;

public class EyeConversionBuilder {

    private final Block result;
    private BlockIngredient ingredient = BlockIngredient.EMPTY;

    public EyeConversionBuilder(Block result) {
        this.result = result;
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static EyeConversionBuilder conversion(Block result) {
        return new EyeConversionBuilder(result);
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public EyeConversionBuilder requires(TagKey<Block> tag) {
        return this.requires(BlockIngredient.of(tag));
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public EyeConversionBuilder requires(Block block) {
        return this.requires(BlockIngredient.of(block));
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public EyeConversionBuilder requires(BlockIngredient ingredient) {
        this.ingredient = ingredient;
        return this;
    }

    public Block getResult() {
        return this.result;
    }

    public void save(Consumer<Result> finishedRecipeConsumer) {
        this.save(finishedRecipeConsumer, BuiltInRegistries.BLOCK.getKey(this.result));
    }

    public void save(Consumer<Result> finishedRecipeConsumer, ResourceLocation recipeId) {
        this.ensureValid(recipeId);
        finishedRecipeConsumer.accept(new EyeConversionBuilder.Result(recipeId, this.result, this.ingredient));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void ensureValid(ResourceLocation id) {
        for (var block : ingredient.getBlocks()) {
            if (!BuiltInRegistries.BLOCK.containsValue(block.getBlock())) {
                throw new IllegalArgumentException("Block " + block + " is not registered!");
            }
        }
    }

    public static class Result {

        private final ResourceLocation id;
        private final Block result;
        private final BlockIngredient ingredient;

        public Result(ResourceLocation id, Block result, BlockIngredient key) {
            this.id = id;
            this.result = result;
            this.ingredient = key;
        }

        public void serializeRecipeData(JsonObject json) {
            JsonPrimitive result = new JsonPrimitive(BuiltInRegistries.BLOCK.getKey(this.result).toString());

            json.add("result", result);
            json.add("ingredient", this.ingredient.toJson());
        }

        public JsonObject serializeRecipe() {
            JsonObject jsonobject = new JsonObject();
            this.serializeRecipeData(jsonobject);
            return jsonobject;
        }

        public RecipeSerializer<?> getType() {
            return RecipeSerializer.SHAPED_RECIPE;
        }

        /**
         * Gets the ID for the recipe.
         */
        public ResourceLocation getId() {
            return this.id;
        }
    }
}

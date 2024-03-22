package screret.sas.data.conversion.builder;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import screret.sas.recipe.ingredient.BlockIngredient;

import java.util.function.Consumer;

public class EyeConversionBuilder {
    private final Block result;
    private BlockIngredient ingredient = BlockIngredient.EMPTY;
    public EyeConversionBuilder(Block pResult) {
        this.result = pResult;
    }

    /**
     * Creates a new builder for a shaped recipe.
     */
    public static EyeConversionBuilder conversion(Block pResult) {
        return new EyeConversionBuilder(pResult);
    }

    /**
     * Adds a key to the recipe pattern.
     */
    public EyeConversionBuilder requires(TagKey<Block> pTag) {
        return this.requires(BlockIngredient.of(pTag));
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
    public EyeConversionBuilder requires(BlockIngredient pIngredient) {
        this.ingredient = pIngredient;
        return this;
    }

    public Block getResult() {
        return this.result;
    }

    public void save(Consumer<Result> pFinishedRecipeConsumer){
        this.save(pFinishedRecipeConsumer, BuiltInRegistries.BLOCK.getKey(this.result));
    }

    public void save(Consumer<Result> pFinishedRecipeConsumer, ResourceLocation pRecipeId) {
        this.ensureValid(pRecipeId);
        pFinishedRecipeConsumer.accept(new EyeConversionBuilder.Result(pRecipeId, this.result, this.ingredient));
    }

    /**
     * Makes sure that this recipe is valid and obtainable.
     */
    private void ensureValid(ResourceLocation pId) {

        for (var block : ingredient.getBlocks()){
            if(!BuiltInRegistries.BLOCK.containsValue(block.getBlock())){
                throw new IllegalArgumentException("Block " + block + " is not registered!");
            }
        }
    }

    public static class Result {
        private final ResourceLocation id;
        private final Block result;
        private final BlockIngredient ingredient;

        public Result(ResourceLocation id, Block result, BlockIngredient pKey) {
            this.id = id;
            this.result = result;
            this.ingredient = pKey;
        }

        public void serializeRecipeData(JsonObject pJson) {
            JsonPrimitive result = new JsonPrimitive(BuiltInRegistries.BLOCK.getKey(this.result).toString());

            pJson.add("result", result);
            pJson.add("ingredient", this.ingredient.toJson());
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
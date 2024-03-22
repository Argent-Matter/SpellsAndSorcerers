package screret.sas.recipe.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import screret.sas.recipe.ModRecipes;

public class PotionDistillingRecipe implements Recipe<Container> {
    public static String TYPE_ID_NAME = "potion_distilling";

    protected final ResourceLocation id;
    protected final String group;
    protected final Ingredient ingredient;
    protected final ItemStack result;
    protected final float experience;
    protected final int processingTime;

    public PotionDistillingRecipe(ResourceLocation pId, String pGroup, Ingredient pIngredient, ItemStack pResult, float pExperience, int pCookingTime) {
        this.id = pId;
        this.group = pGroup;
        this.ingredient = pIngredient;
        this.result = pResult;
        this.experience = pExperience;
        this.processingTime = pCookingTime;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(Container pInv, Level pLevel) {
        return this.ingredient.test(pInv.getItem(0));
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack assemble(Container pInv) {
        return this.result.copy();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    /**
     * Gets the experience of this recipe
     */
    public float getExperience() {
        return this.experience;
    }

    /**
     * Get the result of this recipe, usually for display purposes (e.g. recipe book). If your recipe has more than one
     * possible result (e.g. it's dynamic and depends on its inputs), then return an empty stack.
     */
    public ItemStack getResultItem() {
        return this.result;
    }

    /**
     * Recipes with equal group are combined into one button in the recipe book
     */
    public String getGroup() {
        return this.group;
    }

    /**
     * Gets the cook time in ticks
     */
    public int getProcessingTime() {
        return this.processingTime;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.POTION_DISTILLING_SERIALIZER.get();
    }

    public RecipeType<?> getType() {
        return ModRecipes.POTION_DISTILLING_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<PotionDistillingRecipe> {
        private final int defaultCookingTime;
        public Serializer() {
            this.defaultCookingTime = 400;
        }

        public PotionDistillingRecipe fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
            String s = GsonHelper.getAsString(pJson, "group", "");
            JsonElement jsonelement = GsonHelper.isArrayNode(pJson, "ingredient") ? GsonHelper.getAsJsonArray(pJson, "ingredient") : GsonHelper.getAsJsonObject(pJson, "ingredient");
            Ingredient ingredient = Ingredient.fromJson(jsonelement);
            //Forge: Check if primitive string to keep vanilla or a object which can contain a count field.
            if (!pJson.has("result")) throw new JsonSyntaxException("Missing result, expected to find a string or object");
            ItemStack result;
            if (pJson.get("result").isJsonObject()) result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pJson, "result"));
            else {
                String resultName = GsonHelper.getAsString(pJson, "result");
                ResourceLocation resultId = new ResourceLocation(resultName);
                result = new ItemStack(Registry.ITEM.getOptional(resultId).orElseThrow(() -> {
                    return new IllegalStateException("Item: " + resultName + " does not exist");
                }));
            }
            float experience = GsonHelper.getAsFloat(pJson, "experience", 0.0F);
            int cookingTime = GsonHelper.getAsInt(pJson, "time", this.defaultCookingTime);
            return new PotionDistillingRecipe(pRecipeId, s, ingredient, result, experience, cookingTime);
        }

        public PotionDistillingRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String s = pBuffer.readUtf();
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            ItemStack itemstack = pBuffer.readItem();
            float f = pBuffer.readFloat();
            int i = pBuffer.readVarInt();
            return new PotionDistillingRecipe(pRecipeId, s, ingredient, itemstack, f, i);
        }

        public void toNetwork(FriendlyByteBuf pBuffer, PotionDistillingRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.group);
            pRecipe.ingredient.toNetwork(pBuffer);
            pBuffer.writeItem(pRecipe.result);
            pBuffer.writeFloat(pRecipe.experience);
            pBuffer.writeVarInt(pRecipe.processingTime);
        }
    }

}

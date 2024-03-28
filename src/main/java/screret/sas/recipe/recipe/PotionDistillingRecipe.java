package screret.sas.recipe.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import screret.sas.recipe.ModRecipeTypes;

public class PotionDistillingRecipe implements Recipe<Container> {
    public static String TYPE_ID_NAME = "potion_distilling";

    protected final String group;
    protected final Ingredient ingredient;
    protected final ItemStack result;
    protected final float experience;
    protected final int processingTime;

    public PotionDistillingRecipe(String pGroup, Ingredient pIngredient, ItemStack pResult, float pExperience, int pCookingTime) {
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

    @Override
    public ItemStack assemble(Container pContainer, RegistryAccess pRegistryAccess) {
        return this.result.copy();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return this.result.copy();
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

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.POTION_DISTILLING_SERIALIZER.get();
    }

    public RecipeType<?> getType() {
        return ModRecipeTypes.POTION_DISTILLING_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<PotionDistillingRecipe> {
        private static final int PROCESSING_TIME = 400;
        public static final Codec<PotionDistillingRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(val -> val.group),
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(val -> val.ingredient),
                ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(val -> val.result),
                Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(val -> val.experience),
                Codec.INT.fieldOf("processing_time").orElse(PROCESSING_TIME).forGetter(val -> val.processingTime)
        ).apply(instance, PotionDistillingRecipe::new));

        @Override
        public Codec<PotionDistillingRecipe> codec() {
            return CODEC;
        }

        public PotionDistillingRecipe fromNetwork(FriendlyByteBuf pBuffer) {
            String group = pBuffer.readUtf();
            Ingredient ingredient = Ingredient.fromNetwork(pBuffer);
            ItemStack result = pBuffer.readItem();
            float experience = pBuffer.readFloat();
            int processing_time = pBuffer.readVarInt();
            return new PotionDistillingRecipe(group, ingredient, result, experience, processing_time);
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

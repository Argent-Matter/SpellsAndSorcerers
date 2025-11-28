package dev.screret.mitm.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.screret.mitm.data.MITMRecipeTypes;
import lombok.AccessLevel;
import lombok.Getter;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PotionDistillingRecipe implements Recipe<RecipeInput> {

    /**
     *  Recipes with equal group are combined into one button in the recipe book
     */
    @Getter
    protected final String group;
    @Getter(AccessLevel.PRIVATE)
    protected final Ingredient ingredient;
    @Getter(AccessLevel.PRIVATE)
    protected final ItemStack result;
    /**
     *  Gets the experience of this recipe
     */
    @Getter
    protected final float experience;
    /**
     *  Gets the processing time in ticks
     */
    @Getter
    protected final int processingTime;

    public PotionDistillingRecipe(String group, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        this.group = group;
        this.ingredient = ingredient;
        this.result = result;
        this.experience = experience;
        this.processingTime = cookingTime;
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(RecipeInput inv, Level level) {
        return this.ingredient.test(inv.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeInput container, HolderLookup.Provider registries) {
        return this.result.copy();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return this.result.copy();
    }

    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> nonnulllist = NonNullList.create();
        nonnulllist.add(this.ingredient);
        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MITMRecipeTypes.POTION_DISTILLING_SERIALIZER.get();
    }

    public RecipeType<?> getType() {
        return MITMRecipeTypes.POTION_DISTILLING_RECIPE.get();
    }

    public static class Serializer implements RecipeSerializer<PotionDistillingRecipe> {

        // spotless:off
        private static final int PROCESSING_TIME = 400;
        private static final MapCodec<PotionDistillingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(val -> val.group),
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(val -> val.ingredient),
                ItemStack.CODEC.fieldOf("result").forGetter(val -> val.result),
                Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(val -> val.experience),
                Codec.INT.fieldOf("processing_time").orElse(PROCESSING_TIME).forGetter(val -> val.processingTime)
        ).apply(instance, PotionDistillingRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, PotionDistillingRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, PotionDistillingRecipe::getGroup,
                Ingredient.CONTENTS_STREAM_CODEC, PotionDistillingRecipe::getIngredient,
                ItemStack.STREAM_CODEC, PotionDistillingRecipe::getResult,
                ByteBufCodecs.FLOAT, PotionDistillingRecipe::getExperience,
                ByteBufCodecs.VAR_INT, PotionDistillingRecipe::getProcessingTime,
                PotionDistillingRecipe::new
        );
        // spotless:on

        @Override
        public MapCodec<PotionDistillingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, PotionDistillingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

}

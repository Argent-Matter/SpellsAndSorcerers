package dev.screret.mitm.common.recipe.wand;

import dev.screret.mitm.common.recipe.ingredient.WandAbilityIngredient;
import dev.screret.mitm.data.MITMIngredientTypes;
import dev.screret.mitm.data.MITMRecipeTypes;

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
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.Getter;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ShapedWandRecipe implements WandRecipe {

    @Getter
    private final String group;
    @Getter(AccessLevel.PRIVATE)
    private final ShapedRecipePattern pattern;
    @Getter(AccessLevel.PRIVATE)
    private final WandAbilityIngredient result;

    public ShapedWandRecipe(String group, ShapedRecipePattern pattern, WandAbilityIngredient result) {
        this.group = group;
        this.result = result;
        this.pattern = pattern;
    }

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        return pattern.matches(inv);
    }

    @Override
    public ItemStack assemble(CraftingInput container, HolderLookup.Provider registries) {
        return this.result.getStack();
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x <= this.pattern.width() && y <= this.pattern.height();
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return result.getStack();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return pattern.ingredients();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MITMRecipeTypes.SHAPED_WAND_RECIPE_SERIALIZER.get();
    }

    @Override
    public boolean isShapeless() {
        return false;
    }

    public static class Serializer implements RecipeSerializer<ShapedWandRecipe> {

        // spotless:off
        public static final MapCodec<ShapedWandRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(ShapedWandRecipe::getGroup),
                ShapedRecipePattern.MAP_CODEC.forGetter(ShapedWandRecipe::getPattern),
                WandAbilityIngredient.CODEC.fieldOf("result").forGetter(ShapedWandRecipe::getResult)
        ).apply(instance, ShapedWandRecipe::new));
        private static final StreamCodec<RegistryFriendlyByteBuf, ShapedWandRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, ShapedWandRecipe::getGroup,
                ShapedRecipePattern.STREAM_CODEC, ShapedWandRecipe::getPattern,
                MITMIngredientTypes.WAND_ABILITY.get().streamCodec(), ShapedWandRecipe::getResult,
                ShapedWandRecipe::new
        );
        // spotless:off

        @Override
        public MapCodec<ShapedWandRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, ShapedWandRecipe> streamCodec() {
                return STREAM_CODEC;
        }
    }
}

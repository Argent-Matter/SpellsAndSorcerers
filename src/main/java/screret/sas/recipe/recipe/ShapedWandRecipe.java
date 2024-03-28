package screret.sas.recipe.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import screret.sas.recipe.ModRecipeTypes;
import screret.sas.recipe.ingredient.WandAbilityIngredient;

public class ShapedWandRecipe implements WandRecipe {

    public static final String TYPE_ID_NAME = "shaped_wand";
    public static final int MAX_SIZE_X = 3, MAX_SIZE_Y = 2;

    final String group;
    final ShapedRecipePattern pattern;
    final WandAbilityIngredient result;

    public ShapedWandRecipe(String group, ShapedRecipePattern pattern, WandAbilityIngredient result) {
        this.group = group;
        this.result = result;
        this.pattern = pattern;
    }

    @Override
    public boolean matches(CraftingContainer pInv, Level pLevel) {
        return pattern.matches(pInv);
    }

    @Override
    public ItemStack assemble(CraftingContainer container, RegistryAccess pRegistryAccess) {
        return this.result.getStack();
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x <= this.pattern.width() && y <= this.pattern.height();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return result.getStack();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return pattern.ingredients();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.SHAPED_WAND_RECIPE_SERIALIZER.get();
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

    @Override
    public boolean isShapeless() {
        return false;
    }

    public static class Serializer implements RecipeSerializer<ShapedWandRecipe> {
        public static final Codec<ShapedWandRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(val -> val.group),
                ShapedRecipePattern.MAP_CODEC.forGetter(val -> val.pattern),
                WandAbilityIngredient.CODEC.fieldOf("result").forGetter(val -> val.result)
        ).apply(instance, ShapedWandRecipe::new));

        @Override
        public Codec<ShapedWandRecipe> codec() {
            return CODEC;
        }

        @Override
        public ShapedWandRecipe fromNetwork(FriendlyByteBuf buffer) {
            String group = buffer.readUtf();
            ShapedRecipePattern pattern = ShapedRecipePattern.fromNetwork(buffer);
            WandAbilityIngredient result = (WandAbilityIngredient) WandAbilityIngredient.fromNetwork(buffer);

            return new ShapedWandRecipe(group, pattern, result);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ShapedWandRecipe recipe) {
            buffer.writeUtf(recipe.group);
            recipe.pattern.toNetwork(buffer);
            recipe.result.toNetwork(buffer);
        }
    }
}
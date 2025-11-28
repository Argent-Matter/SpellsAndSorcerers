package dev.screret.mitm.common.recipe.wand;

import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import dev.screret.mitm.data.MITMRecipeTypes;

public interface WandRecipe extends Recipe<CraftingInput> {
    default RecipeType<?> getType() {
        return MITMRecipeTypes.WAND_RECIPE.get();
    }

    boolean isShapeless();

}

package dev.screret.motm.common.recipe.wand;

import dev.screret.motm.data.MOTMRecipeTypes;

import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import org.jetbrains.annotations.NotNull;

public interface WandRecipe extends Recipe<CraftingInput> {

    @Override
    default @NotNull RecipeType<?> getType() {
        return MOTMRecipeTypes.WAND_RECIPE.get();
    }

    boolean isShapeless();
}

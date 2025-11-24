package dev.screret.sas.common.recipe.wand;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import dev.screret.sas.data.ModRecipeTypes;

public interface WandRecipe extends Recipe<CraftingContainer> {
    default RecipeType<?> getType() {
        return ModRecipeTypes.WAND_RECIPE.get();
    }

    boolean isShapeless();

}

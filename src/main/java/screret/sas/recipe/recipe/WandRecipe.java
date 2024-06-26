package screret.sas.recipe.recipe;

import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import screret.sas.recipe.ModRecipeTypes;

public interface WandRecipe extends Recipe<CraftingContainer> {
    default RecipeType<?> getType() {
        return ModRecipeTypes.WAND_RECIPE.get();
    }

    boolean isShapeless();

}

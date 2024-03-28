package screret.sas.container.stackhandler;

import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class CraftResultStackHandler extends ItemStackHandler implements RecipeCraftingHolder {

    @Nullable
    private RecipeHolder<?> recipeUsed;

    public CraftResultStackHandler(int size) {
        super(size);
    }

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> pRecipe) {
        this.recipeUsed = pRecipe;
    }

    @Nullable
    @Override
    public RecipeHolder<?> getRecipeUsed() {
        return recipeUsed;
    }
}

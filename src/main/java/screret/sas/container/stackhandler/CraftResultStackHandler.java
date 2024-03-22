package screret.sas.container.stackhandler;

import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class CraftResultStackHandler extends ItemStackHandler implements RecipeHolder {

    @Nullable
    private Recipe<?> recipeUsed;

    public CraftResultStackHandler(int size) {
        super(size);
    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> recipe) {
        this.recipeUsed = recipe;
    }

    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return recipeUsed;
    }
}

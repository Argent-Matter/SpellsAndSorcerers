package screret.sas.integration.rei.wand;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;
import net.minecraft.world.item.crafting.ShapedRecipe;
import screret.sas.recipe.recipe.WandRecipe;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DefaultWandDisplay extends DefaultCraftingDisplay<WandRecipe> {

    public DefaultWandDisplay(WandRecipe recipe) {
        super(
                EntryIngredients.ofIngredients(recipe.getIngredients()),
                Collections.singletonList(EntryIngredients.of(recipe.getResultItem())),
                Optional.of(recipe)
        );
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 2;
    }
}

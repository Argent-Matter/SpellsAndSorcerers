package dev.screret.motm.integration.rei.wand;

import dev.screret.motm.common.recipe.wand.WandRecipe;

import net.minecraft.world.item.crafting.RecipeHolder;

import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import me.shedaniel.rei.plugin.common.displays.crafting.DefaultCraftingDisplay;

import java.util.Collections;
import java.util.Optional;

public class DefaultWandDisplay extends DefaultCraftingDisplay<WandRecipe> {

    public DefaultWandDisplay(RecipeHolder<WandRecipe> recipe) {
        super(
                EntryIngredients.ofIngredients(recipe.value().getIngredients()),
                Collections.singletonList(EntryIngredients.of(recipe.value().getResultItem(BasicDisplay.registryAccess()))),
                Optional.of(recipe));
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

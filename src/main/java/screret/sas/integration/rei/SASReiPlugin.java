package screret.sas.integration.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.subsets.SubsetsRegistry;
import me.shedaniel.rei.api.common.plugins.REIPlugin;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import screret.sas.Util;
import screret.sas.block.ModBlocks;
import screret.sas.client.gui.screen.WandTableScreen;
import screret.sas.integration.rei.wand.WandCoreSubtypeInterpreter;
import screret.sas.integration.rei.wand.WandRecipeCategory;
import screret.sas.integration.rei.wand.WandSubtypeInterpreter;
import screret.sas.item.ModItems;
import screret.sas.recipe.ModRecipes;
import screret.sas.recipe.recipe.WandRecipe;
import java.util.List;

@REIPluginClient
public class SASReiPlugin implements REIClientPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return Util.id("jei_plugin");
    }
    REIPlugin

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new WandRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerSubsets(SubsetsRegistry registry) {
        registry.registerPathEntries(ModItems.WAND.get(), WandSubtypeInterpreter.INSTANCE);
        registry.registerSubtypeInterpreter(ModItems.WAND_CORE.get(), WandCoreSubtypeInterpreter.INSTANCE);
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.add();
        registry.addRecipeClickArea(WandTableScreen.class, 89, 35, 22, 15, WandRecipeCategory.JEI_RECIPE_TYPE);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.WAND_TABLE.get()), WandRecipeCategory.JEI_RECIPE_TYPE);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<WandRecipe> wandRecipes = Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(ModRecipes.WAND_RECIPE.get());
        registration.addRecipes(WandRecipeCategory.JEI_RECIPE_TYPE, wandRecipes);
    }
}

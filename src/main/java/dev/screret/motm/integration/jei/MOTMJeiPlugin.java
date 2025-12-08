package dev.screret.motm.integration.jei;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;

import net.minecraft.resources.ResourceLocation;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;

@JeiPlugin
public class MOTMJeiPlugin implements IModPlugin {

    private static final ResourceLocation PLUGIN_ID = MOTMUtil.id("plugin");

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IModPlugin.super.registerCategories(registration);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IModPlugin.super.registerRecipes(registration);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        IModPlugin.super.registerItemSubtypes(registration);
    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        IModPlugin.super.registerExtraIngredients(registration);
    }

    @Override
    public void registerModInfo(IModInfoRegistration registration) {
        registration.addModAliases(MagicOfTheMind.MOD_ID, MagicOfTheMind.NAME);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_ID;
    }
}

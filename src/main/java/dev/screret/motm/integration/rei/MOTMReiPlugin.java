package dev.screret.motm.integration.rei;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.common.recipe.wand.WandRecipe;
import dev.screret.motm.data.MOTMDataComponents;
import dev.screret.motm.data.MOTMItems;
import dev.screret.motm.data.MOTMRecipeTypes;
import dev.screret.motm.integration.rei.wand.DefaultWandDisplay;
import dev.screret.motm.integration.rei.wand.WandRecipeCategory;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;

import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
@REIPluginClient
public class MOTMReiPlugin implements REIClientPlugin {

    @Override
    public String getPluginProviderName() {
        return "motm_rei_plugin";
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new WandRecipeCategory());
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(WandRecipe.class, MOTMRecipeTypes.WAND_RECIPE.get(), DefaultWandDisplay::new);
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        registry.addEntries(MOTMUtil.CUSTOM_WANDS.values().stream().map(EntryStacks::of).collect(Collectors.toList()));
        registry.addEntries(MOTMUtil.CUSTOM_WAND_CORES.values().stream().map(EntryStacks::of).collect(Collectors.toList()));
    }

    @Override
    public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {
        registry.group(MOTMUtil.id("wands"), Component.translatable("group.motm.wands"), entryStack -> {
            if (entryStack.getType() == VanillaEntryTypes.ITEM) {
                ItemStack stack = entryStack.castValue();
                return !stack.is(MOTMItems.WAND_CORE) && stack.has(MOTMDataComponents.WAND);
            }
            return false;
        });
        registry.group(MOTMUtil.id("wand_cores"), Component.translatable("group.motm.wand_cores"), entryStack -> {
            if (entryStack.getType() == VanillaEntryTypes.ITEM) {
                ItemStack stack = entryStack.castValue();
                return stack.is(MOTMItems.WAND_CORE) && stack.has(MOTMDataComponents.WAND_CORE);
            }
            return false;
        });
    }
}

package screret.sas.integration.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.forge.REIPluginClient;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import screret.sas.Util;
import screret.sas.api.capability.ability.ICapabilityWandAbility;
import screret.sas.integration.rei.wand.DefaultWandDisplay;
import screret.sas.integration.rei.wand.WandRecipeCategory;
import screret.sas.item.ModItems;
import screret.sas.recipe.ModRecipeTypes;
import screret.sas.recipe.recipe.WandRecipe;

import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
@REIPluginClient
public class SASReiPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return "sas_rei_plugin";
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new WandRecipeCategory());
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(WandRecipe.class, ModRecipeTypes.WAND_RECIPE.get(), DefaultWandDisplay::new);
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        registry.addEntries(Util.CUSTOM_WANDS.values().stream().map(EntryStacks::of).collect(Collectors.toList()));
        registry.addEntries(Util.CUSTOM_WAND_CORES.values().stream().map(EntryStacks::of).collect(Collectors.toList()));
    }

    @Override
    public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {
        registry.group(Util.id("wands"), Component.translatable("group.sas.wands"), entryStack -> {
            if (entryStack.getType() == VanillaEntryTypes.ITEM) {
                ItemStack itemStack = entryStack.castValue();
                if (itemStack.getCapability(ICapabilityWandAbility.WAND_ABILITY) != null) {
                    ICapabilityWandAbility cap = itemStack.getCapability(ICapabilityWandAbility.WAND_ABILITY);
                    return cap.getMainAbility() != null;
                }
            }
            return false;
        });
        registry.group(Util.id("wand_cores"), Component.translatable("group.sas.wand_cores"), entryStack -> {
            if (entryStack.getType() == VanillaEntryTypes.ITEM) {
                ItemStack itemStack = entryStack.castValue();
                if (itemStack.getItem() == ModItems.WAND_CORE.get()) {
                    return itemStack.hasTag() && itemStack.getTag().contains("ability", Tag.TAG_STRING);
                }
            }
            return false;
        });
    }
}

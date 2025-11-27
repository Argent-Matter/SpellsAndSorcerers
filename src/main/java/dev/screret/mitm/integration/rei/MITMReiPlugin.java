package dev.screret.mitm.integration.rei;

import dev.screret.mitm.data.MITMRecipeTypes;
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
import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.api.capability.ability.CapabilityWandAbility;
import dev.screret.mitm.integration.rei.wand.DefaultWandDisplay;
import dev.screret.mitm.integration.rei.wand.WandRecipeCategory;
import dev.screret.mitm.data.MITMItems;
import dev.screret.mitm.common.recipe.wand.WandRecipe;

import java.util.stream.Collectors;

@SuppressWarnings("UnstableApiUsage")
@REIPluginClient
public class MITMReiPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return "mitm_rei_plugin";
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new WandRecipeCategory());
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerRecipeFiller(WandRecipe.class, MITMRecipeTypes.WAND_RECIPE.get(), DefaultWandDisplay::new);
    }

    @Override
    public void registerEntries(EntryRegistry registry) {
        registry.addEntries(MITMUtil.CUSTOM_WANDS.values().stream().map(EntryStacks::of).collect(Collectors.toList()));
        registry.addEntries(MITMUtil.CUSTOM_WAND_CORES.values().stream().map(EntryStacks::of).collect(Collectors.toList()));
    }

    @Override
    public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {
        registry.group(MITMUtil.id("wands"), Component.translatable("group.mitm.wands"), entryStack -> {
            if (entryStack.getType() == VanillaEntryTypes.ITEM) {
                ItemStack itemStack = entryStack.castValue();
                if (itemStack.getCapability(CapabilityWandAbility.WAND_ABILITY) != null) {
                    CapabilityWandAbility cap = itemStack.getCapability(CapabilityWandAbility.WAND_ABILITY);
                    return cap.getMainAbility() != null;
                }
            }
            return false;
        });
        registry.group(MITMUtil.id("wand_cores"), Component.translatable("group.mitm.wand_cores"), entryStack -> {
            if (entryStack.getType() == VanillaEntryTypes.ITEM) {
                ItemStack itemStack = entryStack.castValue();
                if (itemStack.getItem() == MITMItems.WAND_CORE.get()) {
                    return itemStack.hasTag() && itemStack.getTag().contains("ability", Tag.TAG_STRING);
                }
            }
            return false;
        });
    }
}

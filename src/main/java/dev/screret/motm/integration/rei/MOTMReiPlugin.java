package dev.screret.motm.integration.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.registry.entry.CollapsibleEntryRegistry;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.forge.REIPluginClient;

@SuppressWarnings("UnstableApiUsage")
@REIPluginClient
public class MOTMReiPlugin implements REIClientPlugin {

    @Override
    public void registerCategories(CategoryRegistry registry) {}

    @Override
    public void registerDisplays(DisplayRegistry registry) {}

    @Override
    public void registerEntries(EntryRegistry registry) {}

    @Override
    public void registerCollapsibleEntries(CollapsibleEntryRegistry registry) {}
}

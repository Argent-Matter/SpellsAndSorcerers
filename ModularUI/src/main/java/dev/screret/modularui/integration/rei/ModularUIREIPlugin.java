package dev.screret.modularui.integration.rei;

import dev.screret.modularui.client.screen.ContainerScreenWrapper;
import dev.screret.modularui.client.screen.ScreenWrapper;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.screen.ExclusionZones;
import me.shedaniel.rei.api.client.registry.screen.ScreenRegistry;
import me.shedaniel.rei.forge.REIPluginClient;

@REIPluginClient
public class ModularUIREIPlugin implements REIClientPlugin {

    @Override
    public void registerExclusionZones(ExclusionZones zones) {
        zones.register(ScreenWrapper.class, REIScreenHandler.of(ScreenWrapper.class));
        zones.register(ContainerScreenWrapper.class, REIScreenHandler.of(ContainerScreenWrapper.class));
    }

    @Override
    public void registerScreens(ScreenRegistry registry) {
        registry.registerDraggableStackProvider(REIScreenHandler.of(ScreenWrapper.class));
        registry.registerDraggableStackVisitor(REIScreenHandler.of(ScreenWrapper.class).getDraggableVisitor());
    }
}

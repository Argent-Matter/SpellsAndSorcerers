package dev.screret.modularui.integration.emi;

import dev.screret.modularui.client.screen.ContainerScreenWrapper;
import dev.screret.modularui.client.screen.ScreenWrapper;
import dev.screret.modularui.integration.emi.handler.EmiScreenHandler;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class MuiEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addExclusionArea(ScreenWrapper.class, EmiScreenHandler.of(ScreenWrapper.class));
        registry.addExclusionArea(ContainerScreenWrapper.class, EmiScreenHandler.of(ContainerScreenWrapper.class));
    }
}

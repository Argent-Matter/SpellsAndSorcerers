package dev.screret.modularui.integration.emi;

import dev.screret.modularui.client.screen.ContainerScreenWrapper;
import dev.screret.modularui.client.screen.ScreenWrapper;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class ModularUIEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        EmiScreenHandler.register(ScreenWrapper.class, registry);
        EmiScreenHandler.register(ContainerScreenWrapper.class, registry);
    }
}

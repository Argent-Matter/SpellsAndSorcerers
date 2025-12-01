package dev.screret.mui.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.screret.mui.client.screen.ContainerScreenWrapper;
import dev.screret.mui.client.screen.ScreenWrapper;
import dev.screret.mui.integration.emi.handler.EmiScreenHandler;

@EmiEntrypoint
public class MuiEMIPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {
        registry.addExclusionArea(ScreenWrapper.class, EmiScreenHandler.of(ScreenWrapper.class));
        registry.addExclusionArea(ContainerScreenWrapper.class, EmiScreenHandler.of(ContainerScreenWrapper.class));
    }
}

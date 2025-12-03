package dev.screret.modularui.integration.jei;

import dev.screret.modularui.ModularUI;
import dev.screret.modularui.client.screen.ContainerScreenWrapper;
import dev.screret.modularui.client.screen.ScreenWrapper;
import dev.screret.modularui.integration.jei.handler.JeiContainerHandler;
import dev.screret.modularui.integration.jei.handler.JeiScreenHandler;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.resources.ResourceLocation;

import lombok.Getter;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@JeiPlugin
public class MuiJeiPlugin implements IModPlugin {

    @Getter
    private static IJeiRuntime runtime = null;

    @Override
    public ResourceLocation getPluginUid() {
        return ModularUI.id("jei_plugin");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        if (ModularUI.Mods.REI.isLoaded() || ModularUI.Mods.EMI.isLoaded()) return;
        registration.addGhostIngredientHandler(ScreenWrapper.class, JeiScreenHandler.of(ScreenWrapper.class));
        registration.addGhostIngredientHandler(ContainerScreenWrapper.class,
                JeiScreenHandler.of(ContainerScreenWrapper.class));
        registration.addGuiContainerHandler(ContainerScreenWrapper.class, JeiContainerHandler.INSTANCE);
    }
}

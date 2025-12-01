package dev.screret.mui.integration.jei;

import dev.screret.mui.ModularUI;
import dev.screret.mui.client.screen.ContainerScreenWrapper;
import dev.screret.mui.client.screen.ScreenWrapper;
import dev.screret.mui.integration.jei.handler.JEIContainerHandler;
import dev.screret.mui.integration.jei.handler.JEIScreenHandler;

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
public class MuiJEIPlugin implements IModPlugin {

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
        registration.addGhostIngredientHandler(ScreenWrapper.class, JEIScreenHandler.of(ScreenWrapper.class));
        registration.addGhostIngredientHandler(ContainerScreenWrapper.class,
                JEIScreenHandler.of(ContainerScreenWrapper.class));
        registration.addGuiContainerHandler(ContainerScreenWrapper.class, JEIContainerHandler.INSTANCE);
    }
}

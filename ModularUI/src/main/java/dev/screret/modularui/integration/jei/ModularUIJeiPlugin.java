package dev.screret.modularui.integration.jei;

import dev.screret.modularui.ModularUI;
import dev.screret.modularui.client.screen.ContainerScreenWrapper;
import dev.screret.modularui.client.screen.ScreenWrapper;

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
public class ModularUIJeiPlugin implements IModPlugin {

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

        JeiScreenHandler.register(ScreenWrapper.class, registration);
        JeiScreenHandler.register(ContainerScreenWrapper.class, registration);
    }
}

package dev.screret.modularui;

import dev.screret.modularui.client.screen.ContainerScreenWrapper;
import dev.screret.modularui.client.screen.ModularContainerMenu;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = ModularUI.MOD_ID, value = Dist.CLIENT)
public class ModularUIClientEvents {

    @SubscribeEvent
    public static void registerScreens(final RegisterMenuScreensEvent event) {
        event.<ModularContainerMenu, ContainerScreenWrapper>register(ModularUI.MODULAR_CONTAINER.get(),
                ContainerScreenWrapper::new);
    }
}

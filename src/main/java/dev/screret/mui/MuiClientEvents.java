package dev.screret.mui;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import dev.screret.mui.client.screen.ContainerScreenWrapper;
import dev.screret.mui.client.screen.ModularContainerMenu;

@EventBusSubscriber(modid = ModularUI.MOD_ID, value = Dist.CLIENT)
public class MuiClientEvents {


    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void registerScreens(final RegisterMenuScreensEvent event) {
        event.<ModularContainerMenu, ContainerScreenWrapper>register(ModularUI.MODULAR_CONTAINER.get(), ContainerScreenWrapper::new);
    }
}

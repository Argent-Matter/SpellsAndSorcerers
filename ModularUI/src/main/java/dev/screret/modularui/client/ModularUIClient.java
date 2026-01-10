package dev.screret.modularui.client;

import dev.screret.modularui.ModularUI;
import dev.screret.modularui.animation.AnimatorManager;
import dev.screret.modularui.client.screen.ContainerScreenWrapper;
import dev.screret.modularui.drawable.DrawableSerialization;
import dev.screret.modularui.factory.inventory.InventoryTypes;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.data.loading.DatagenModLoader;

@Mod(value = ModularUI.MOD_ID, dist = Dist.CLIENT)
public class ModularUIClient {

    public ModularUIClient(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.register(this);

        if (!DatagenModLoader.isRunningDataGen()) {
            CursorHandler.init();
            AnimatorManager.init();
            DrawableSerialization.init();
            InventoryTypes.init();
        }
    }

    @SubscribeEvent
    public void registerScreens(final RegisterMenuScreensEvent event) {
        event.register(ModularUI.MODULAR_CONTAINER.get(), ContainerScreenWrapper::new);
    }
}

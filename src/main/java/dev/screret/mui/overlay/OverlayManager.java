package dev.screret.mui.overlay;

import dev.screret.mui.ModularUI;
import dev.screret.mui.client.screen.ModularScreen;

import net.minecraft.client.gui.screens.Screen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
@EventBusSubscriber(modid = ModularUI.MOD_ID, value = Dist.CLIENT)
public class OverlayManager {

    public static final List<OverlayHandler> overlays = new ArrayList<>();

    public static void register(OverlayHandler handler) {
        if (!overlays.contains(handler)) {
            overlays.add(handler);
            overlays.sort(OverlayHandler::compareTo);
        }
    }

    public static void onOpenScreen(Screen newScreen) {
        // if (newScreen == event.getCurrentScreen()) return;
        OverlayStack.closeAll();
        for (OverlayHandler handler : overlays) {
            if (handler.isValidFor(newScreen)) {
                ModularScreen overlay = Objects.requireNonNull(handler.createOverlay(newScreen),
                        "Overlays must not be null!");
                overlay.constructOverlay(newScreen);
                OverlayStack.open(overlay);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCloseScreen(ScreenEvent.Closing event) {
        OverlayStack.closeAll();
    }
}

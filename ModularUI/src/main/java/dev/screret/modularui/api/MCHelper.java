package dev.screret.modularui.api;

import dev.screret.modularui.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;

public class MCHelper {

    @OnlyIn(Dist.CLIENT)
    public static Minecraft getMc() {
        return Minecraft.getInstance();
    }

    @OnlyIn(Dist.CLIENT)
    public static Player getPlayer() {
        return getMc().player;
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean closeScreen() {
        getMc().popGuiLayer();
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static void popScreen(boolean openParentOnClose, Screen parent) {
        Player player = MCHelper.getPlayer();
        if (player != null) {
            // container should not just be closed here
            // instead they are kept in a stack until all screens are closed
            // prepareCloseContainer(player);
            if (openParentOnClose) {
                Minecraft.getInstance().setScreen(parent);
                ModularNetwork.CLIENT.reopenSyncerOf(parent);
            } else {
                Minecraft.getInstance().setScreen(null);
            }
        } else {
            // we are currently not in a world and want to display the previous screen
            Minecraft.getInstance().setScreen(parent);
        }
    }

    public static void setScreen(Screen screen) {
        if (screen == null) {
            closeScreen();
        } else {
            getMc().setScreen(screen);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static Screen getCurrentScreen() {
        return getMc().screen;
    }

    @OnlyIn(Dist.CLIENT)
    public static Font getFont() {
        return getMc().font;
    }

    public static List<Component> getItemToolTip(ItemStack item) {
        return Screen.getTooltipFromItem(getMc(), item);
    }
}

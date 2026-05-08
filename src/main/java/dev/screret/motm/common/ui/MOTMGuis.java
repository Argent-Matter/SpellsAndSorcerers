package dev.screret.motm.common.ui;

import brachy.modularui.api.IPanelHandler;
import brachy.modularui.screen.ModularPanel;
import brachy.modularui.widgets.ButtonWidget;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import org.jetbrains.annotations.NotNull;

public class MOTMGuis {

    public static final int DEFAULT_WIDTH = 176, DEFAULT_HEIGHT = 166;

    public static ModularPanel<?> createPanel(String name, int width, int height) {
        return ModularPanel.defaultPanel(name, width, height);
    }

    public static ModularPanel<?> createPanel(ItemStack stack, int width, int height) {
        return createPanel(stack.getDescriptionId(), width, height);
    }

    public static ModularPanel<?> createPanel(String name) {
        return ModularPanel.defaultPanel(name, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static ModularPanel<?> defaultPanel(ItemStack stack) {
        return createPanel(stack, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static ModularPanel<?> defaultPanel(Item item) {
        return createPanel(item.getDescriptionId());
    }

    public static PopupPanel createPopupPanel(String name, int width, int height) {
        return defaultPopupPanel(name)
                .size(width, height);
    }

    public static PopupPanel createPopupPanel(String name, int width, int height, boolean deleteCachedPanel) {
        return createPopupPanel(name, width, height)
                .deleteCachedPanel(deleteCachedPanel);
    }

    public static PopupPanel defaultPopupPanel(String name) {
        return new PopupPanel(name)
                .size(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static PopupPanel defaultPopupPanel(String name, boolean disableBelow,
                                               boolean closeOnOutsideClick, boolean deleteCachedPanel) {
        return defaultPopupPanel(name)
                .disablePanelsBelow(disableBelow)
                .closeOnOutOfBoundsClick(closeOnOutsideClick)
                .deleteCachedPanel(deleteCachedPanel);
    }

    @Accessors(chain = true, fluent = true)
    public static class PopupPanel extends ModularPanel<PopupPanel> {

        @Getter
        @Setter
        private boolean disablePanelsBelow;
        @Getter
        @Setter
        private boolean closeOnOutOfBoundsClick;
        @Setter
        private boolean deleteCachedPanel;

        private PopupPanel(@NotNull String name) {
            super(name);
            background(MOTMGuiTextures.BACKGROUND);
            child(ButtonWidget.panelCloseButton().top(5).right(5)
                    .onMousePressed((ctx, button) -> {
                        if (button == 0 || button == 1) {
                            this.closeIfOpen();
                            return true;
                        }
                        return false;
                    }));
        }

        @Override
        public void onClose() {
            super.onClose();
            if (deleteCachedPanel && isSynced() && getSyncHandler() instanceof IPanelHandler handler) {
                handler.deleteCachedPanel();
            }
        }

        @Override
        public PopupPanel size(int w, int h) {
            super.size(w, h);
            return this;
        }

        @Override
        public PopupPanel size(int val) {
            super.size(val);
            return this;
        }
    }
}

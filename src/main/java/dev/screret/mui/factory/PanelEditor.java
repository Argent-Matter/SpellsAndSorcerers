package dev.screret.mui.factory;

import dev.screret.mui.api.machine.MetaMachine;
import dev.screret.mui.value.sync.PanelSyncManager;
import dev.screret.mui.client.screen.ModularPanel;
import dev.screret.mui.client.screen.UISettings;

@FunctionalInterface
public interface PanelEditor {

    void editUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings,
                MetaMachine machine, ModularPanel panel);
}

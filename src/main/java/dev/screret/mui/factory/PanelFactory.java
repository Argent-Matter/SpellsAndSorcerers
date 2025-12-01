package dev.screret.mui.factory;

import dev.screret.mui.api.machine.MetaMachine;
import dev.screret.mui.api.IUIHolder;
import dev.screret.mui.value.sync.PanelSyncManager;
import dev.screret.mui.client.screen.ModularPanel;
import dev.screret.mui.client.screen.UISettings;
import dev.screret.mui.common.mui.factory.MachineUIFactory;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

@FunctionalInterface
public interface PanelFactory extends IUIHolder<PosGuiData> {

    @Override
    default ModularPanel buildUI(PosGuiData data, PanelSyncManager syncManager, UISettings settings) {
        var machine = MachineUIFactory.getMachine(data);
        return buildUIFunction(data, syncManager, settings, machine);
    };

    ModularPanel buildUIFunction(PosGuiData data, PanelSyncManager syncManager, UISettings settings,
                                 MetaMachine machine);

    default boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return true;
    }

    default InteractionResult tryToOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        if (this.shouldOpenUI(player, hand, hit)) {
            if (player instanceof ServerPlayer serverPlayer) {
                MachineUIFactory.INSTANCE.open(serverPlayer, hit.getBlockPos());
            }
            return InteractionResult.sidedSuccess(player.level().isClientSide);
        } else {
            return InteractionResult.PASS;
        }
    }

    default PanelFactory andThen(PanelEditor... edits) {
        return (data, syncManager, settings, machine) -> {
            var panel = this.buildUIFunction(data, syncManager, settings, machine);
            for (PanelEditor edit : edits) {
                edit.editUI(data, syncManager, settings, machine, panel);
            }
            return panel;
        };
    }
}

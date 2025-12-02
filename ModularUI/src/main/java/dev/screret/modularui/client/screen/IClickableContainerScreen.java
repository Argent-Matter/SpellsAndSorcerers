package dev.screret.modularui.client.screen;

import net.minecraft.world.inventory.Slot;

public interface IClickableContainerScreen {

    void mui$setClickedSlot(Slot slot);

    Slot mui$getClickedSlot();
}

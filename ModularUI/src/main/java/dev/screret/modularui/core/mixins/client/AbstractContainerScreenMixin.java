package dev.screret.modularui.core.mixins.client;

import dev.screret.modularui.api.IMuiScreen;
import dev.screret.modularui.client.screen.IClickableContainerScreen;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin implements IClickableContainerScreen {

    @Shadow
    protected Slot hoveredSlot;

    @Unique
    private Slot mui$clickedSlot;

    /**
     * Mixin into ModularUI screen wrapper to return the true hovered slot.
     * The method is private and only the mouse pos is ever passed to this method.
     * That's why we can just return the current hovered slot.
     */
    @Inject(method = "findSlot", at = @At("HEAD"), cancellable = true)
    public void mui$injectGetSlot(double mouseX, double mouseY, CallbackInfoReturnable<Slot> cir) {
        if (this.mui$clickedSlot != null) {
            cir.setReturnValue(this.mui$clickedSlot);
        } else if (IMuiScreen.class.isAssignableFrom(this.getClass())) {
            cir.setReturnValue(this.hoveredSlot);
        }
    }

    @Override
    public void mui$setClickedSlot(Slot slot) {
        this.mui$clickedSlot = slot;
    }

    @Override
    public Slot mui$getClickedSlot() {
        return mui$clickedSlot;
    }
}

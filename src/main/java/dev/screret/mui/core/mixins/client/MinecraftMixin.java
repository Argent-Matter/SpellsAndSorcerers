package dev.screret.mui.core.mixins.client;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;

import dev.screret.mui.ModularUI;
import dev.screret.mui.client.screen.ClientScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Inject(method = "runTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/DeltaTracker$Timer;advanceTime(JZ)I", shift = At.Shift.AFTER))
    public void timer(CallbackInfo ci) {
        ModularUI.getTimer60Fps().advanceTime(Util.getMillis(), true);
        for (int j = 0; j < Math.min(20, ModularUI.getTimer60Fps().getGameTimeDeltaTicks()); ++j) {
            ClientScreenHandler.onFrameUpdate();
        }
    }
}

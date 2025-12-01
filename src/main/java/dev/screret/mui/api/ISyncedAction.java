package dev.screret.mui.api;

import net.minecraft.network.RegistryFriendlyByteBuf;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ISyncedAction {

    @ApiStatus.OverrideOnly
    void invoke(@NotNull RegistryFriendlyByteBuf packet);
}

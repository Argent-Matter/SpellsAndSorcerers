package dev.screret.modularui.utils.sides;

import dev.screret.modularui.ModularUI;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.common.extensions.ICommonPacketListener;
import net.neoforged.neoforge.network.connection.ConnectionType;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import io.netty.buffer.ByteBuf;

import org.jetbrains.annotations.Nullable;

public final class SidedAccessHelper {

    private SidedAccessHelper() {}

    public static RegistryAccess getRegistries() {
        if (ModularUI.isClientThread()) {
            return ClientCallWrapper.getClientRegistries();
        } else {
            return getServer().registryAccess();
        }
    }

    public static RecipeManager getRecipeManager() {
        if (ModularUI.isClientThread()) {
            return ClientCallWrapper.getClientRecipeManager();
        } else {
            return getServer().getRecipeManager();
        }
    }

    public static ICommonPacketListener getCommonPacketListener(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            return serverPlayer.connection;
        } else {
            return ClientCallWrapper.getCommonPacketListener();
        }
    }

    public static RegistryFriendlyByteBuf makeRegistryByteBuf(ByteBuf buffer) {
        return makeRegistryByteBuf(buffer, ConnectionType.NEOFORGE);
    }

    public static RegistryFriendlyByteBuf makeRegistryByteBuf(ByteBuf buffer, Player player) {
        return makeRegistryByteBuf(buffer, getCommonPacketListener(player).getConnectionType());
    }

    public static RegistryFriendlyByteBuf makeRegistryByteBuf(ByteBuf buffer, ConnectionType connectionType) {
        return new RegistryFriendlyByteBuf(buffer, getRegistries(), connectionType);
    }

    private static @Nullable MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }
}

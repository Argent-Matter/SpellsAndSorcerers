package com.gtceu.syncsystem;

import net.minecraft.client.Minecraft;
import net.neoforged.fml.loading.FMLEnvironment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class SyncSystem {

    public static final String MOD_ID = "syncsystem",
            MOD_NAME = "SyncSystem";

    public static final Logger LOGGER = LogManager.getLogger(MOD_NAME);

    private SyncSystem() {}

    /**
     * For async stuff use this, otherwise use {@link SyncSystem#isClientSide}
     *
     * @return if the current thread is the client thread
     */
    @SuppressWarnings("ConstantValue")
    public static boolean isClientThread() {
        return isClientSide() && Minecraft.getInstance() != null && Minecraft.getInstance().isSameThread();
    }

    /**
     * @return if the game is the <strong>PHYSICAL</strong> client, e.g. not a dedicated server.
     * @apiNote Do not use this to check if you're currently on the server thread for side-specific actions!
     *          It does <strong>NOT</strong> work for that. Use {@link #isClientThread()} instead.
     * @see #isClientThread()
     */
    public static boolean isClientSide() {
        return FMLEnvironment.dist.isClient();
    }
}

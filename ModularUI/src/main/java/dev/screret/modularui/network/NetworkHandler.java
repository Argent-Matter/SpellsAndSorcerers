package dev.screret.modularui.network;

import dev.screret.modularui.ModularUI;
import dev.screret.modularui.network.packets.CloseAllGuisPacket;
import dev.screret.modularui.network.packets.OpenGuiPacket;
import dev.screret.modularui.network.packets.SyncHandlerPacket;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = ModularUI.MOD_ID)
public class NetworkHandler {

    public static final String NETWORK_VERSION = "1.0.0";

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(NETWORK_VERSION);
        // spotless:off
        registrar.playBidirectional(OpenGuiPacket.TYPE, OpenGuiPacket.CODEC, OpenGuiPacket::execute);
        registrar.playBidirectional(SyncHandlerPacket.TYPE, SyncHandlerPacket.CODEC, SyncHandlerPacket::execute);
        registrar.playBidirectional(CloseAllGuisPacket.TYPE, CloseAllGuisPacket.CODEC, CloseAllGuisPacket::execute);
        // spotless:on
    }
}

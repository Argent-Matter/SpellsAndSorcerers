package dev.screret.motm.common.network;

import dev.screret.motm.common.network.packets.RequestStructurePacket;
import dev.screret.motm.common.network.packets.SendStructurePacket;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber
public class MOTMNetworkHandler {

    public static final String NETWORK_VERSION = "1";

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(NETWORK_VERSION);
        // spotless:off
        registrar.playToServer(RequestStructurePacket.TYPE, RequestStructurePacket.CODEC, RequestStructurePacket::execute);
        registrar.playToClient(SendStructurePacket.TYPE, SendStructurePacket.CODEC, SendStructurePacket::execute);
        // spotless:on
    }
}

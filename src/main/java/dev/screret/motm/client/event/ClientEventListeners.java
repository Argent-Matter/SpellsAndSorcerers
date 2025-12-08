package dev.screret.motm.client.event;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.client.particle.EyeParticle;
import dev.screret.motm.client.renderer.blockentity.PalantirBERenderer;
import dev.screret.motm.data.*;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = MagicOfTheMind.MOD_ID, value = Dist.CLIENT)
public class ClientEventListeners {

    // region mod bus events

    @SubscribeEvent
    public static void registerScreens(final RegisterMenuScreensEvent event) {
    }

    @SubscribeEvent
    public static void registerParticles(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(MOTMParticles.EYE.get(), EyeParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MOTMBlockEntities.PALANTIR.get(), context -> new PalantirBERenderer());
    }

    @SubscribeEvent
    public static void registerGuiOverlay(final RegisterGuiLayersEvent event) {}

    @SubscribeEvent
    public static void registerItemColors(final RegisterColorHandlersEvent.Item event) {}

    @SubscribeEvent
    public static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {}

    @SubscribeEvent
    public static void registerClientExtensions(final RegisterClientExtensionsEvent event) {}

    // endregion

    // region forge bus events

    // endregion
}

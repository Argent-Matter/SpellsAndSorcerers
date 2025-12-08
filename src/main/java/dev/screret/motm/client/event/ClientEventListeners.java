package dev.screret.motm.client.event;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.registry.MOTMRegistries;
import dev.screret.motm.client.gui.overlay.ManaBarOverlay;
import dev.screret.motm.client.model.item.WandItemClientExtensions;
import dev.screret.motm.client.model.item.WandModel;
import dev.screret.motm.client.particle.EyeParticle;
import dev.screret.motm.client.renderer.blockentity.PalantirBERenderer;
import dev.screret.motm.client.renderer.entity.BossWizardRenderer;
import dev.screret.motm.client.renderer.entity.WizardRenderer;
import dev.screret.motm.common.ability.SubAbility;
import dev.screret.motm.data.*;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
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
        event.registerEntityRenderer(MOTMEntityTypes.WIZARD.get(), WizardRenderer::new);
        event.registerEntityRenderer(MOTMEntityTypes.BOSS_WIZARD.get(), BossWizardRenderer::new);

        event.registerBlockEntityRenderer(MOTMBlockEntities.PALANTIR.get(), context -> new PalantirBERenderer());
    }

    @SubscribeEvent
    public static void onRegisterGeometryLoaders(final ModelEvent.RegisterGeometryLoaders event) {
        event.register(MOTMUtil.id("wand"), WandModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerModels(final ModelEvent.RegisterAdditional event) {
        MOTMRegistries.WAND_ABILITIES.holders().forEach(ability -> {
            if (ability.value() instanceof SubAbility) {
                event.register(ModelResourceLocation.standalone(ability.key().location().withPrefix("item/wand/")));
            }

        });
    }

    @SubscribeEvent
    public static void registerTextures(final TextureAtlasStitchedEvent event) {
        TextureAtlas map = event.getAtlas();

        if (map.location() == InventoryMenu.BLOCK_ATLAS) {
            MOTMRegistries.WAND_ABILITIES.holders().forEach(ability -> {
                event.getAtlas().getSprite(ability.key().location().withPrefix("item/wand/"));
            });
        }
    }

    @SubscribeEvent
    public static void registerGuiOverlay(final RegisterGuiLayersEvent event) {
        event.registerAbove(ResourceLocation.withDefaultNamespace("armor_level"), MOTMUtil.id("mana"), new ManaBarOverlay());
    }

    @SubscribeEvent
    public static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
        event.register((stack, layer) -> {
            var component = stack.get(MOTMDataComponents.WAND_CORE);
            if (component != null && layer == 1) {
                return component.getAbility().getColor();
            }
            return 0xFFFFFFFF;
        }, MOTMItems.WAND_CORE.get());
    }

    @SubscribeEvent
    public static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {}

    @SubscribeEvent
    public static void registerClientExtensions(final RegisterClientExtensionsEvent event) {
        event.registerItem(new WandItemClientExtensions(), MOTMItems.WAND);
    }

    // endregion

    // region forge bus events

    // endregion
}

package dev.screret.mitm.client.event;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.api.registry.MITMRegistries;
import dev.screret.mitm.client.gui.overlay.ManaBarOverlay;
import dev.screret.mitm.client.gui.screen.PotionDistilleryScreen;
import dev.screret.mitm.client.gui.screen.WandTableScreen;
import dev.screret.mitm.client.model.item.WandItemClientExtensions;
import dev.screret.mitm.client.model.item.WandModel;
import dev.screret.mitm.client.particle.EyeParticle;
import dev.screret.mitm.client.renderer.blockentity.PalantirBERenderer;
import dev.screret.mitm.client.renderer.blockentity.SummoningCircleBERenderer;
import dev.screret.mitm.client.renderer.entity.BossWizardRenderer;
import dev.screret.mitm.client.renderer.entity.WizardRenderer;
import dev.screret.mitm.common.ability.SubAbility;
import dev.screret.mitm.common.block.SummoningCircleBlock;
import dev.screret.mitm.data.*;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

@EventBusSubscriber(modid = MagicOfTheMind.MODID, value = Dist.CLIENT)
public class ClientEventListeners {

    // region mod bus events

    @SubscribeEvent
    public static void registerScreens(final RegisterMenuScreensEvent event) {
        event.register(MITMContainers.WAND_TABLE.get(), WandTableScreen::new);
        event.register(MITMContainers.POTION_DISTILLERY.get(), PotionDistilleryScreen::new);
    }

    @SubscribeEvent
    public static void registerParticles(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(MITMParticles.EYE.get(), EyeParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(MITMEntityTypes.WIZARD.get(), WizardRenderer::new);
        event.registerEntityRenderer(MITMEntityTypes.BOSS_WIZARD.get(), BossWizardRenderer::new);

        event.registerBlockEntityRenderer(MITMBlockEntities.SUMMONING_CIRCLE.get(), context -> new SummoningCircleBERenderer());
        event.registerBlockEntityRenderer(MITMBlockEntities.PALANTIR.get(), context -> new PalantirBERenderer());
    }

    @SubscribeEvent
    public static void onRegisterGeometryLoaders(final ModelEvent.RegisterGeometryLoaders event) {
        event.register(MITMUtil.id("wand"), WandModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerModels(final ModelEvent.RegisterAdditional event) {
        MITMRegistries.WAND_ABILITIES.holders().forEach(ability -> {
            if (ability.value() instanceof SubAbility) {
                event.register(ModelResourceLocation.standalone(ability.key().location().withPrefix("item/wand/")));
            }

        });
    }

    @SubscribeEvent
    public static void registerTextures(final TextureAtlasStitchedEvent event) {
        TextureAtlas map = event.getAtlas();

        if (map.location() == InventoryMenu.BLOCK_ATLAS) {
            MITMRegistries.WAND_ABILITIES.holders().forEach(ability -> {
                event.getAtlas().getSprite(ability.key().location().withPrefix("item/wand/"));
            });
        }
    }

    @SubscribeEvent
    public static void registerGuiOverlay(final RegisterGuiLayersEvent event) {
        event.registerAbove(ResourceLocation.withDefaultNamespace("armor_level"), MITMUtil.id("mana"), new ManaBarOverlay());
    }

    @SubscribeEvent
    public static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
        event.register((stack, layer) -> {
            var component = stack.get(MITMDataComponents.WAND_CORE);
            if (component != null && layer == 1) {
                return component.getAbility().getColor();
            }
            return 0xFFFFFFFF;
        }, MITMItems.WAND_CORE.get());
    }

    @SubscribeEvent
    public static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, layer) -> state.getValue(SummoningCircleBlock.COLOR).getTextureDiffuseColor(),
                MITMBlocks.SUMMONING_CIRCLE.get());
    }

    @SubscribeEvent
    public static void registerClientExtensions(final RegisterClientExtensionsEvent event) {
        event.registerItem(new WandItemClientExtensions(), MITMItems.WAND);
    }

    // endregion

    // region forge bus events

    // endregion
}

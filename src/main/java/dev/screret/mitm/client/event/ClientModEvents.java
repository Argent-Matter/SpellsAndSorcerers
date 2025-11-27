package dev.screret.mitm.client.event;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.common.ability.SubAbility;
import dev.screret.mitm.api.registry.MITMRegistries;
import dev.screret.mitm.data.MITMBlocks;
import dev.screret.mitm.common.block.SummonSignBlock;
import dev.screret.mitm.data.MITMBlockEntities;
import dev.screret.mitm.client.gui.overlay.ManaBarOverlay;
import dev.screret.mitm.client.gui.screen.PotionDistilleryScreen;
import dev.screret.mitm.client.gui.screen.WandTableScreen;
import dev.screret.mitm.client.model.item.WandModel;
import dev.screret.mitm.data.MITMParticles;
import dev.screret.mitm.client.particle.EyeParticle;
import dev.screret.mitm.client.renderer.blockentity.PalantirBERenderer;
import dev.screret.mitm.client.renderer.blockentity.SummonSignBERenderer;
import dev.screret.mitm.client.renderer.entity.BossWizardRenderer;
import dev.screret.mitm.client.renderer.entity.WizardRenderer;
import dev.screret.mitm.data.MITMContainers;
import dev.screret.mitm.data.MITMEntities;
import dev.screret.mitm.data.MITMItems;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = MagicOfTheMind.MODID, value = Dist.CLIENT)
public class ClientModEvents {

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
        event.registerEntityRenderer(MITMEntities.WIZARD.get(), WizardRenderer::new);
        event.registerEntityRenderer(MITMEntities.BOSS_WIZARD.get(), BossWizardRenderer::new);

        event.registerBlockEntityRenderer(MITMBlockEntities.SUMMON_SIGN.get(), context -> new SummonSignBERenderer());
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
                event.register(ModelResourceLocation.inventory(ability.key().location().withPrefix("item/wand/")));
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
    public static void registerGuiOverlay(final RegisterGuiOverlaysEvent event) {
        event.registerAbove(ResourceLocation.withDefaultNamespace("armor_level"), MITMUtil.id("mana"), new ManaBarOverlay());
    }

    @SubscribeEvent
    public static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
        event.register((stack, index) -> {
            if (stack.hasTag() && stack.getTag().contains("ability") && index == 1) {
                var colorLocation = ResourceLocation.parse(stack.getTag().getString("ability"));
                if (MITMRegistries.WAND_ABILITIES.containsKey(colorLocation)) {
                    return MITMRegistries.WAND_ABILITIES.get(colorLocation).getColor();
                }
            }
            return 0xFFFFFFFF;
        }, MITMItems.WAND_CORE.get());

        event.register((itemStack, layer) -> {
            BlockState blockstate = ((BlockItem) itemStack.getItem()).getBlock().defaultBlockState();
            return event.getBlockColors().getColor(blockstate, null, null, layer);
        }, MITMItems.SUMMON_SIGN.get());
    }

    @SubscribeEvent
    public static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
        event.register((pState, pLevel, pPos, pTintIndex) -> pState.getValue(SummonSignBlock.COLOR).getFireworkColor(), MITMBlocks.SUMMON_SIGN.get());
    }
}

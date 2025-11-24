package dev.screret.sas.client.event;

import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;
import dev.screret.sas.SpellsAndSorcerers;
import dev.screret.sas.Util;
import dev.screret.sas.ability.SubAbility;
import dev.screret.sas.api.wand.ability.WandAbilityRegistry;
import dev.screret.sas.data.ModBlocks;
import dev.screret.sas.block.SummonSignBlock;
import dev.screret.sas.data.ModBlockEntities;
import dev.screret.sas.client.gui.overlay.ManaBarOverlay;
import dev.screret.sas.client.gui.screen.PotionDistilleryScreen;
import dev.screret.sas.client.gui.screen.WandTableScreen;
import dev.screret.sas.client.model.item.WandModel;
import dev.screret.sas.data.ModParticles;
import dev.screret.sas.client.particle.EyeParticle;
import dev.screret.sas.client.renderer.blockentity.PalantirBERenderer;
import dev.screret.sas.client.renderer.blockentity.SummonSignBERenderer;
import dev.screret.sas.client.renderer.entity.BossWizardRenderer;
import dev.screret.sas.client.renderer.entity.WizardRenderer;
import dev.screret.sas.data.ModContainers;
import dev.screret.sas.data.ModEntities;
import dev.screret.sas.data.ModItems;

@SuppressWarnings("unused")
@Mod.EventBusSubscriber(modid = SpellsAndSorcerers.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    @SubscribeEvent
    public static void registerScreens(final RegisterMenuScreensEvent event) {
        event.register(ModContainers.WAND_TABLE.get(), WandTableScreen::new);
        event.register(ModContainers.POTION_DISTILLERY.get(), PotionDistilleryScreen::new);
    }

    @SubscribeEvent
    public static void registerParticles(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.EYE.get(), EyeParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.WIZARD.get(), WizardRenderer::new);
        event.registerEntityRenderer(ModEntities.BOSS_WIZARD.get(), BossWizardRenderer::new);

        event.registerBlockEntityRenderer(ModBlockEntities.SUMMON_SIGN.get(), context -> new SummonSignBERenderer());
        event.registerBlockEntityRenderer(ModBlockEntities.PALANTIR.get(), context -> new PalantirBERenderer());
    }

    @SubscribeEvent
    public static void onRegisterGeometryLoaders(final ModelEvent.RegisterGeometryLoaders event) {
        event.register(Util.id("wand"), WandModel.Loader.INSTANCE);
    }

    @SubscribeEvent
    public static void registerModels(final ModelEvent.RegisterAdditional event) {
        SASRegistries.WAND_ABILITIES.holders().forEach(ability -> {
            if (ability.value() instanceof SubAbility) {
                event.register(ModelResourceLocation.inventory(ability.key().location().withPrefix("item/wand/")));
            }

        });
    }

    @SubscribeEvent
    public static void registerTextures(final TextureAtlasStitchedEvent event) {
        TextureAtlas map = event.getAtlas();

        if (map.location() == InventoryMenu.BLOCK_ATLAS) {
            SASRegistries.WAND_ABILITIES.holders().forEach(ability -> {
                event.getAtlas().getSprite(ability.key().location().withPrefix("item/wand/"));
            });
        }
    }

    @SubscribeEvent
    public static void registerGuiOverlay(final RegisterGuiOverlaysEvent event) {
        event.registerAbove(ResourceLocation.withDefaultNamespace("armor_level"), Util.id("mana"), new ManaBarOverlay());
    }

    @SubscribeEvent
    public static void registerItemColors(final RegisterColorHandlersEvent.Item event) {
        event.register((stack, index) -> {
            if (stack.hasTag() && stack.getTag().contains("ability") && index == 1) {
                var colorLocation = ResourceLocation.parse(stack.getTag().getString("ability"));
                if (SASRegistries.WAND_ABILITIES.containsKey(colorLocation)) {
                    return SASRegistries.WAND_ABILITIES.get(colorLocation).getColor();
                }
            }
            return 0xFFFFFFFF;
        }, ModItems.WAND_CORE.get());

        event.register((itemStack, layer) -> {
            BlockState blockstate = ((BlockItem) itemStack.getItem()).getBlock().defaultBlockState();
            return event.getBlockColors().getColor(blockstate, null, null, layer);
        }, ModItems.SUMMON_SIGN.get());
    }

    @SubscribeEvent
    public static void registerBlockColors(final RegisterColorHandlersEvent.Block event) {
        event.register((pState, pLevel, pPos, pTintIndex) -> pState.getValue(SummonSignBlock.COLOR).getFireworkColor(), ModBlocks.SUMMON_SIGN.get());
    }
}

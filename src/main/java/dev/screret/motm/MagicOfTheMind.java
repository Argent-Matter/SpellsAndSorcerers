package dev.screret.motm;

import dev.screret.motm.api.registry.MOTMRegistries;
import dev.screret.motm.common.data.provider.lang.*;
import dev.screret.motm.common.data.provider.model.*;
import dev.screret.motm.common.data.provider.recipe.*;
import dev.screret.motm.common.data.provider.tag.*;
import dev.screret.motm.config.MOTMConfig;
import dev.screret.motm.data.*;
import dev.screret.motm.data.memory.*;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Mod(MagicOfTheMind.MOD_ID)
@EventBusSubscriber
public class MagicOfTheMind {

    public static final String MOD_ID = "motm";
    public static final String NAME = "Magic of the Mind";
    public static final Logger LOGGER = LogManager.getLogger(NAME);

    public MagicOfTheMind(IEventBus modEventBus, ModContainer modContainer) {
        MOTMArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        MOTMDataComponents.DATA_COMPONENTS.register(modEventBus);

        MOTMBlocks.BLOCKS.register(modEventBus);
        MOTMItems.ITEMS.register(modEventBus);

        MOTMRecipeTypes.RECIPE_TYPES.register(modEventBus);
        MOTMRecipeTypes.RECIPE_SERIALIZERS.register(modEventBus);
        MOTMIngredientTypes.INGREDIENT_TYPES.register(modEventBus);

        MOTMAttributes.ATTRIBUTES.register(modEventBus);
        MOTMAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        MOTMMobEffects.EFFECTS.register(modEventBus);
        MOTMPotions.POTIONS.register(modEventBus);

        MOTMMenuTypes.MENU_TYPES.register(modEventBus);

        MOTMEntityTypes.ENTITY_TYPES.register(modEventBus);
        MOTMBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        MOTMParticles.PARTICLES.register(modEventBus);

        MOTMCreativeTabs.CREATIVE_TABS.register(modEventBus);

        MOTMMemoryAnimationKeyframeTypes.MEMORY_ANIMATION_KEYFRAME_TYPES.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.CLIENT, MOTMConfig.Client.CLIENT_CONFIG);
        modContainer.registerConfig(ModConfig.Type.SERVER, MOTMConfig.Server.SERVER_CONFIG);
    }

    // region mod bus events

    @SubscribeEvent
    public static void addVanillaTabItems(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(MOTMItems.SOULSTEEL_AXE.get());
            event.accept(MOTMItems.SOULSTEEL_SWORD.get());
            event.accept(MOTMItems.SOULSTEEL_HELMET.get());
            event.accept(MOTMItems.SOULSTEEL_CHESTPLATE.get());
            event.accept(MOTMItems.SOULSTEEL_LEGGINGS.get());
            event.accept(MOTMItems.SOULSTEEL_BOOTS.get());
        } else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(MOTMItems.SOULSTEEL_AXE.get());
            event.accept(MOTMItems.SOULSTEEL_HOE.get());
            event.accept(MOTMItems.SOULSTEEL_PICKAXE.get());
            event.accept(MOTMItems.SOULSTEEL_SHOVEL.get());
        } else if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(MOTMItems.SOULSTEEL_BLOCK.get());
        } else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(MOTMItems.PALANTIR.get());
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(MOTMItems.HANDLE.get());
            event.accept(MOTMItems.CLOUD_BOTTLE.get());
            event.accept(MOTMItems.SOUL_BOTTLE.get());
            event.accept(MOTMItems.SOULSTEEL_INGOT.get());
        }
    }

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent event) {
        DataProvider.INDENT_WIDTH.set(4);

        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();

        registries = gen.addProvider(true, new DatapackBuiltinEntriesProvider(packOutput, registries,
                new RegistrySetBuilder()
                        .add(Registries.ENCHANTMENT, MOTMEnchantments::bootstrap)
                        .add(MOTMRegistries.MEMORY_REGISTRY, MOTMMemories::bootstrap),
                Set.of(MagicOfTheMind.MOD_ID)))
                .getRegistryProvider();

        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        MOTMBlockTagsProvider blockTags = new MOTMBlockTagsProvider(packOutput, registries, existingFileHelper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(),
                new MOTMItemTagsProvider(packOutput, registries, blockTags.contentsGetter(), existingFileHelper));

        gen.addProvider(event.includeServer(), new MOTMRecipeProvider(packOutput, registries));

        gen.addProvider(event.includeServer(), new MOTMBiomeTagsProvider(packOutput, registries, existingFileHelper));
        gen.addProvider(event.includeServer(), new MOTMEntityTypeTagsProvider(packOutput, registries, existingFileHelper));

        gen.addProvider(event.includeClient(), new MOTMLangProvider(packOutput, MagicOfTheMind.MOD_ID, "en_us"));
        gen.addProvider(event.includeClient(), new MOTMBlockStateProvider(packOutput, existingFileHelper));
        gen.addProvider(event.includeClient(), new MOTMItemModelProvider(packOutput, existingFileHelper));
    }

    @SubscribeEvent
    public static void registerCapabilities(final RegisterCapabilitiesEvent event) {}

    @SubscribeEvent
    public static void registerEntityAttributes(final EntityAttributeCreationEvent event) {}

    // endregion

    // region game bus events

    @SubscribeEvent
    public static void registerVanillaEntityAttributes(final EntityAttributeModificationEvent event) {}

    @SubscribeEvent
    public static void registerBrewingRecipes(final RegisterBrewingRecipesEvent event) {
        MOTMPotions.registerPotionMixes(event);
    }

    @SubscribeEvent
    public static void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity().getItemInHand(event.getHand()).is(MOTMTags.Items.GLASS_BOTTLES)) {
            if (event.getLevel().getBlockState(event.getHitVec().getBlockPos()).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
                event.getEntity().awardStat(Stats.ITEM_USED.get(event.getEntity().getUseItem().getItem()));
                ItemUtils.createFilledResult(event.getEntity().getUseItem(), event.getEntity(),
                        new ItemStack(MOTMItems.SOUL_BOTTLE.get()));
            } else if (event.getEntity().getY() > 320 - 16) {
                event.getEntity().awardStat(Stats.ITEM_USED.get(event.getEntity().getUseItem().getItem()));
                ItemUtils.createFilledResult(event.getEntity().getUseItem(), event.getEntity(),
                        new ItemStack(MOTMItems.CLOUD_BOTTLE.get()));
            }
        }
    }

    // endregion
}

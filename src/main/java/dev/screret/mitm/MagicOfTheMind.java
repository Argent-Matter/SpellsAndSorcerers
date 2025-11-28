package dev.screret.mitm;

import com.mojang.logging.LogUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import dev.screret.mitm.client.model.item.WandItemClientExtensions;
import dev.screret.mitm.common.data.provider.lang.MITMLangProvider;
import dev.screret.mitm.common.data.provider.tag.MITMEntityTypeTagsProvider;
import dev.screret.mitm.data.*;
import org.slf4j.Logger;
import dev.screret.mitm.api.capability.mana.Mana;
import dev.screret.mitm.api.registry.MITMRegistries;
import dev.screret.mitm.common.block.entity.PotionDistilleryBlockEntity;
import dev.screret.mitm.config.MITMConfig;
import dev.screret.mitm.common.data.provider.conversion.EyeConversionProvider;
import dev.screret.mitm.common.data.provider.recipe.MITMRecipeProvider;
import dev.screret.mitm.common.data.provider.tag.MITMBiomeTagsProvider;
import dev.screret.mitm.common.data.provider.tag.MITMBlockTagsProvider;
import dev.screret.mitm.common.data.provider.tag.MITMItemTagsProvider;
import dev.screret.mitm.common.entity.BossWizardEntity;
import dev.screret.mitm.common.entity.WizardEntity;
import dev.screret.mitm.data.MITMIngredientTypes;
import dev.screret.mitm.common.data.EyeConversionManager;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MagicOfTheMind.MODID)
@EventBusSubscriber(modid = MagicOfTheMind.MODID)
public class MagicOfTheMind {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "mitm";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public MagicOfTheMind(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::registerRegistries);
        modEventBus.addListener(this::addItemsVanillaTabs);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerEntityAttributes);
        modEventBus.addListener(this::registerVanillaEntityAttributes);

        MITMWandAbilities.WAND_ABILITIES.register(modEventBus);
        MITMArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        MITMDataComponents.DATA_COMPONENTS.register(modEventBus);

        MITMBlocks.BLOCKS.register(modEventBus);
        MITMItems.ITEMS.register(modEventBus);

        MITMEnchantments.ENCHANTS.register(modEventBus);
        MITMEnchantments.ENCHANTS_MINECRAFT.register(modEventBus);

        MITMRecipeTypes.RECIPE_TYPES.register(modEventBus);
        MITMRecipeTypes.RECIPE_SERIALIZERS.register(modEventBus);
        MITMIngredientTypes.INGREDIENT_TYPES.register(modEventBus);

        MITMAttributes.ATTRIBUTES.register(modEventBus);
        MITMAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        MITMMobEffects.EFFECTS.register(modEventBus);
        MITMPotions.POTIONS.register(modEventBus);

        MITMContainers.MENU_TYPES.register(modEventBus);

        MITMEntityTypes.ENTITY_TYPES.register(modEventBus);
        MITMBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        MITMParticles.PARTICLES.register(modEventBus);

        MITMCreativeTabs.CREATIVE_TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.CLIENT, MITMConfig.Client.clientSpec);
        modContainer.registerConfig(ModConfig.Type.SERVER, MITMConfig.Server.serverSpec);
    }

    // region mod bus events

    @SubscribeEvent
    private void registerRegistries(final NewRegistryEvent event) {
        event.register(MITMRegistries.WAND_ABILITIES);
    }

    @SubscribeEvent
    public void addItemsVanillaTabs(final BuildCreativeModeTabContentsEvent event) {
        MITMUtil.generateWandItems();
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(MITMItems.SOULSTEEL_AXE.get());
            event.accept(MITMItems.SOULSTEEL_SWORD.get());
            event.accept(MITMItems.SOULSTEEL_HELMET.get());
            event.accept(MITMItems.SOULSTEEL_CHESTPLATE.get());
            event.accept(MITMItems.SOULSTEEL_LEGGINGS.get());
            event.accept(MITMItems.SOULSTEEL_BOOTS.get());
            event.acceptAll(MITMUtil.CUSTOM_WANDS.values());
            event.acceptAll(MITMUtil.CUSTOM_WAND_CORES.values());
        } else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(MITMItems.SOULSTEEL_AXE.get());
            event.accept(MITMItems.SOULSTEEL_HOE.get());
            event.accept(MITMItems.SOULSTEEL_PICKAXE.get());
            event.accept(MITMItems.SOULSTEEL_SHOVEL.get());
            event.accept(MITMItems.CTHULHU_EYE.get());
        } else if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(MITMItems.SOULSTEEL_BLOCK.get());
        } else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(MITMItems.PALANTIR.get());
            event.accept(MITMItems.POTION_DISTILLERY.get());
            event.accept(MITMItems.WAND_TABLE.get());
        } else if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(MITMItems.POTION_DISTILLERY.get());
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(MITMItems.HANDLE.get());
            event.accept(MITMItems.CLOUD_BOTTLE.get());
            event.accept(MITMItems.SOUL_BOTTLE.get());
            event.accept(MITMItems.SOULSTEEL_INGOT.get());
        } else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(MITMItems.WIZARD_SPAWN_EGG.get());
            event.accept(MITMItems.BOSS_WIZARD_SPAWN_EGG.get());
        }
    }

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> registries = event.getLookupProvider();

        DatapackBuiltinEntriesProvider provider = gen.addProvider(true, new DatapackBuiltinEntriesProvider(
                packOutput, registries, new RegistrySetBuilder()
                        .add(Registries.ENCHANTMENT, MITMEnchantments::bootstrap),
                Set.of(MagicOfTheMind.MODID)
        ));
        registries = provider.getRegistryProvider();

        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        MITMBlockTagsProvider blockTags = new MITMBlockTagsProvider(packOutput, registries, existingFileHelper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new MITMItemTagsProvider(packOutput, registries, blockTags.contentsGetter(), existingFileHelper));

        gen.addProvider(event.includeServer(), new MITMRecipeProvider(packOutput, registries));
        gen.addProvider(event.includeServer(), new EyeConversionProvider(packOutput));

        gen.addProvider(event.includeServer(), new MITMBiomeTagsProvider(packOutput, registries, existingFileHelper));
        gen.addProvider(event.includeServer(), new MITMEntityTypeTagsProvider(packOutput, registries, existingFileHelper));

        //gen.addProvider(event.includeServer(), new ModBlockstateProvider(gen, existingFileHelper));

        gen.addProvider(event.includeClient(), new MITMLangProvider(packOutput, MagicOfTheMind.MODID, "en_us"));
    }

    @SubscribeEvent
    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, MITMBlockEntities.POTION_DISTILLERY.get(), PotionDistilleryBlockEntity::getItemHandler);
    }

    @SubscribeEvent
    public void registerEntityAttributes(final EntityAttributeCreationEvent event) {
        event.put(MITMEntityTypes.WIZARD.get(), WizardEntity.createAttributes().build());
        event.put(MITMEntityTypes.BOSS_WIZARD.get(), BossWizardEntity.createAttributes().build());
    }

    // endregion

    // region forge bus events

    @SubscribeEvent
    public void registerVanillaEntityAttributes(final EntityAttributeModificationEvent event) {
        if (!event.has(EntityType.PLAYER, MITMAttributes.MANA)) {
            event.add(EntityType.PLAYER, MITMAttributes.MANA);
        }
    }

    @SubscribeEvent
    public static void registerReloadListeners(final AddReloadListenerEvent event) {
        EyeConversionManager.INSTANCE = new EyeConversionManager();
        event.addListener(EyeConversionManager.INSTANCE);
    }

    @SubscribeEvent
    public static void registerBrewingRecipes(final RegisterBrewingRecipesEvent event) {
        MITMPotions.registerPotionMixes(event);
    }

    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent.Post event) {
        if (event.getEntity().tickCount % 20 == 0) {
            AttributeInstance manaAttribute = event.getEntity().getAttribute(MITMAttributes.MANA);
            Mana mana = event.getEntity().getData(MITMAttachmentTypes.MANA);

            mana.setMaxManaStored(Mth.floor(manaAttribute.getValue()));
            mana.addMana(1, false);
            event.getEntity().setData(MITMAttachmentTypes.MANA, mana);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity().getItemInHand(event.getHand()).is(MITMTags.Items.GLASS_BOTTLES)) {
            if (event.getLevel().getBlockState(event.getHitVec().getBlockPos()).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
                event.getEntity().awardStat(Stats.ITEM_USED.get(event.getEntity().getUseItem().getItem()));
                ItemUtils.createFilledResult(event.getEntity().getUseItem(), event.getEntity(), new ItemStack(MITMItems.SOUL_BOTTLE.get()));
            } else if (event.getEntity().getY() > 320 - 16) {
                event.getEntity().awardStat(Stats.ITEM_USED.get(event.getEntity().getUseItem().getItem()));
                ItemUtils.createFilledResult(event.getEntity().getUseItem(), event.getEntity(), new ItemStack(MITMItems.CLOUD_BOTTLE.get()));
            }
        }
    }

    // endregion
}

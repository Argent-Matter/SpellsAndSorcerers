package dev.screret.sas;

import com.mojang.logging.LogUtils;
import net.minecraft.core.HolderLookup;
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
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;

import dev.screret.sas.common.data.provider.lang.ModLangProvider;
import dev.screret.sas.data.*;
import org.slf4j.Logger;
import dev.screret.sas.api.capability.ability.CapabilityWandAbility;
import dev.screret.sas.api.capability.mana.Mana;
import dev.screret.sas.api.registry.SASRegistries;
import dev.screret.sas.common.blockentity.PotionDistilleryBlockEntity;
import dev.screret.sas.config.SASConfig;
import dev.screret.sas.common.data.provider.conversion.EyeConversionProvider;
import dev.screret.sas.common.data.provider.recipe.ModRecipeProvider;
import dev.screret.sas.common.data.provider.tag.SASBiomeTagsProvider;
import dev.screret.sas.common.data.provider.tag.SASBlockTagsProvider;
import dev.screret.sas.common.data.provider.tag.SASItemTagsProvider;
import dev.screret.sas.common.entity.BossWizardEntity;
import dev.screret.sas.common.entity.WizardEntity;
import dev.screret.sas.common.recipe.ingredient.ModIngredients;
import dev.screret.sas.common.data.EyeConversionManager;

import java.util.concurrent.CompletableFuture;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SpellsAndSorcerers.MODID)
public class SpellsAndSorcerers {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "sas";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public SpellsAndSorcerers(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerRegistries);
        modEventBus.addListener(this::addItemsVanillaTabs);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerEntityAttributes);
        modEventBus.addListener(this::registerVanillaEntityAttributes);

        ModWandAbilities.WAND_ABILITIES.register(modEventBus);

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);

        ModEnchantments.ENCHANTS.register(modEventBus);
        ModEnchantments.ENCHANTS_MINECRAFT.register(modEventBus);

        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeTypes.RECIPE_SERIALIZERS.register(modEventBus);
        ModIngredients.INGREDIENT_TYPES.register(modEventBus);

        ModAttributes.ATTRIBUTES.register(modEventBus);
        ModAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        ModMobEffects.EFFECTS.register(modEventBus);
        ModPotions.POTIONS.register(modEventBus);

        ModContainers.MENU_TYPES.register(modEventBus);

        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        ModParticles.PARTICLES.register(modEventBus);

        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.CLIENT, SASConfig.Client.clientSpec);
        modContainer.registerConfig(ModConfig.Type.SERVER, SASConfig.Server.serverSpec);
    }

    @SuppressWarnings("Convert2MethodRef")
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        event.enqueueWork(() -> {
            ModPotions.registerPotionMixes();
        });
    }

    private void registerRegistries(final NewRegistryEvent event) {
        event.register(SASRegistries.WAND_ABILITIES);
    }

    public void addItemsVanillaTabs(final BuildCreativeModeTabContentsEvent event) {
        Util.generateWandItems();
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
            event.accept(ModItems.SOULSTEEL_AXE.get());
            event.accept(ModItems.SOULSTEEL_SWORD.get());
            event.accept(ModItems.SOULSTEEL_HELMET.get());
            event.accept(ModItems.SOULSTEEL_CHESTPLATE.get());
            event.accept(ModItems.SOULSTEEL_LEGGINGS.get());
            event.accept(ModItems.SOULSTEEL_BOOTS.get());
            event.acceptAll(Util.CUSTOM_WANDS.values());
            event.acceptAll(Util.CUSTOM_WAND_CORES.values());
        } else if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(ModItems.SOULSTEEL_AXE.get());
            event.accept(ModItems.SOULSTEEL_HOE.get());
            event.accept(ModItems.SOULSTEEL_PICKAXE.get());
            event.accept(ModItems.SOULSTEEL_SHOVEL.get());
            event.accept(ModItems.CTHULHU_EYE.get());
        } else if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ModItems.SOULSTEEL_BLOCK.get());
        } else if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModItems.PALANTIR.get());
            event.accept(ModItems.POTION_DISTILLERY.get());
            event.accept(ModItems.WAND_TABLE.get());
        } else if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(ModItems.POTION_DISTILLERY.get());
        } else if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.HANDLE.get());
            event.accept(ModItems.CLOUD_BOTTLE.get());
            event.accept(ModItems.SOUL_BOTTLE.get());
            event.accept(ModItems.SOULSTEEL_INGOT.get());
        } else if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.WIZARD_SPAWN_EGG.get());
            event.accept(ModItems.BOSS_WIZARD_SPAWN_EGG.get());
        }
    }

    public void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        SASBlockTagsProvider blockTags = new SASBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new SASItemTagsProvider(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper));

        gen.addProvider(event.includeServer(), new ModRecipeProvider(packOutput, lookupProvider));
        gen.addProvider(event.includeServer(), new EyeConversionProvider(packOutput));

        gen.addProvider(event.includeServer(), new SASBiomeTagsProvider(packOutput, lookupProvider, existingFileHelper));

        //gen.addProvider(event.includeServer(), new ModBlockstateProvider(gen, existingFileHelper));

        gen.addProvider(event.includeClient(), new ModLangProvider(packOutput, SpellsAndSorcerers.MODID, "en_us"));
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerItem(CapabilityWandAbility.WAND_ABILITY, (stack, ctx) -> CapabilityWandAbility.wandAbility(stack), ModItems.WAND);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.POTION_DISTILLERY.get(), PotionDistilleryBlockEntity::getItemHandler);
    }

    public void registerEntityAttributes(final EntityAttributeCreationEvent event) {
        event.put(ModEntities.WIZARD.get(), WizardEntity.createAttributes().build());
        event.put(ModEntities.BOSS_WIZARD.get(), BossWizardEntity.createAttributes().build());
    }

    public void registerVanillaEntityAttributes(final EntityAttributeModificationEvent event) {
        if (!event.has(EntityType.PLAYER, ModAttributes.MANA)) {
            event.add(EntityType.PLAYER, ModAttributes.MANA);
        }
    }

    @SuppressWarnings("unused")
    @EventBusSubscriber(modid = SpellsAndSorcerers.MODID)
    private static class ForgeBusEvents {
        @SubscribeEvent
        public static void onPlayerTick(final PlayerTickEvent.Post event) {
            if (event.getEntity().tickCount % 20 == 0) {
                AttributeInstance manaAttribute = event.getEntity().getAttribute(ModAttributes.MANA);
                Mana mana = event.getEntity().getData(ModAttachmentTypes.MANA);
                mana.setMaxManaStored(Mth.floor(manaAttribute.getValue()));
                mana.addMana(1, false);
                event.getEntity().setData(ModAttachmentTypes.MANA, mana);
            }
        }

        @SubscribeEvent
        public static void onRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
            if (event.getEntity().getItemInHand(event.getHand()).is(ModTags.Items.GLASS_BOTTLES)) {
                if (event.getLevel().getBlockState(event.getHitVec().getBlockPos()).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
                    event.getEntity().awardStat(Stats.ITEM_USED.get(event.getEntity().getUseItem().getItem()));
                    ItemUtils.createFilledResult(event.getEntity().getUseItem(), event.getEntity(), new ItemStack(ModItems.SOUL_BOTTLE.get()));
                } else if (event.getEntity().getY() > 320 - 16) {
                    event.getEntity().awardStat(Stats.ITEM_USED.get(event.getEntity().getUseItem().getItem()));
                    ItemUtils.createFilledResult(event.getEntity().getUseItem(), event.getEntity(), new ItemStack(ModItems.CLOUD_BOTTLE.get()));
                }
            }
        }

        @SubscribeEvent
        public static void registerReloadListeners(final AddReloadListenerEvent event) {
            EyeConversionManager.INSTANCE = new EyeConversionManager();
            event.addListener(EyeConversionManager.INSTANCE);
        }
    }
}

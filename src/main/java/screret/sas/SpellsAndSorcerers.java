package screret.sas;

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
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import org.slf4j.Logger;
import screret.sas.ability.ModWandAbilities;
import screret.sas.alchemy.effect.ModMobEffects;
import screret.sas.alchemy.potion.ModPotions;
import screret.sas.api.capability.ability.CapabilityWandAbility;
import screret.sas.api.capability.ability.ICapabilityWandAbility;
import screret.sas.api.capability.mana.Mana;
import screret.sas.api.wand.ability.WandAbilityRegistry;
import screret.sas.attachmenttypes.ModAttachmentTypes;
import screret.sas.attributes.ModAttributes;
import screret.sas.block.ModBlocks;
import screret.sas.blockentity.ModBlockEntities;
import screret.sas.blockentity.blockentity.PotionDistilleryBlockEntity;
import screret.sas.client.particle.ModParticles;
import screret.sas.config.SASConfig;
import screret.sas.container.ModContainers;
import screret.sas.creativetab.ModCreativeTabs;
import screret.sas.data.conversion.provider.EyeConversionProvider;
import screret.sas.data.recipe.provider.ModRecipeProvider;
import screret.sas.data.tag.SASBiomeTagsProvider;
import screret.sas.data.tag.SASBlockTagsProvider;
import screret.sas.data.tag.SASItemTagsProvider;
import screret.sas.enchantment.ModEnchantments;
import screret.sas.entity.ModEntities;
import screret.sas.entity.entity.BossWizardEntity;
import screret.sas.entity.entity.WizardEntity;
import screret.sas.item.ModItems;
import screret.sas.recipe.ModRecipeTypes;
import screret.sas.recipe.ingredient.ModIngredients;
import screret.sas.resource.EyeConversionManager;

import java.util.concurrent.CompletableFuture;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SpellsAndSorcerers.MODID)
public class SpellsAndSorcerers {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "sas";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public SpellsAndSorcerers(IEventBus modEventBus) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addItemsVanillaTabs);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::registerEntityAttributes);
        modEventBus.addListener(this::registerVanillaEntityAttributes);


        WandAbilityRegistry.init();
        ModWandAbilities.init();
        WandAbilityRegistry.WAND_ABILITIES.register(modEventBus);

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

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SASConfig.Client.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SASConfig.Server.serverSpec);
    }

    @SuppressWarnings("Convert2MethodRef")
    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        event.enqueueWork(() -> {
            ModPotions.registerPotionMixes();
        });
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

        gen.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));
        gen.addProvider(event.includeServer(), new EyeConversionProvider(packOutput));

        gen.addProvider(event.includeServer(), new SASBiomeTagsProvider(packOutput, lookupProvider, existingFileHelper));

        //gen.addProvider(event.includeServer(), new ModBlockstateProvider(gen, existingFileHelper));
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.registerItem(ICapabilityWandAbility.WAND_ABILITY, (stack, ctx) -> CapabilityWandAbility.wandAbility(stack), ModItems.WAND);
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ModBlockEntities.POTION_DISTILLERY.get(), PotionDistilleryBlockEntity::getItemHandler);
    }

    public void registerEntityAttributes(final EntityAttributeCreationEvent event) {
        event.put(ModEntities.WIZARD.get(), WizardEntity.createAttributes().build());
        event.put(ModEntities.BOSS_WIZARD.get(), BossWizardEntity.createAttributes().build());
    }

    public void registerVanillaEntityAttributes(final EntityAttributeModificationEvent event) {
        if (!event.has(EntityType.PLAYER, ModAttributes.MANA.get())) {
            event.add(EntityType.PLAYER, ModAttributes.MANA.get());
        }
    }

    @SuppressWarnings("unused")
    @Mod.EventBusSubscriber(modid = SpellsAndSorcerers.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    private static class ForgeBusEvents {
        @SubscribeEvent
        public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
            if (event.phase == TickEvent.Phase.END && event.player.tickCount % 20 == 0) {
                AttributeInstance manaAttribute = event.player.getAttribute(ModAttributes.MANA.get());
                Mana mana = event.player.getData(ModAttachmentTypes.MANA);
                mana.setMaxManaStored(Mth.floor(manaAttribute.getValue()));
                mana.addMana(1, false);
                event.player.setData(ModAttachmentTypes.MANA, mana);
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

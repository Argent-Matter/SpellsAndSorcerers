package screret.sas;

import com.mojang.logging.LogUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import screret.sas.ability.ModWandAbilities;
import screret.sas.alchemy.effect.ModMobEffects;
import screret.sas.alchemy.potion.ModPotions;
import screret.sas.api.capability.ability.ICapabilityWandAbility;
import screret.sas.api.capability.mana.ICapabilityMana;
import screret.sas.api.capability.mana.ManaProvider;
import screret.sas.api.wand.ability.WandAbilityRegistry;
import screret.sas.block.ModBlocks;
import screret.sas.blockentity.ModBlockEntities;
import screret.sas.client.particle.ModParticles;
import screret.sas.config.SASConfig;
import screret.sas.container.ModContainers;
import screret.sas.creativetab.ModCreativeTabs;
import screret.sas.data.conversion.provider.EyeConversionProvider;
import screret.sas.data.recipe.provider.ModRecipeProvider;
import screret.sas.data.recipe.provider.WandRecipeProvider;
import screret.sas.data.tag.SASBiomeTagsProvider;
import screret.sas.data.tag.SASBlockTagsProvider;
import screret.sas.data.tag.SASItemTagsProvider;
import screret.sas.enchantment.ModEnchantments;
import screret.sas.entity.ModEntities;
import screret.sas.entity.entity.BossWizardEntity;
import screret.sas.entity.entity.WizardEntity;
import screret.sas.item.ModItems;
import screret.sas.recipe.ModRecipeTypes;
import screret.sas.recipe.ingredient.WandAbilityIngredient;
import screret.sas.resource.EyeConversionManager;

import java.util.concurrent.CompletableFuture;

import static screret.sas.Util.addWand;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SpellsAndSorcerers.MODID)
public class SpellsAndSorcerers {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "sas";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    public SpellsAndSorcerers() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::gatherData);
        modEventBus.addListener(this::registerEntityAttributes);
        modEventBus.addListener(this::registerSerializers);

        WandAbilityRegistry.init();
        WandAbilityRegistry.WAND_ABILITIES.register(modEventBus);
        ModWandAbilities.init();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);

        ModEnchantments.ENCHANTS.register(modEventBus);
        ModEnchantments.ENCHANTS_MINECRAFT.register(modEventBus);

        ModRecipeTypes.RECIPE_TYPES.register(modEventBus);
        ModRecipeTypes.RECIPE_SERIALIZERS.register(modEventBus);

        ModMobEffects.EFFECTS.register(modEventBus);
        ModPotions.POTIONS.register(modEventBus);

        ModContainers.MENU_TYPES.register(modEventBus);

        ModEntities.ENTITY_TYPES.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);

        ModParticles.PARTICLES.register(modEventBus);

        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SASConfig.Client.clientSpec);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SASConfig.Server.serverSpec);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        event.enqueueWork(() ->  {
            ModPotions.registerPotionMixes();
        });
    }

    public void gatherData(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        SASBlockTagsProvider blockTags = new SASBlockTagsProvider(packOutput, lookupProvider, existingFileHelper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), new SASItemTagsProvider(packOutput, lookupProvider, blockTags.contentsGetter(), existingFileHelper));

        gen.addProvider(event.includeServer(), new WandRecipeProvider(packOutput));
        gen.addProvider(event.includeServer(), new ModRecipeProvider(packOutput));
        gen.addProvider(event.includeServer(), new EyeConversionProvider(packOutput));

        gen.addProvider(event.includeServer(), new SASBiomeTagsProvider(packOutput, lookupProvider, existingFileHelper));

        //gen.addProvider(event.includeServer(), new ModBlockstateProvider(gen, existingFileHelper));
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        event.register(ICapabilityWandAbility.class);
        event.register(ICapabilityMana.class);
    }

    @SubscribeEvent
    public void attachCapabilitiesPlayer(final AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player)) return;
        if(!SASConfig.Server.useMana.get()) return;
        event.addCapability(Util.id("mana"), new ManaProvider());
    }

    public void registerEntityAttributes(final EntityAttributeCreationEvent event) {
        event.put(ModEntities.WIZARD.get(), WizardEntity.createAttributes().build());
        event.put(ModEntities.BOSS_WIZARD.get(), BossWizardEntity.createAttributes().build());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onRegister(final RegisterEvent event) {
        Util.addItems();
    }


    public void registerSerializers(RegisterEvent event) {
        event.register(ForgeRegistries.Keys.RECIPE_SERIALIZERS, helper -> CraftingHelper.register(Util.id("wand_ability"), WandAbilityIngredient.Serializer.INSTANCE));
    }

    @Mod.EventBusSubscriber(modid = SpellsAndSorcerers.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    private static class ForgeBusEvents {
        @SubscribeEvent
        public static void onPlayerTick(final TickEvent.PlayerTickEvent event) {
            if(event.phase == TickEvent.Phase.END && event.player.tickCount % 20 == 0) {
                event.player.getCapability(ManaProvider.MANA).ifPresent(cap -> {
                    cap.addMana(1, false);
                });
            }
        }

        @SubscribeEvent
        public static void onRightclickBlock(final PlayerInteractEvent.RightClickBlock event) {
            if(event.getEntity().getItemInHand(event.getHand()).is(ModTags.Items.GLASS_BOTTLES)) {
                if(event.getLevel().getBlockState(event.getHitVec().getBlockPos()).is(BlockTags.SOUL_FIRE_BASE_BLOCKS)) {
                    event.getEntity().awardStat(Stats.ITEM_USED.get(event.getEntity().getUseItem().getItem()));
                    ItemUtils.createFilledResult(event.getEntity().getUseItem(), event.getEntity(), new ItemStack(ModItems.SOUL_BOTTLE.get()));
                } else if(event.getEntity().getY() > 320 - 16) {
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

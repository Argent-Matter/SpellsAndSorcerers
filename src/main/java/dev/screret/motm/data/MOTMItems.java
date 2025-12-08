package dev.screret.motm.data;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.item.*;

import net.minecraft.world.item.*;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public class MOTMItems {

    // spotless:off
    public static final DeferredRegister.Items ITEMS = DeferredRegister.Items.createItems(MagicOfTheMind.MOD_ID);

    // BLOCK ITEMS
    public static final DeferredItem<BlockItem> PALANTIR = ITEMS.registerSimpleBlockItem(MOTMBlocks.PALANTIR, fireResistantProps().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<BlockItem> SOULSTEEL_BLOCK = ITEMS.registerSimpleBlockItem("soulsteel_block", MOTMBlocks.SOULSTEEL_BLOCK);
    public static final DeferredItem<BlockItem> GLINT_ORE = ITEMS.registerSimpleBlockItem(MOTMBlocks.GLINT_ORE);

    // WANDS
    public static final DeferredItem<WandItem> WAND = ITEMS.register("wand", WandItem::new);
    public static final DeferredItem<WandCoreItem> WAND_CORE = ITEMS.register("wand_core", WandCoreItem::new);

    // OTHER ITEMS
    public static final DeferredItem<Item> HANDLE = ITEMS.registerSimpleItem("handle");
    public static final DeferredItem<Item> SOUL_BOTTLE = ITEMS.registerSimpleItem("soul_bottle", new Item.Properties().craftRemainder(Items.GLASS_BOTTLE));
    public static final DeferredItem<Item> CLOUD_BOTTLE = ITEMS.registerSimpleItem("cloud_bottle", new Item.Properties().craftRemainder(Items.GLASS_BOTTLE));
    public static final DeferredItem<CthulhuEyeItem> CTHULHU_EYE = ITEMS.register("cthulhu_eye", CthulhuEyeItem::new);
    public static final DeferredItem<Item> SOULSTEEL_INGOT = ITEMS.registerSimpleItem("soulsteel_ingot");
    public static final DeferredItem<Item> SOULSTEEL_NUGGET = ITEMS.registerSimpleItem("soulsteel_nugget");
    public static final DeferredItem<Item> GLINT = ITEMS.registerSimpleItem("glint");

    public static final DeferredItem<OneRingItem> THE_ONE_RING = ITEMS.register("the_one_ring", OneRingItem::new);

    public static final DeferredItem<DeferredSpawnEggItem> WIZARD_SPAWN_EGG = ITEMS.registerItem("wizard_spawn_egg",
            p -> new DeferredSpawnEggItem(MOTMEntityTypes.WIZARD, 0x002017, 0x959b9b, p));
    public static final DeferredItem<DeferredSpawnEggItem> BOSS_WIZARD_SPAWN_EGG = ITEMS.registerItem("boss_wizard_spawn_egg",
            p -> new DeferredSpawnEggItem(MOTMEntityTypes.BOSS_WIZARD, 0x9a080f, 0x959b9b, p));

    public static final DeferredItem<MOTMArmorItem> SOULSTEEL_HELMET = ITEMS.register("soulsteel_helmet", makeSoulsteelArmorItem(ArmorItem.Type.HELMET));
    public static final DeferredItem<MOTMArmorItem> SOULSTEEL_CHESTPLATE = ITEMS.register("soulsteel_chestplate", makeSoulsteelArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<MOTMArmorItem> SOULSTEEL_LEGGINGS = ITEMS.register("soulsteel_leggings", makeSoulsteelArmorItem(ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<MOTMArmorItem> SOULSTEEL_BOOTS = ITEMS.register("soulsteel_boots", makeSoulsteelArmorItem(ArmorItem.Type.BOOTS));

    public static final DeferredItem<SwordItem> SOULSTEEL_SWORD = ITEMS.register("soulsteel_sword", makeSoulsteelToolItem(SwordItem::new));
    public static final DeferredItem<ShovelItem> SOULSTEEL_SHOVEL = ITEMS.register("soulsteel_shovel", makeSoulsteelToolItem(ShovelItem::new));
    public static final DeferredItem<PickaxeItem> SOULSTEEL_PICKAXE = ITEMS.register("soulsteel_pickaxe", makeSoulsteelToolItem(PickaxeItem::new));
    public static final DeferredItem<AxeItem> SOULSTEEL_AXE = ITEMS.register("soulsteel_axe", makeSoulsteelToolItem(AxeItem::new));
    public static final DeferredItem<HoeItem> SOULSTEEL_HOE = ITEMS.register("soulsteel_hoe", makeSoulsteelToolItem(HoeItem::new));

    // spotless:on

    private static Item.Properties fireResistantProps() {
        return new Item.Properties().fireResistant();
    }

    private static Supplier<MOTMArmorItem> makeSoulsteelArmorItem(ArmorItem.Type type) {
        return () -> new MOTMArmorItem(MOTMArmorMaterials.SOULSTEEL, MOTMArmorItem.SOUL_STEEL_EFFECT,
                type, fireResistantProps().durability(type.getDurability(40)));
    }

    private static <I extends TieredItem> Supplier<I> makeSoulsteelToolItem(BiFunction<Tier, Item.Properties, I> func) {
        return () -> func.apply(MOTMTiers.SOULSTEEL, fireResistantProps());
    }
}

package dev.screret.motm.data.item;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.item.SimpleGeoBlockItem;
import dev.screret.motm.common.item.*;
import dev.screret.motm.data.block.MOTMBlocks;
import dev.screret.motm.data.entity.MOTMEntityTypes;

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
    public static final DeferredItem<BlockItem> PALANTIR = ITEMS.registerItem("palantir", p -> new SimpleGeoBlockItem(MOTMBlocks.PALANTIR.get(), p), fireResistantProps().rarity(Rarity.UNCOMMON));
    public static final DeferredItem<BlockItem> SOULSTEEL_BLOCK = ITEMS.registerSimpleBlockItem(MOTMBlocks.SOULSTEEL_BLOCK);
    public static final DeferredItem<BlockItem> GLINT_ORE = ITEMS.registerSimpleBlockItem(MOTMBlocks.GLINT_ORE);

    public static final DeferredItem<PortStoneBlockItem> PORT_STONE = ITEMS.registerItem("port_stone", PortStoneBlockItem::new);

    public static final DeferredItem<BlockItem> UNAWAKENED_MEMORYSTONE = ITEMS.registerSimpleBlockItem(MOTMBlocks.UNAWAKENED_MEMORYSTONE);
    public static final DeferredItem<BlockItem> UNAWAKENED_MEMORYSTONE_STAIRS = ITEMS.registerSimpleBlockItem(MOTMBlocks.UNAWAKENED_MEMORYSTONE_STAIRS);
    public static final DeferredItem<BlockItem> UNAWAKENED_MEMORYSTONE_SLAB = ITEMS.registerSimpleBlockItem(MOTMBlocks.UNAWAKENED_MEMORYSTONE_SLAB);
    public static final DeferredItem<BlockItem> UNAWAKENED_MEMORYSTONE_WALL = ITEMS.registerSimpleBlockItem(MOTMBlocks.UNAWAKENED_MEMORYSTONE_WALL);

    public static final DeferredItem<BlockItem> MEMORYSTONE = ITEMS.registerSimpleBlockItem(MOTMBlocks.MEMORYSTONE);
    public static final DeferredItem<BlockItem> MEMORYSTONE_STAIRS = ITEMS.registerSimpleBlockItem(MOTMBlocks.MEMORYSTONE_STAIRS);
    public static final DeferredItem<BlockItem> MEMORYSTONE_SLAB = ITEMS.registerSimpleBlockItem(MOTMBlocks.MEMORYSTONE_SLAB);
    public static final DeferredItem<BlockItem> MEMORYSTONE_WALL = ITEMS.registerSimpleBlockItem(MOTMBlocks.MEMORYSTONE_WALL);

    public static final DeferredItem<BlockItem> POLISHED_MEMORYSTONE = ITEMS.registerSimpleBlockItem(MOTMBlocks.POLISHED_MEMORYSTONE);
    public static final DeferredItem<BlockItem> POLISHED_MEMORYSTONE_STAIRS = ITEMS.registerSimpleBlockItem(MOTMBlocks.POLISHED_MEMORYSTONE_STAIRS);
    public static final DeferredItem<BlockItem> POLISHED_MEMORYSTONE_SLAB = ITEMS.registerSimpleBlockItem(MOTMBlocks.POLISHED_MEMORYSTONE_SLAB);
    public static final DeferredItem<BlockItem> POLISHED_MEMORYSTONE_WALL = ITEMS.registerSimpleBlockItem(MOTMBlocks.POLISHED_MEMORYSTONE_WALL);
    public static final DeferredItem<BlockItem> POLISHED_MEMORYSTONE_PRESSURE_PLATE = ITEMS.registerSimpleBlockItem(MOTMBlocks.POLISHED_MEMORYSTONE_PRESSURE_PLATE);
    public static final DeferredItem<BlockItem> POLISHED_MEMORYSTONE_BUTTON = ITEMS.registerSimpleBlockItem(MOTMBlocks.POLISHED_MEMORYSTONE_BUTTON);
    public static final DeferredItem<BlockItem> CHISELED_POLISHED_MEMORYSTONE = ITEMS.registerSimpleBlockItem(MOTMBlocks.CHISELED_POLISHED_MEMORYSTONE);

    public static final DeferredItem<BlockItem> POLISHED_MEMORYSTONE_BRICKS = ITEMS.registerSimpleBlockItem(MOTMBlocks.POLISHED_MEMORYSTONE_BRICKS);
    public static final DeferredItem<BlockItem> CRACKED_POLISHED_MEMORYSTONE_BRICKS = ITEMS.registerSimpleBlockItem(MOTMBlocks.CRACKED_POLISHED_MEMORYSTONE_BRICKS);
    public static final DeferredItem<BlockItem> POLISHED_MEMORYSTONE_BRICK_STAIRS = ITEMS.registerSimpleBlockItem(MOTMBlocks.POLISHED_MEMORYSTONE_BRICK_STAIRS);
    public static final DeferredItem<BlockItem> POLISHED_MEMORYSTONE_BRICK_SLAB = ITEMS.registerSimpleBlockItem(MOTMBlocks.POLISHED_MEMORYSTONE_BRICK_SLAB);
    public static final DeferredItem<BlockItem> POLISHED_MEMORYSTONE_BRICK_WALL = ITEMS.registerSimpleBlockItem(MOTMBlocks.POLISHED_MEMORYSTONE_BRICK_WALL);

    // OTHER ITEMS
    public static final DeferredItem<Item> HANDLE = ITEMS.registerSimpleItem("handle");
    public static final DeferredItem<Item> SOUL_BOTTLE = ITEMS.registerSimpleItem("soul_bottle", new Item.Properties().craftRemainder(Items.GLASS_BOTTLE));
    public static final DeferredItem<Item> CLOUD_BOTTLE = ITEMS.registerSimpleItem("cloud_bottle", new Item.Properties().craftRemainder(Items.GLASS_BOTTLE));
    public static final DeferredItem<Item> SOULSTEEL_INGOT = ITEMS.registerSimpleItem("soulsteel_ingot");
    public static final DeferredItem<Item> SOULSTEEL_NUGGET = ITEMS.registerSimpleItem("soulsteel_nugget");
    public static final DeferredItem<Item> GLINT = ITEMS.registerSimpleItem("glint");

    public static final DeferredItem<OneRingItem> THE_ONE_RING = ITEMS.register("the_one_ring", OneRingItem::new);

    public static final DeferredItem<MOTMArmorItem> SOULSTEEL_HELMET = ITEMS.register("soulsteel_helmet", makeSoulsteelArmorItem(ArmorItem.Type.HELMET));
    public static final DeferredItem<MOTMArmorItem> SOULSTEEL_CHESTPLATE = ITEMS.register("soulsteel_chestplate", makeSoulsteelArmorItem(ArmorItem.Type.CHESTPLATE));
    public static final DeferredItem<MOTMArmorItem> SOULSTEEL_LEGGINGS = ITEMS.register("soulsteel_leggings", makeSoulsteelArmorItem(ArmorItem.Type.LEGGINGS));
    public static final DeferredItem<MOTMArmorItem> SOULSTEEL_BOOTS = ITEMS.register("soulsteel_boots", makeSoulsteelArmorItem(ArmorItem.Type.BOOTS));

    public static final DeferredItem<SwordItem> SOULSTEEL_SWORD = ITEMS.register("soulsteel_sword", makeSoulsteelToolItem(SwordItem::new));
    public static final DeferredItem<ShovelItem> SOULSTEEL_SHOVEL = ITEMS.register("soulsteel_shovel", makeSoulsteelToolItem(ShovelItem::new));
    public static final DeferredItem<PickaxeItem> SOULSTEEL_PICKAXE = ITEMS.register("soulsteel_pickaxe", makeSoulsteelToolItem(PickaxeItem::new));
    public static final DeferredItem<AxeItem> SOULSTEEL_AXE = ITEMS.register("soulsteel_axe", makeSoulsteelToolItem(AxeItem::new));
    public static final DeferredItem<HoeItem> SOULSTEEL_HOE = ITEMS.register("soulsteel_hoe", makeSoulsteelToolItem(HoeItem::new));

    public static final DeferredItem<DeferredSpawnEggItem> ELDERLING_SPAWN_EGG = ITEMS.registerItem("elderling_spawn_egg", p -> new DeferredSpawnEggItem(MOTMEntityTypes.ELDERLING, 0x5b841b, 0xdfc453, p));

    // spotless:on

    private static Item.Properties fireResistantProps() {
        return new Item.Properties().fireResistant();
    }

    private static Supplier<MOTMArmorItem> makeSoulsteelArmorItem(ArmorItem.Type type) {
        return () -> new MOTMArmorItem(MOTMArmorMaterials.SOULSTEEL, type,
                fireResistantProps().durability(type.getDurability(40)));
    }

    private static <I extends TieredItem> Supplier<I> makeSoulsteelToolItem(BiFunction<Tier, Item.Properties, I> func) {
        return () -> func.apply(MOTMTiers.SOULSTEEL, fireResistantProps());
    }
}

package screret.sas.item;

import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import screret.sas.SpellsAndSorcerers;
import screret.sas.block.ModBlocks;
import screret.sas.entity.ModEntities;
import screret.sas.item.item.*;
import screret.sas.item.material.ModArmorMaterials;
import screret.sas.item.material.ModTiers;

import java.util.function.Supplier;

public class ModItems {

    // Create a Deferred Register to hold Items which will all be registered under the "sas" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.Items.createItems(SpellsAndSorcerers.MODID);


    //BLOCK ITEMS
    public static final DeferredItem<BlockItem> WAND_TABLE = ITEMS.register("wand_table", blockItem(ModBlocks.WAND_TABLE));
    public static final DeferredItem<BlockItem> SUMMON_SIGN = ITEMS.register("summon_sign", blockItem(ModBlocks.SUMMON_SIGN));
    public static final DeferredItem<PalantirItem> PALANTIR = ITEMS.register("palantir", PalantirItem::new);
    public static final DeferredItem<BlockItem> SOULSTEEL_BLOCK = ITEMS.register("soulsteel_block", blockItem(ModBlocks.SOULSTEEL_BLOCK));
    public static final DeferredItem<BlockItem> GLINT_ORE = ITEMS.register("glint_ore", blockItem(ModBlocks.GLINT_ORE));
    public static final DeferredItem<BlockItem> POTION_DISTILLERY = ITEMS.register("potion_distillery", blockItem(ModBlocks.POTION_DISTILLERY));


    // WANDS
    public static final DeferredItem<WandItem> WAND = ITEMS.register("wand", WandItem::new);
    public static final DeferredItem<WandCoreItem> WAND_CORE = ITEMS.register("wand_core", WandCoreItem::new);


    //OTHER ITEMS
    public static final DeferredItem<Item> HANDLE = ITEMS.register("handle", basicItem());
    public static final DeferredItem<Item> SOUL_BOTTLE = ITEMS.register("soul_bottle", () -> new Item(basicItemProperties().craftRemainder(Items.GLASS_BOTTLE)));
    public static final DeferredItem<Item> CLOUD_BOTTLE = ITEMS.register("cloud_bottle", () -> new Item(basicItemProperties().craftRemainder(Items.GLASS_BOTTLE)));
    public static final DeferredItem<CthulhuEyeItem> CTHULHU_EYE = ITEMS.register("cthulhu_eye", CthulhuEyeItem::new);
    public static final DeferredItem<Item> SOULSTEEL_INGOT = ITEMS.register("soulsteel_ingot", basicItem());
    public static final DeferredItem<Item> SOULSTEEL_NUGGET = ITEMS.register("soulsteel_nugget", basicItem());
    public static final DeferredItem<Item> GLINT = ITEMS.register("glint", basicItem());


    public static final DeferredItem<DeferredSpawnEggItem> WIZARD_SPAWN_EGG = ITEMS.register("wizard_spawn_egg", () -> new DeferredSpawnEggItem(ModEntities.WIZARD, 0x002017, 0x959b9b, basicItemProperties()));
    public static final DeferredItem<DeferredSpawnEggItem> BOSS_WIZARD_SPAWN_EGG = ITEMS.register("boss_wizard_spawn_egg", () -> new DeferredSpawnEggItem(ModEntities.BOSS_WIZARD, 0x9a080f, 0x959b9b, basicItemProperties()));

    public static final DeferredItem<ModArmorItem> SOULSTEEL_HELMET = ITEMS.register("soulsteel_helmet", () -> new ModArmorItem(ModArmorMaterials.SOULSTEEL, ModArmorItem.SOUL_STEEL_EFFECT, ArmorItem.Type.HELMET, basicItemProperties().fireResistant()));
    public static final DeferredItem<ModArmorItem> SOULSTEEL_CHESTPLATE = ITEMS.register("soulsteel_chestplate", () -> new ModArmorItem(ModArmorMaterials.SOULSTEEL, ModArmorItem.SOUL_STEEL_EFFECT, ArmorItem.Type.CHESTPLATE, basicItemProperties().fireResistant()));
    public static final DeferredItem<ModArmorItem> SOULSTEEL_LEGGINGS = ITEMS.register("soulsteel_leggings", () -> new ModArmorItem(ModArmorMaterials.SOULSTEEL, ModArmorItem.SOUL_STEEL_EFFECT, ArmorItem.Type.LEGGINGS, basicItemProperties().fireResistant()));
    public static final DeferredItem<ModArmorItem> SOULSTEEL_BOOTS = ITEMS.register("soulsteel_boots", () -> new ModArmorItem(ModArmorMaterials.SOULSTEEL, ModArmorItem.SOUL_STEEL_EFFECT, ArmorItem.Type.BOOTS, basicItemProperties().fireResistant()));
    public static final DeferredItem<SwordItem> SOULSTEEL_SWORD = ITEMS.register("soulsteel_sword", () -> new SwordItem(ModTiers.SOULSTEEL, 3, -2.4F, basicItemProperties().fireResistant()));
    public static final DeferredItem<ShovelItem> SOULSTEEL_SHOVEL = ITEMS.register("soulsteel_shovel", () -> new ShovelItem(ModTiers.SOULSTEEL, 1.5F, -3.0F, basicItemProperties().fireResistant()));
    public static final DeferredItem<PickaxeItem> SOULSTEEL_PICKAXE = ITEMS.register("soulsteel_pickaxe", () -> new PickaxeItem(ModTiers.SOULSTEEL, 1, -2.8F, basicItemProperties().fireResistant()));
    public static final DeferredItem<AxeItem> SOULSTEEL_AXE = ITEMS.register("soulsteel_axe", () -> new AxeItem(ModTiers.SOULSTEEL, 5.0F, -3.0F, basicItemProperties().fireResistant()));
    public static final DeferredItem<HoeItem> SOULSTEEL_HOE = ITEMS.register("soulsteel_hoe", () -> new HoeItem(ModTiers.SOULSTEEL, -5, 0.0F, basicItemProperties().fireResistant()));


    private static Item.Properties basicItemProperties() {
        return new Item.Properties();
    }

    private static Supplier<Item> basicItem() {
        return () -> new Item(basicItemProperties());
    }

    private static Supplier<BlockItem> blockItem(DeferredBlock<? extends Block> block) {
        return () -> new BlockItem(block.get(), basicItemProperties());
    }
}

package screret.sas.enchantment;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import screret.sas.SpellsAndSorcerers;
import screret.sas.enchantment.enchantment.ManaEfficiencyEnchantment;
import screret.sas.enchantment.enchantment.PowerEnchantment;
import screret.sas.enchantment.enchantment.ProlongedUseEnchantment;
import screret.sas.enchantment.enchantment.QuickChargeEnchantment;
import screret.sas.item.item.WandItem;

import java.util.function.Supplier;

public class ModEnchantments {

    public static class Categories {
        public static final EnchantmentCategory WAND = EnchantmentCategory.create("sas:wand", (item)->(item instanceof WandItem));
        public static final EnchantmentCategory CROSSBOW_OR_WAND = EnchantmentCategory.create("sas:crossbow_wand", (item)->(item instanceof WandItem || item instanceof CrossbowItem));
        public static final EnchantmentCategory BOW_OR_WAND = EnchantmentCategory.create("sas:bow_wand", (item)->(item instanceof WandItem || item instanceof BowItem));
    }



    public static final DeferredRegister<Enchantment> ENCHANTS = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT, SpellsAndSorcerers.MODID);
    public static final DeferredRegister<Enchantment> ENCHANTS_MINECRAFT = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT, "minecraft");


    public static final Supplier<Enchantment> PROLONGED_USE = ENCHANTS.register("prolonged_use", () -> new ProlongedUseEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
    public static final Supplier<Enchantment> MANA_EFFICIENCY = ENCHANTS.register("mana_efficiency", () -> new ManaEfficiencyEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));

    // overwrite some vanilla enchants. not the best way to do this, fix.
    public static final Supplier<Enchantment> QUICK_CHARGE = ENCHANTS_MINECRAFT.register("quick_charge", () -> new QuickChargeEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));
    public static final Supplier<Enchantment> POWER = ENCHANTS_MINECRAFT.register("power", () -> new PowerEnchantment(Enchantment.Rarity.COMMON, EquipmentSlot.MAINHAND));

}

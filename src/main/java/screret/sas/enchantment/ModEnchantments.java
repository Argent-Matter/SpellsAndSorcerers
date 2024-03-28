package screret.sas.enchantment;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import screret.sas.SpellsAndSorcerers;
import screret.sas.enchantment.enchantment.ManaEfficiencyEnchantment;
import screret.sas.enchantment.enchantment.ProlongedUseEnchantment;
import screret.sas.item.item.WandItem;

import java.util.function.Supplier;

public class ModEnchantments {

    public static class Categories {
        public static final EnchantmentCategory WAND = EnchantmentCategory.create("sas:wand", (item) -> (item instanceof WandItem));
    }


    public static final DeferredRegister<Enchantment> ENCHANTS = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT, SpellsAndSorcerers.MODID);
    public static final DeferredRegister<Enchantment> ENCHANTS_MINECRAFT = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT, "minecraft");


    public static final Supplier<Enchantment> PROLONGED_USE = ENCHANTS.register("prolonged_use", () -> new ProlongedUseEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
    public static final Supplier<Enchantment> MANA_EFFICIENCY = ENCHANTS.register("mana_efficiency", () -> new ManaEfficiencyEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));

}

package dev.screret.motm.data;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.enchantment.Enchantment;

public class MOTMEnchantments {

    // public static final DeferredRegister<Enchantment> ENCHANTS = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT,
    // MagicOfTheMind.MODID);

    // public static final DeferredHolder<Enchantment, Enchantment> PROLONGED_USE = ENCHANTS.register("prolonged_use", () -> new
    // ProlongedUseEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
    // public static final DeferredHolder<Enchantment, Enchantment> MANA_EFFICIENCY = ENCHANTS.register("mana_efficiency", () ->
    // new ManaEfficiencyEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));

    public static void bootstrap(BootstrapContext<Enchantment> context) {}
}

package dev.screret.mitm.data;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.common.enchantment.ManaEfficiencyEnchantment;
import dev.screret.mitm.common.enchantment.ProlongedUseEnchantment;

public class MITMEnchantments {

    public static final DeferredRegister<Enchantment> ENCHANTS = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT, MagicOfTheMind.MODID);
    public static final DeferredRegister<Enchantment> ENCHANTS_MINECRAFT = DeferredRegister.create(BuiltInRegistries.ENCHANTMENT, "minecraft");


    public static final DeferredHolder<Enchantment, Enchantment> PROLONGED_USE = ENCHANTS.register("prolonged_use", () -> new ProlongedUseEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND));
    public static final DeferredHolder<Enchantment, Enchantment> MANA_EFFICIENCY = ENCHANTS.register("mana_efficiency", () -> new ManaEfficiencyEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND));


    public static void bootstrap(BootstrapContext<Enchantment> context) {

    }
}

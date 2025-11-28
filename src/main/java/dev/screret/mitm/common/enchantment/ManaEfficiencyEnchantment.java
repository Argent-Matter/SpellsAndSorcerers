// package dev.screret.mitm.common.enchantment;
//
// import net.minecraft.world.entity.EquipmentSlot;
// import net.minecraft.world.item.Rarity;
// import net.minecraft.world.item.enchantment.Enchantment;
// import net.minecraft.world.item.enchantment.Enchantments;
//
// import dev.screret.mitm.data.MITMEnchantments;
//
// public class ManaEfficiencyEnchantment extends Enchantment {
//
//     public ManaEfficiencyEnchantment(Rarity rarity, EquipmentSlot... applicableSlots) {
//         super(rarity, MITMEnchantments.Categories.WAND, applicableSlots);
//     }
//
//     /**
//      * Returns the minimal value of enchantability needed on the enchantment level passed.
//      */
//     @Override
//     public int getMinCost(int enchantmentLevel) {
//         return 1 + (enchantmentLevel - 1) * 10;
//     }
//
//     @Override
//     public int getMaxCost(int enchantmentLevel) {
//         return this.getMinCost(enchantmentLevel) + 15;
//     }
//
//     /**
//      * Returns the maximum level that the enchantment can have.
//      */
//     @Override
//     public int getMaxLevel() {
//         return 5;
//     }
//
//     @Override
//     public boolean checkCompatibility(Enchantment enchantment) {
//         return enchantment != Enchantments.QUICK_CHARGE && super.checkCompatibility(enchantment);
//     }
// }

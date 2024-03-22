package screret.sas.enchantment.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import screret.sas.enchantment.ModEnchantments;

public class QuickChargeEnchantment extends Enchantment {
    public QuickChargeEnchantment(Rarity pRarity, EquipmentSlot... pApplicableSlots) {
        super(pRarity, ModEnchantments.Categories.CROSSBOW_OR_WAND, pApplicableSlots);
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinCost(int pEnchantmentLevel) {
        return 12 + (pEnchantmentLevel - 1) * 20;
    }

    public int getMaxCost(int pEnchantmentLevel) {
        return 50;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel() {
        return 3;
    }
}

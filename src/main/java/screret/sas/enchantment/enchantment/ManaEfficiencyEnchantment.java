package screret.sas.enchantment.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import screret.sas.enchantment.ModEnchantments;

public class ManaEfficiencyEnchantment extends Enchantment {

    public ManaEfficiencyEnchantment(Rarity pRarity, EquipmentSlot... pApplicableSlots) {
        super(pRarity, ModEnchantments.Categories.WAND, pApplicableSlots);
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    @Override
    public int getMinCost(int pEnchantmentLevel) {
        return 1 + (pEnchantmentLevel - 1) * 10;
    }

    @Override
    public int getMaxCost(int pEnchantmentLevel) {
        return this.getMinCost(pEnchantmentLevel) + 15;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public boolean checkCompatibility(Enchantment enchantment) {
        return !(enchantment instanceof QuickChargeEnchantment) && super.checkCompatibility(enchantment);
    }
}


package screret.sas.enchantment.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import screret.sas.enchantment.ModEnchantments;

public class WandPowerEnchantment extends Enchantment {

    public WandPowerEnchantment(Rarity enchantRarity, EquipmentSlot... pApplicableSlots) {
        super(enchantRarity, ModEnchantments.Categories.WAND, pApplicableSlots);
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinCost(int enchantLevel) {
        return 20;
    }

    public int getMaxCost(int enchantLevel) {
        return 50;
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel() {
        return 5;
    }
}

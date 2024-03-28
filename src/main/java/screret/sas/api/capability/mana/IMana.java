package screret.sas.api.capability.mana;

import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.UUID;

public interface IMana extends INBTSerializable<Tag> {
    UUID MANA_ATTRIBUTE_UUID = UUID.fromString("4f0c2919-7ac9-45f8-b6fe-a7e89b453dba");


    int addMana(int maxReceive, boolean simulate);

    /**
     * Removes mana from the storage. Returns quantity of energy that was removed.
     *
     * @param maxExtract Maximum amount of energy to be extracted.
     * @param simulate   If TRUE, the extraction will only be simulated.
     * @return Amount of mana that was (or would have been, if simulated) extracted from the storage.
     */
    int deductMana(int maxExtract, boolean simulate);


    /**
     * Sets the maximum mana amount.
     */
    void setMaxManaStored(int max);

    /**
     * Returns the amount of mana currently stored.
     */
    int getManaStored();

    /**
     * Returns the maximum amount of mana that can be stored.
     */
    int getMaxManaStored();

    /**
     * Returns if this storage can have mana extracted.
     * If this is false, then any calls to extractEnergy will return 0.
     */
    boolean canExtract();

    /**
     * Used to determine if this storage can receive mana.
     * If this is false, then any calls to receiveEnergy will return 0.
     */
    boolean canReceive();
}

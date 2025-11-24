package dev.screret.sas.api.capability.mana;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;

public class Mana {

    protected int mana;
    protected int capacity;
    protected int maxReceive;
    protected int maxExtract;

    public Mana(int capacity) {
        this(capacity, capacity, capacity, 0);
    }

    public Mana(int capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public Mana(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public Mana(int capacity, int maxReceive, int maxExtract, int mana) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.mana = Math.max(0, Math.min(capacity, mana));
    }

    public int addMana(int maxReceive, boolean simulate) {
        if (!canReceive())
            return 0;

        int manaReceived = Math.min(capacity - mana, Math.min(this.maxReceive, maxReceive));
        if (!simulate)
            mana += manaReceived;
        return manaReceived;
    }

    public int deductMana(int maxExtract, boolean simulate) {
        if (!canExtract())
            return 0;

        int manaExtracted = Math.min(mana, Math.min(this.maxExtract, maxExtract));
        if (!simulate)
            mana -= manaExtracted;
        return manaExtracted;
    }

    public int getMaxManaStored() {
        return capacity;
    }

    public void setMaxManaStored(int max) {
        this.capacity = max;
    }

    public int getManaStored() {
        return mana;
    }

    public void setManaStored(int mana) {
        this.mana = mana;
    }

    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    public Tag serializeNBT() {
        return IntTag.valueOf(this.getManaStored());
    }

    public void deserializeNBT(Tag nbt) {
        if (!(nbt instanceof IntTag intNbt))
            throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
        this.mana = intNbt.getAsInt();
    }
}

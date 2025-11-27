package dev.screret.mitm.api.capability.ability;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.api.wand.ability.WandAbility;
import dev.screret.mitm.api.wand.ability.WandAbilityInstance;

import java.util.List;

public class CapabilityWandAbility {

    public static final ItemCapability<CapabilityWandAbility, Void> WAND_ABILITY = ItemCapability.createVoid(MITMUtil.id("wand_ability"), CapabilityWandAbility.class);

    private WandAbilityInstance ability;
    private WandAbilityInstance crouchAbility;

    private boolean isPoweredUp;

    public CapabilityWandAbility(WandAbilityInstance ability, WandAbilityInstance crouchAbility, boolean isPoweredUp) {
        this.ability = ability;
        this.crouchAbility = crouchAbility;
        this.isPoweredUp = isPoweredUp;
    }

    public static CapabilityWandAbility wandAbility(ItemStack stack) {
        CompoundTag nbt = stack.getOrCreateTagElement(WandAbility.ABILITIES_KEY);

        var main = new WandAbilityInstance(nbt.getCompound(WandAbility.MAIN_ABILITY_KEY));

        WandAbilityInstance crouch = null;
        if (nbt.contains(WandAbility.CROUCH_ABILITY_KEY, Tag.TAG_COMPOUND)) {
            crouch = new WandAbilityInstance(nbt.getCompound(WandAbility.CROUCH_ABILITY_KEY));
        }

        boolean isPoweredUp = false;
        if (nbt.contains(WandAbility.POWERED_UP_KEY, Tag.TAG_ANY_NUMERIC)) {
            isPoweredUp = nbt.getBoolean(WandAbility.POWERED_UP_KEY);
        }
        return new CapabilityWandAbility(main, crouch, isPoweredUp);
    }

    public WandAbilityInstance getCrouchAbility() {
        return crouchAbility;
    }

    public WandAbilityInstance getMainAbility() {
        return ability;
    }

    public void setPoweredUp(boolean poweredUp) {
        this.isPoweredUp = poweredUp;
    }

    public boolean getPoweredUp() {
        return isPoweredUp;
    }

    public void setMainAbility(WandAbilityInstance ability) {
        this.ability = ability;
    }

    public void setCrouchAbility(WandAbilityInstance ability) {
        this.crouchAbility = ability;
    }

    public List<WandAbilityInstance> getAll() {
        return Lists.newArrayList(ability, crouchAbility);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (ability != null)
            tag.put(WandAbility.MAIN_ABILITY_KEY, ability.serializeNBT());
        if (crouchAbility != null)
            tag.put(WandAbility.CROUCH_ABILITY_KEY, crouchAbility.serializeNBT());
        tag.putBoolean(WandAbility.POWERED_UP_KEY, isPoweredUp);
        return tag;
    }

    public void deserializeNBT(CompoundTag nbt) {
        ability.deserializeNBT(nbt.getCompound(WandAbility.MAIN_ABILITY_KEY));
        if (crouchAbility != null)
            crouchAbility.deserializeNBT(nbt.getCompound(WandAbility.CROUCH_ABILITY_KEY));
        isPoweredUp = nbt.contains(WandAbility.POWERED_UP_KEY, Tag.TAG_ANY_NUMERIC) && nbt.getBoolean(WandAbility.POWERED_UP_KEY);
    }
}

package screret.sas.api.capability.ability;

import com.google.common.collect.Lists;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import screret.sas.SpellsAndSorcerers;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;

import java.util.List;

public class CapabilityWandAbility implements ICapabilityWandAbility {

    private WandAbilityInstance ability;
    private WandAbilityInstance crouchAbility;

    private boolean isPoweredUp;

    public CapabilityWandAbility(WandAbilityInstance ability, WandAbilityInstance crouchAbility, boolean isPoweredUp) {
        this.ability = ability;
        this.crouchAbility = crouchAbility;
        this.isPoweredUp = isPoweredUp;
    }

    @Override
    public WandAbilityInstance getCrouchAbility() {
        return crouchAbility;
    }

    @Override
    public WandAbilityInstance getMainAbility() {
        return ability;
    }

    @Override
    public void setPoweredUp(boolean poweredUp) {
        this.isPoweredUp = poweredUp;
    }

    @Override
    public boolean getPoweredUp() {
        return isPoweredUp;
    }

    @Override
    public void setMainAbility(WandAbilityInstance ability) {
        this.ability = ability;
    }

    @Override
    public void setCrouchAbility(WandAbilityInstance ability) {
        this.crouchAbility = ability;
    }


    @Override
    public List<WandAbilityInstance> getAll() {
        return Lists.newArrayList(ability, crouchAbility);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if(ability != null) tag.put(WandAbility.BASIC_ABILITY_KEY, ability.serializeNBT());
        if(crouchAbility != null) tag.put(WandAbility.CROUCH_ABILITY_KEY, crouchAbility.serializeNBT());
        tag.putBoolean(WandAbility.POWERED_UP_KEY, isPoweredUp);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        ability.deserializeNBT(nbt.getCompound(WandAbility.BASIC_ABILITY_KEY));
        if(crouchAbility != null) crouchAbility.deserializeNBT(nbt.getCompound(WandAbility.CROUCH_ABILITY_KEY));
        isPoweredUp = nbt.contains(WandAbility.POWERED_UP_KEY, Tag.TAG_ANY_NUMERIC) && nbt.getBoolean(WandAbility.POWERED_UP_KEY);
    }
}

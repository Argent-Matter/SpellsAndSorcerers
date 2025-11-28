package dev.screret.mitm.api.capability.ability;

import com.google.common.collect.Lists;

import net.neoforged.neoforge.capabilities.ItemCapability;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.api.ability.WandAbilityInstance;

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
}

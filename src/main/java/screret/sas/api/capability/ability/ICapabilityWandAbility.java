package screret.sas.api.capability.ability;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.common.util.INBTSerializable;
import screret.sas.Util;
import screret.sas.api.wand.ability.WandAbilityInstance;

import java.util.List;

public interface ICapabilityWandAbility extends INBTSerializable<CompoundTag> {

    ItemCapability<ICapabilityWandAbility, Void> WAND_ABILITY = ItemCapability.createVoid(Util.id("wand_ability"), ICapabilityWandAbility.class);

    WandAbilityInstance getCrouchAbility();

    WandAbilityInstance getMainAbility();

    void setPoweredUp(boolean poweredUp);

    boolean getPoweredUp();

    void setMainAbility(WandAbilityInstance ability);

    void setCrouchAbility(WandAbilityInstance ability);

    List<WandAbilityInstance> getAll();
}


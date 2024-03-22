package screret.sas.api.capability.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;
import screret.sas.Util;
import screret.sas.api.wand.ability.WandAbilityInstance;

import java.util.List;

public interface ICapabilityWandAbility extends INBTSerializable<CompoundTag> {

    WandAbilityInstance getCrouchAbility();

    WandAbilityInstance getAbility();

    void setPoweredUp(boolean poweredUp);

    boolean getPoweredUp();

    void setMainAbility(WandAbilityInstance ability);

    void setCrouchAbility(WandAbilityInstance ability);

    List<WandAbilityInstance> getAll();
}


package screret.sas.api.capability.ability;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import screret.sas.api.wand.ability.WandAbilityInstance;

import java.util.List;

public interface ICapabilityWandAbility extends INBTSerializable<CompoundTag> {

    WandAbilityInstance getCrouchAbility();

    WandAbilityInstance getMainAbility();

    void setPoweredUp(boolean poweredUp);

    boolean getPoweredUp();

    void setMainAbility(WandAbilityInstance ability);

    void setCrouchAbility(WandAbilityInstance ability);

    List<WandAbilityInstance> getAll();
}


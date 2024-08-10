package screret.sas.api.capability.mana;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import screret.sas.api.capability.ability.CapabilityWandAbility;
import screret.sas.api.capability.ability.ICapabilityWandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;
import screret.sas.config.SASConfig;

import javax.annotation.Nullable;

public class ManaProvider implements ICapabilitySerializable<Tag> {

    public static final Capability<ICapabilityMana> MANA = CapabilityManager.get(new CapabilityToken<>() {});

    CapabilityMana backend = null;
    LazyOptional<ICapabilityMana> optionalStorage = LazyOptional.of(this::createCapability);

    public ManaProvider() {

    }

    @NotNull
    public CapabilityMana createCapability() {
        if(backend == null) {
            backend = new CapabilityMana(SASConfig.Server.maxDefaultMana.get(), SASConfig.Server.maxDefaultMana.get(), SASConfig.Server.maxDefaultMana.get(), SASConfig.Server.maxDefaultMana.get());
        }
        return backend;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ManaProvider.MANA) {
            return optionalStorage.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public Tag serializeNBT() {
        if(optionalStorage.isPresent()) {
            return optionalStorage.resolve().get().serializeNBT();
        }
        if(backend == null) createCapability();
        return backend.serializeNBT();
    }

    @Override
    public void deserializeNBT(Tag tag) {
        if(optionalStorage.isPresent()) {
            optionalStorage.resolve().get().deserializeNBT(tag);
        }
    }
}

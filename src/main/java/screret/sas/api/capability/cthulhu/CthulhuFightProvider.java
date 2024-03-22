package screret.sas.api.capability.cthulhu;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.CapabilityManager;
import net.neoforged.neoforge.common.capabilities.CapabilityToken;
import net.neoforged.neoforge.common.capabilities.ICapabilitySerializable;
import net.neoforged.neoforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import screret.sas.api.capability.ability.ICapabilityWandAbility;
import screret.sas.api.capability.ability.WandAbilityProvider;

import javax.annotation.Nullable;

public class CthulhuFightProvider implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<ICapabilityCthulhuFight> CTHULHU_FIGHT = CapabilityManager.get(new CapabilityToken<>(){});

    private final ServerLevel level;

    CapabilityCthulhuFight backend = null;
    LazyOptional<ICapabilityCthulhuFight> optionalStorage = LazyOptional.of(this::createCapability);

    public CthulhuFightProvider(ServerLevel level){
        this.level = level;
    }

    @NotNull
    public CapabilityCthulhuFight createCapability(){
        if(backend == null){
            backend = new CapabilityCthulhuFight(level);
        }
        return backend;
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == WandAbilityProvider.WAND_ABILITY) {
            return optionalStorage.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return createCapability().serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        createCapability().deserializeNBT(nbt);
    }
}

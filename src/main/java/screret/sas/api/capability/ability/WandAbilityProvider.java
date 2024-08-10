package screret.sas.api.capability.ability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import screret.sas.SpellsAndSorcerers;
import screret.sas.api.wand.ability.WandAbilityInstance;

import javax.annotation.Nullable;

public class WandAbilityProvider implements ICapabilitySerializable<CompoundTag> {

    public static final Capability<ICapabilityWandAbility> WAND_ABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private final WandAbilityInstance main, crouch;
    private boolean isPoweredUp;

    CapabilityWandAbility backend = null;
    LazyOptional<ICapabilityWandAbility> optionalStorage = LazyOptional.of(this::createCapability);

    public WandAbilityProvider(WandAbilityInstance mainAbility, WandAbilityInstance crouchAbility, boolean isPoweredUp) {
        this.main = mainAbility;
        this.crouch = crouchAbility;
        this.isPoweredUp = isPoweredUp;

    }

    @NotNull
    public CapabilityWandAbility createCapability() {
        if(backend == null) {
            backend = new CapabilityWandAbility(main, crouch, isPoweredUp);
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
        if(optionalStorage.isPresent()) {
            return optionalStorage.resolve().get().serializeNBT();
        }
        if(backend == null) createCapability();
        return backend.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if(optionalStorage.isPresent()) {
            optionalStorage.resolve().get().deserializeNBT(tag);
        }
    }
}

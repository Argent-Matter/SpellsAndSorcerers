package screret.sas.api.capability.cthulhu;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.INBTSerializable;
import screret.sas.entity.entity.boss.cthulhu.CthulhuFight;

public class CapabilityCthulhuFight implements ICapabilityCthulhuFight, INBTSerializable<CompoundTag> {
    private CthulhuFight fight;
    private final ServerLevel level;

    public CapabilityCthulhuFight(ServerLevel level){
        this.level = level;
    }

    @Override
    public CthulhuFight getCurrentFight() {
        return fight;
    }

    @Override
    public void setCurrentFight(CthulhuFight fight) {
        this.fight = fight;
    }

    @Override
    public CompoundTag serializeNBT() {
        return fight.saveData();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        long seed = level.getServer().getWorldData().worldGenSettings().seed();
        this.fight = new CthulhuFight(level, seed, nbt);
    }
}

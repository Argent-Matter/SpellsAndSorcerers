package screret.sas.api.capability.cthulhu;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import screret.sas.entity.entity.boss.cthulhu.CthulhuFight;

public class CthulhuFightSavedData extends SavedData implements ICapabilityCthulhuFight {
    public static CthulhuFightSavedData getOrCreate(ServerLevel level) {
        return level.getDataStorage().get(new SavedData.Factory<>(() -> new CthulhuFightSavedData(level), (tag) -> load(level, tag)), "cthulhu_fight");
    }

    private CthulhuFight fight;

    public CthulhuFightSavedData(ServerLevel level) {
        long seed = level.getServer().getWorldData().worldGenOptions().seed();
        this.fight = new CthulhuFight(level, seed, new CompoundTag());
    }

    public CthulhuFightSavedData(CthulhuFight fight) {
        this.fight = fight;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        return fight.saveData(tag);
    }

    public static CthulhuFightSavedData load(ServerLevel level, CompoundTag nbt) {
        long seed = level.getServer().getWorldData().worldGenOptions().seed();
        CthulhuFight fight = new CthulhuFight(level, seed, nbt);
        return new CthulhuFightSavedData(fight);
    }

    @Override
    public CthulhuFight getCurrentFight() {
        return fight;
    }

    @Override
    public void setCurrentFight(CthulhuFight fight) {
        this.fight = fight;
    }
}

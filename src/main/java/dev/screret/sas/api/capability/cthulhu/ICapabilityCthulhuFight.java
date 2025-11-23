package dev.screret.sas.api.capability.cthulhu;

import dev.screret.sas.entity.entity.boss.cthulhu.CthulhuFight;

public interface ICapabilityCthulhuFight {

    CthulhuFight getCurrentFight();

    void setCurrentFight(CthulhuFight fight);
}

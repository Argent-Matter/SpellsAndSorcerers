package screret.sas.api.capability.cthulhu;

import screret.sas.entity.entity.boss.cthulhu.CthulhuFight;

public interface ICapabilityCthulhuFight {

    CthulhuFight getCurrentFight();

    void setCurrentFight(CthulhuFight fight);
}

package dev.screret.sas.client.model.entity;

import dev.screret.sas.Util;
import dev.screret.sas.entity.boss.cthulhu.CthulhuEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CthulhuModel extends DefaultedEntityGeoModel<CthulhuEntity> {
    public CthulhuModel() {
        super(Util.id("cthulhu"), true);
    }
}

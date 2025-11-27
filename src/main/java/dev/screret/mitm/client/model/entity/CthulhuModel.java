package dev.screret.mitm.client.model.entity;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.common.entity.boss.cthulhu.CthulhuEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CthulhuModel extends DefaultedEntityGeoModel<CthulhuEntity> {
    public CthulhuModel() {
        super(MITMUtil.id("cthulhu"), true);
    }
}

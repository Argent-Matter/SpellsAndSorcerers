package screret.sas.client.model.entity;

import screret.sas.Util;
import screret.sas.entity.entity.boss.cthulhu.CthulhuEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class CthulhuModel extends DefaultedEntityGeoModel<CthulhuEntity> {
    public CthulhuModel() {
        super(Util.id("cthulhu"), true);
    }
}

package screret.sas.client.model.entity;

import screret.sas.Util;
import screret.sas.entity.entity.BossWizardEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BossWizardModel extends DefaultedEntityGeoModel<BossWizardEntity> {
    public BossWizardModel() {
        super(Util.id("boss_wizard"), true);
    }
}

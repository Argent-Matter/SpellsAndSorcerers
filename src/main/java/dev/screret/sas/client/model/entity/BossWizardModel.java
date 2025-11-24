package dev.screret.sas.client.model.entity;

import dev.screret.sas.Util;
import dev.screret.sas.entity.BossWizardEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BossWizardModel extends DefaultedEntityGeoModel<BossWizardEntity> {
    public BossWizardModel() {
        super(Util.id("boss_wizard"), true);
    }
}

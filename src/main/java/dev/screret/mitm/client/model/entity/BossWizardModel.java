package dev.screret.mitm.client.model.entity;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.common.entity.BossWizardEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BossWizardModel extends DefaultedEntityGeoModel<BossWizardEntity> {
    public BossWizardModel() {
        super(MITMUtil.id("boss_wizard"), true);
    }
}

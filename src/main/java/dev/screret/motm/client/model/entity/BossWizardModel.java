package dev.screret.motm.client.model.entity;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.common.entity.BossWizardEntity;

import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class BossWizardModel extends DefaultedEntityGeoModel<BossWizardEntity> {

    public BossWizardModel() {
        super(MOTMUtil.id("boss_wizard"), true);
    }
}

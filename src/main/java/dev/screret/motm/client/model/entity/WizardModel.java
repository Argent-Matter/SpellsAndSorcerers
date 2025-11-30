package dev.screret.mitm.client.model.entity;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.common.entity.WizardEntity;

import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class WizardModel extends DefaultedEntityGeoModel<WizardEntity> {

    public WizardModel() {
        super(MOTMUtil.id("wizard"), true);
    }
}

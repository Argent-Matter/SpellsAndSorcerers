package dev.screret.mitm.client.model.entity;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.common.entity.WizardEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class WizardModel extends DefaultedEntityGeoModel<WizardEntity> {
    public WizardModel() {
        super(MITMUtil.id("wizard"), true);
    }

}

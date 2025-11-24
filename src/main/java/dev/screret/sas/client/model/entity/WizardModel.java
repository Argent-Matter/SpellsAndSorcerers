package dev.screret.sas.client.model.entity;

import dev.screret.sas.Util;
import dev.screret.sas.common.entity.WizardEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class WizardModel extends DefaultedEntityGeoModel<WizardEntity> {
    public WizardModel() {
        super(Util.id("wizard"), true);
    }

}

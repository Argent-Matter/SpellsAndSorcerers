package screret.sas.client.model.entity;

import screret.sas.Util;
import screret.sas.entity.entity.WizardEntity;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;

public class WizardModel extends DefaultedEntityGeoModel<WizardEntity> {
    public WizardModel() {
        super(Util.id("wizard"), true);
    }

}

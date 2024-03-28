package screret.sas.client.renderer.blockentity;

import screret.sas.blockentity.blockentity.PalantirBlockEntity;
import screret.sas.client.model.blockentity.PalantirModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PalantirBERenderer extends GeoBlockRenderer<PalantirBlockEntity> {
    public PalantirBERenderer() {
        super(new PalantirModel());
    }
}

package screret.sas.client.renderer.blockentity;

import screret.sas.blockentity.blockentity.PalantirBE;
import screret.sas.client.model.blockentity.PalantirModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PalantirBERenderer extends GeoBlockRenderer<PalantirBE> {
    public PalantirBERenderer() {
        super(new PalantirModel());
    }
}

package dev.screret.sas.client.renderer.blockentity;

import dev.screret.sas.blockentity.PalantirBlockEntity;
import dev.screret.sas.client.model.blockentity.PalantirModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PalantirBERenderer extends GeoBlockRenderer<PalantirBlockEntity> {
    public PalantirBERenderer() {
        super(new PalantirModel());
    }
}

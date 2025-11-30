package dev.screret.motm.client.renderer.blockentity;

import dev.screret.motm.client.model.blockentity.PalantirModel;
import dev.screret.motm.common.block.entity.PalantirBlockEntity;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PalantirBERenderer extends GeoBlockRenderer<PalantirBlockEntity> {

    public PalantirBERenderer() {
        super(new PalantirModel());
    }
}

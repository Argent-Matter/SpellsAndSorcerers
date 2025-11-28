package dev.screret.mitm.client.renderer.blockentity;

import dev.screret.mitm.client.model.blockentity.PalantirModel;
import dev.screret.mitm.common.block.entity.PalantirBlockEntity;

import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PalantirBERenderer extends GeoBlockRenderer<PalantirBlockEntity> {

    public PalantirBERenderer() {
        super(new PalantirModel());
    }
}

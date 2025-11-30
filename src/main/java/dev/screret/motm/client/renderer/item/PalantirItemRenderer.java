package dev.screret.motm.client.renderer.item;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.common.item.PalantirItem;

import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PalantirItemRenderer extends GeoItemRenderer<PalantirItem> {

    public PalantirItemRenderer() {
        super(new DefaultedItemGeoModel<>(MOTMUtil.id("palantir")));
    }
}

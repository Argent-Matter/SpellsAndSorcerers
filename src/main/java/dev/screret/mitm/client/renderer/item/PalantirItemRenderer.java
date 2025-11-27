package dev.screret.mitm.client.renderer.item;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.common.item.PalantirItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PalantirItemRenderer extends GeoItemRenderer<PalantirItem> {
    public PalantirItemRenderer() {
        super(new DefaultedItemGeoModel<>(MITMUtil.id("palantir")));
    }
}

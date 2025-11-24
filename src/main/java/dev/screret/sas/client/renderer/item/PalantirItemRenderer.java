package dev.screret.sas.client.renderer.item;

import dev.screret.sas.Util;
import dev.screret.sas.item.PalantirItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PalantirItemRenderer extends GeoItemRenderer<PalantirItem> {
    public PalantirItemRenderer() {
        super(new DefaultedItemGeoModel<>(Util.id("palantir")));
    }
}

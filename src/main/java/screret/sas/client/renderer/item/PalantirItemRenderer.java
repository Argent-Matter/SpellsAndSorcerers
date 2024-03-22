package screret.sas.client.renderer.item;

import screret.sas.Util;
import screret.sas.item.item.PalantirItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PalantirItemRenderer extends GeoItemRenderer<PalantirItem> {
    public PalantirItemRenderer() {
        super(new DefaultedItemGeoModel<>(Util.id("palantir")));
    }
}

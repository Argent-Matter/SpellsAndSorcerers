package screret.sas.client.renderer.armor;

import screret.sas.Util;
import screret.sas.item.item.ModArmorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class SoulsteelArmorRenderer extends GeoArmorRenderer<ModArmorItem> {
    public SoulsteelArmorRenderer() {
        super(new DefaultedItemGeoModel<>(Util.id("armor/soulsteel_armor")));
    }
}

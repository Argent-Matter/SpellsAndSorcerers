package dev.screret.sas.client.renderer.armor;

import dev.screret.sas.Util;
import dev.screret.sas.item.ModArmorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class SoulsteelArmorRenderer extends GeoArmorRenderer<ModArmorItem> {
    public SoulsteelArmorRenderer() {
        super(new DefaultedItemGeoModel<>(Util.id("armor/soulsteel_armor")));
    }
}

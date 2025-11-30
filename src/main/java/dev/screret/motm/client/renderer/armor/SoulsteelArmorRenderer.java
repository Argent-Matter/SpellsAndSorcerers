package dev.screret.mitm.client.renderer.armor;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.common.item.MOTMArmorItem;

import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class SoulsteelArmorRenderer extends GeoArmorRenderer<MOTMArmorItem> {

    public SoulsteelArmorRenderer() {
        super(new DefaultedItemGeoModel<>(MOTMUtil.id("armor/soulsteel_armor")));
    }
}

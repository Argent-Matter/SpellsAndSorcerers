package dev.screret.mitm.client.renderer.armor;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.common.item.MITMArmorItem;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class SoulsteelArmorRenderer extends GeoArmorRenderer<MITMArmorItem> {
    public SoulsteelArmorRenderer() {
        super(new DefaultedItemGeoModel<>(MITMUtil.id("armor/soulsteel_armor")));
    }
}

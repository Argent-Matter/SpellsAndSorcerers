package dev.screret.mitm.client.renderer.blockentity;

import dev.screret.mitm.common.block.SummonSignBlock;
import dev.screret.mitm.common.block.entity.SummonSignBlockEntity;
import dev.screret.mitm.client.model.blockentity.SummonSignModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.util.Color;

public class SummonSignBERenderer extends GeoBlockRenderer<SummonSignBlockEntity> {
    public SummonSignBERenderer() {
        super(new SummonSignModel());
    }

    @Override
    public Color getRenderColor(SummonSignBlockEntity animatable, float partialTick, int packedLight) {
        int value = animatable.getBlockState().getValue(SummonSignBlock.COLOR).getTextureDiffuseColor();
        return new Color(value);
    }
}

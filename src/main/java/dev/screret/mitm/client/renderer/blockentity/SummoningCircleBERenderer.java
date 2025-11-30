package dev.screret.mitm.client.renderer.blockentity;

import dev.screret.mitm.client.model.blockentity.SummoningCircleModel;
import dev.screret.mitm.common.block.SummoningCircleBlock;
import dev.screret.mitm.common.block.entity.SummoningCircleBlockEntity;

import software.bernie.geckolib.renderer.GeoBlockRenderer;
import software.bernie.geckolib.util.Color;

public class SummoningCircleBERenderer extends GeoBlockRenderer<SummoningCircleBlockEntity> {

    public SummoningCircleBERenderer() {
        super(new SummoningCircleModel());
    }

    @Override
    public Color getRenderColor(SummoningCircleBlockEntity animatable, float partialTick, int packedLight) {
        int value = animatable.getBlockState().getValue(SummoningCircleBlock.COLOR).getTextureDiffuseColor();
        return new Color(value);
    }
}

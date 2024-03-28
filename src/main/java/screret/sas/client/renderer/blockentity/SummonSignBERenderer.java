package screret.sas.client.renderer.blockentity;

import screret.sas.block.block.SummonSignBlock;
import screret.sas.blockentity.blockentity.SummonSignBlockEntity;
import screret.sas.client.model.blockentity.SummonSignModel;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class SummonSignBERenderer extends GeoBlockRenderer<SummonSignBlockEntity> {
    public SummonSignBERenderer() {
        super(new SummonSignModel());
    }

    @Override
    public Color getRenderColor(SummonSignBlockEntity animatable, float partialTick, int packedLight) {
        var colors = animatable.getBlockState().getValue(SummonSignBlock.COLOR).getTextureDiffuseColors();
        return Color.ofRGB(colors[0], colors[1], colors[2]);
    }
}

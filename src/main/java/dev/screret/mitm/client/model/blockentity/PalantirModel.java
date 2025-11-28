package dev.screret.mitm.client.model.blockentity;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.common.block.entity.PalantirBlockEntity;

import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PalantirModel extends DefaultedBlockGeoModel<PalantirBlockEntity> {

    public PalantirModel() {
        super(MITMUtil.id("palantir"));
    }

    @Override
    public RenderType getRenderType(PalantirBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public void setCustomAnimations(PalantirBlockEntity animatable, long instanceId,
                                    AnimationState<PalantirBlockEntity> animationState) {
        GeoBone eye = this.getAnimationProcessor().getBone("eye");
        eye.setRotX(animatable.xRot * Mth.DEG_TO_RAD);
        eye.setRotY(animatable.yRot * Mth.DEG_TO_RAD);
    }
}

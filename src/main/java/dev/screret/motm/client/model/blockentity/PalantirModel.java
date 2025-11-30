package dev.screret.motm.client.model.blockentity;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.common.block.entity.PalantirBlockEntity;

import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class PalantirModel extends DefaultedBlockGeoModel<PalantirBlockEntity> {

    public PalantirModel() {
        super(MOTMUtil.id("palantir"));
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

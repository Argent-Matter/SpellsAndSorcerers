package screret.sas.client.model.blockentity;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import screret.sas.Util;
import screret.sas.blockentity.blockentity.PalantirBlockEntity;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class PalantirModel extends DefaultedBlockGeoModel<PalantirBlockEntity> {

    public PalantirModel() {
        super(Util.id("palantir"));
    }

    @Override
    public RenderType getRenderType(PalantirBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(getTextureResource(animatable));
    }

    @Override
    public void setCustomAnimations(PalantirBlockEntity animatable, long instanceId, AnimationState<PalantirBlockEntity> animationState) {
        CoreGeoBone eye = this.getAnimationProcessor().getBone("eye");
        eye.setRotX(animatable.xRot * Mth.DEG_TO_RAD);
        eye.setRotY(animatable.yRot * Mth.DEG_TO_RAD);
    }
}

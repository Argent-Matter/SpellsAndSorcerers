package screret.sas.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import screret.sas.Util;
import screret.sas.client.model.entity.WizardModel;
import screret.sas.entity.entity.BossWizardEntity;
import screret.sas.entity.entity.WizardEntity;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WizardRenderer extends GeoEntityRenderer<WizardEntity> {
    public WizardRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WizardModel());
    }

    @Override
    public void postRender(PoseStack poseStack, WizardEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight,
                           int packedOverlay, float red, float green, float blue, float alpha) {
        if (model.getBone("rightArm").isPresent() && Util.getMainAbilityFromStack(animatable.getMainHandItem()).isPresent()) {
            var handWorldPos = model.getBone("rightArm").get().getWorldPosition();
            if (animatable.isCastingSpell()) {
                animatable.getCommandSenderWorld().addParticle(Util.getMainAbilityFromStack(animatable.getMainHandItem()).get().getAbility().getParticle(),
                        handWorldPos.x,
                        handWorldPos.y,
                        handWorldPos.z,
                        (animatable.getRandom().nextDouble() - 0.5D), -animatable.getRandom().nextDouble(),
                        (animatable.getRandom().nextDouble() - 0.5D));
            }
        }
        super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight,
                packedOverlay, red, green, blue, alpha);
    }
}

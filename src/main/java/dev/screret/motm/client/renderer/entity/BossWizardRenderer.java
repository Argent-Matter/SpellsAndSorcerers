package dev.screret.motm.client.renderer.entity;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.client.model.entity.BossWizardModel;
import dev.screret.motm.common.entity.BossWizardEntity;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import org.jetbrains.annotations.Nullable;

public class BossWizardRenderer extends GeoEntityRenderer<BossWizardEntity> {

    public BossWizardRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new BossWizardModel());
    }

    @Override
    public void postRender(PoseStack poseStack, BossWizardEntity animatable, BakedGeoModel model,
                           MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick,
                           int packedLight, int packedOverlay, int colour) {
        if (model.getBone("rightArm").isPresent() && MOTMUtil.getMainAbilityFromStack(animatable.getMainHandItem()).isPresent()) {
            var handWorldPos = model.getBone("rightArm").get().getWorldPosition();
            if (animatable.isCastingSpell()) {
                animatable.getCommandSenderWorld().addParticle(
                        MOTMUtil.getMainAbilityFromStack(animatable.getMainHandItem()).get().getAbility().getParticle(),
                        handWorldPos.x,
                        handWorldPos.y,
                        handWorldPos.z,
                        (animatable.getRandom().nextDouble() - 0.5D), -animatable.getRandom().nextDouble(),
                        (animatable.getRandom().nextDouble() - 0.5D));
            }
        }
        super.postRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight,
                packedOverlay, colour);
    }
}

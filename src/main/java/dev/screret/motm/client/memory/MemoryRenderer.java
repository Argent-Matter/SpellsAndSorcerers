package dev.screret.motm.client.memory;

import dev.screret.modularui.client.schemarenderer.BaseSchemaRenderer;
import dev.screret.modularui.utils.fakelevel.SchemaLevel;
import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.api.memory.animation.MemoryGeoModel;
import dev.screret.motm.api.util.StructureUtil;

import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.RenderUtil;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.Getter;
import org.joml.Matrix4f;

import java.util.concurrent.atomic.AtomicReference;

import org.jetbrains.annotations.Nullable;

/**
 * {@link GeoRenderer} class for rendering {@link Memory MemoryAnimations}
 */
@SuppressWarnings("UnstableApiUsage")
@EventBusSubscriber(value = Dist.CLIENT)
public class MemoryRenderer extends GeoObjectRenderer<Memory> {

    public static final MemoryRenderer INSTANCE = new MemoryRenderer();

    protected BlockPos currentPos;
    @Getter
    protected ActiveMemory currentMemory;

    // non-final so it can be recreated on relog (to stop memory leaks)
    @Getter
    private SchemaLevel fakeLevel = null;
    @Getter
    private BaseSchemaRenderer fakeLevelRenderer = null;

    public MemoryRenderer() {
        super(MemoryGeoModel.INSTANCE);
    }

    public void startMemory(BlockPos pos, ActiveMemory activeMemory) {
        this.currentPos = pos;
        this.currentMemory = activeMemory;
        this.animatable = activeMemory.memory().value();
    }

    public void loadStructure(StructureTemplate structure, BlockPos pos) {
        // clear whatever remaining data might be there
        this.fakeLevel.getChunkSource().clear();
        StructureUtil.placeInWorld(structure, this.fakeLevel, )
        structure.placeInWorld()
    }

    private void resetFakeLevel() {
        this.fakeLevel = new SchemaLevel();
        this.fakeLevelRenderer = new BaseSchemaRenderer(this.fakeLevel);
    }

    @Override
    public long getInstanceId(Memory animatable) {
        return Memory.getAnimatableId(this.currentPos);
    }

    @Override
    public void actuallyRender(PoseStack poseStack, Memory animatable, BakedGeoModel model, @Nullable RenderType renderType,
                               MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender,
                               float partialTick,
                               int packedLight, int packedOverlay, int colour) {
        // disable hitboxes while rendering the fake entities
        EntityRenderDispatcher entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher();
        boolean oldRenderHitboxes = entityRenderer.shouldRenderHitBoxes();
        entityRenderer.setRenderHitBoxes(false);

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, colour);

        entityRenderer.setRenderHitBoxes(oldRenderHitboxes);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, Memory animatable, GeoBone bone,
                                  RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                  boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.isTrackingMatrices()) {
            Matrix4f poseState = new Matrix4f(poseStack.last().pose());

            bone.setModelSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.modelRenderTranslations));
            bone.setLocalSpaceMatrix(RenderUtil.invertAndMultiplyMatrices(poseState, this.objectRenderTranslations));
        }
        poseStack.pushPose();
        RenderUtil.prepMatrixForBone(poseStack, bone);

        String entityMarker = bone.getName();
        LivingEntity entity = this.currentMemory.entities().get(entityMarker);
        if (entity == null) {
            // log & skip invalid entities
            MagicOfTheMind.LOGGER.warn("Invalid entity marker (bone name) {} in memory {}",
                    entityMarker, this.currentMemory.memory().getKey().location());
            return;
        }

        Vec3 cameraPos = CURRENT_CAMERA.get().getPosition();
        EntityRenderDispatcher entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher();
        if (entityRenderer.shouldRender(entity, CURRENT_FRUSTUM.get(), cameraPos.x, cameraPos.y, cameraPos.z)) {
            double xPos = Mth.lerp(partialTick, entity.xOld, entity.getX()) - cameraPos.x;
            double yPos = Mth.lerp(partialTick, entity.yOld, entity.getY()) - cameraPos.y;
            double zPos = Mth.lerp(partialTick, entity.zOld, entity.getZ()) - cameraPos.z;
            float yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
            entityRenderer.render(entity, xPos, yPos, zPos, yRot, partialTick, poseStack, bufferSource, packedLight);
        }

        // skip rendering the actual bone's cubes here.

        renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight,
                packedOverlay, colour);

        poseStack.popPose();
    }

    private static final AtomicReference<Camera> CURRENT_CAMERA = new AtomicReference<>();
    private static final AtomicReference<Frustum> CURRENT_FRUSTUM = new AtomicReference<>();

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (!event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS)) {
            return;
        }
        CURRENT_CAMERA.set(event.getCamera());
        CURRENT_FRUSTUM.set(event.getFrustum());

        MemoryRenderer renderer = MemoryRenderer.INSTANCE;
        if (renderer.currentMemory == null) {
            return;
        }
        renderer.render(event.getPoseStack(), renderer.animatable, null, RenderType.TRANSLUCENT, null,
                LightTexture.FULL_SKY, event.getPartialTick().getGameTimeDeltaPartialTick(false));
    }

    @SubscribeEvent
    public static void onWorldLoad(ClientPlayerNetworkEvent.LoggingIn event) {
        MemoryRenderer.INSTANCE.resetFakeLevel();
    }

    @SubscribeEvent
    public static void onRespawn(ClientPlayerNetworkEvent.Clone event) {
        MemoryRenderer.INSTANCE.resetFakeLevel();
    }
}

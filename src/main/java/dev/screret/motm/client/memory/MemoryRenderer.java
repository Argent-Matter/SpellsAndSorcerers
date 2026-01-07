package dev.screret.motm.client.memory;

import dev.screret.modularui.client.schemarenderer.BaseSchemaRenderer;
import dev.screret.modularui.utils.fakelevel.SchemaLevel;
import dev.screret.modularui.utils.math.MathHelper;
import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.api.util.worldgen.ExtendedStructurePlaceSettings;
import dev.screret.motm.api.util.worldgen.StructureUtil;
import dev.screret.motm.client.util.BufferSourceUtil;
import dev.screret.motm.common.memory.MemoryGeoModel;

import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoObjectRenderer;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.util.RenderUtil;

import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import lombok.Getter;
import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

import org.jetbrains.annotations.Nullable;

/**
 * {@link GeoRenderer} class for rendering {@link Memory MemoryAnimations}
 */
@SuppressWarnings("UnstableApiUsage")
@EventBusSubscriber(Dist.CLIENT)
public class MemoryRenderer extends GeoObjectRenderer<Memory> {

    public static final MemoryRenderer INSTANCE = new MemoryRenderer();

    public static final RawAnimation MEMORY_ANIMATION = RawAnimation.begin().thenLoop("memory");
    public static final int MAX_DISTANCE_FROM_MEMORY_BOUNDS = 8;

    protected BlockPos currentPos;
    protected @Nullable BoundingBox currentStructureBounds;
    @Getter
    protected Holder<Memory> currentMemory;
    @Getter
    protected final Map<String, Set<Entity>> entities = new HashMap<>();

    // non-final so it can be recreated on relog (to stop memory leaks)
    @Getter
    private SchemaLevel fakeLevel = null;
    @Getter
    private BaseSchemaRenderer fakeLevelRenderer = null;

    private MemoryRenderer() {
        super(MemoryGeoModel.INSTANCE);
    }

    public void startMemory(BlockPos pos, Holder<Memory> memory) {
        this.currentPos = pos;
        this.currentMemory = memory;

        this.entities.clear();
        ClientMemoryCache.MEMORY_STRUCTURE_CACHE.getUnchecked(this.currentMemory.value().getInitialStructure())
                .whenComplete((structure, error) -> {
                    if (error != null) {
                        Minecraft.getInstance().delayCrash(CrashReport.forThrowable(error, "Rendering memory structure"));
                        return;
                    }
                    if (this.hasActiveMemory()) {
                        this.loadStructure(this.currentMemory.value().getInitialStructure(), structure);
                    }
                    assert Minecraft.getInstance().player != null;
                    this.currentMemory.value().playAnimation(MEMORY_ANIMATION, Minecraft.getInstance().player, pos);
                });
    }

    /**
     * End the currently playing memory immediately.
     */
    public void endCurrentMemory() {
        this.animatable = null;
        this.currentMemory = null;
        this.currentStructureBounds = null;
        this.currentPos = null;

        this.resetFakeLevel();
    }

    public void loadStructure(ResourceLocation name, StructureTemplate memoryStructure) {
        if (!hasActiveMemory()) {
            MagicOfTheMind.LOGGER.debug("Cannot load memory structure {} because a memory is not active.", name);
            return;
        }
        // clear whatever remaining data might be there
        this.fakeLevel.getChunkSource().clear();

        ExtendedStructurePlaceSettings settings = new ExtendedStructurePlaceSettings()
                .setMirror(Mirror.NONE).setRotation(Rotation.NONE)
                .setLiquidSettings(LiquidSettings.APPLY_WATERLOGGING)
                .setIgnoreBlocks(false).setIgnoreEntities(false)
                .setKnownShape(true);
        if (!StructureUtil.placeInWorld(memoryStructure, this.fakeLevel, this.currentPos, settings)) {
            MagicOfTheMind.LOGGER.debug("Could not load memory structure {}", name);
            return;
        }
        MagicOfTheMind.LOGGER.debug("Loaded memory structure {}", name);
        this.currentStructureBounds = memoryStructure.getBoundingBox(settings, this.currentPos);

        for (Entity entity : this.fakeLevel.getAllEntities()) {
            // map the entity to a bone by all of its tags
            // usually there's only one tag, though.
            for (String tag : entity.getTags()) {
                this.entities.computeIfAbsent(tag, $ -> new HashSet<>()).add(entity);
            }
            if (entity instanceof Mob mob) {
                mob.setNoAi(true);
            }
        }
    }

    public boolean hasActiveMemory() {
        return this.currentMemory != null;
    }

    private void resetFakeLevel() {
        this.entities.clear();
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
                               float partialTick, int packedLight, int packedOverlay, int colour) {
        if (this.entities.isEmpty()) {
            return;
        }

        // disable hitboxes while rendering the fake entities
        EntityRenderDispatcher entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher();
        boolean oldRenderHitboxes = entityRenderer.shouldRenderHitBoxes();
        entityRenderer.setRenderHitBoxes(false);

        poseStack.pushPose();

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer,
                isReRender, partialTick, packedLight, packedOverlay, colour);

        if (this.fakeLevel.hasFilledBlocks()) {
            Camera playerCamera = CURRENT_CAMERA.get();
            var fakeLevelCamera = this.fakeLevelRenderer.camera();
            fakeLevelCamera.setPosAndAngle(MathHelper.ZERO, 1.0f, playerCamera.getYRot(), playerCamera.getXRot());

            this.fakeLevelRenderer.renderWorld(BufferSourceUtil.getRealBufferSource(bufferSource), partialTick);
        }

        poseStack.popPose();
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

        String entityTag = bone.getName();
        Set<Entity> entities = this.entities.get(entityTag);
        if (entities != null) {
            Frustum frustum = CURRENT_FRUSTUM.get();
            Vec3 cameraPos = CURRENT_CAMERA.get().getPosition();
            EntityRenderDispatcher entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher();

            for (Entity entity : entities) {
                if (!entityRenderer.shouldRender(entity, frustum, cameraPos.x, cameraPos.y, cameraPos.z)) {
                    continue;
                }
                double xPos = Mth.lerp(partialTick, entity.xOld, entity.getX());
                double yPos = Mth.lerp(partialTick, entity.yOld, entity.getY());
                double zPos = Mth.lerp(partialTick, entity.zOld, entity.getZ());
                float yRot = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
                entityRenderer.render(entity, xPos, yPos, zPos, yRot, partialTick, poseStack, bufferSource, packedLight);
            }
        } else {
            // log & skip invalid entities
            MagicOfTheMind.LOGGER.warn("Invalid entity tag (bone name) {} in memory {} (no entities have that tag)",
                    entityTag, this.currentMemory.getKey().location());
        }

        // skip rendering the actual bone's cubes here.

        renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight,
                packedOverlay, colour);

        poseStack.popPose();
    }

    private static final AtomicReference<Camera> CURRENT_CAMERA = new AtomicReference<>();
    private static final AtomicReference<Frustum> CURRENT_FRUSTUM = new AtomicReference<>();

    @SubscribeEvent
    public static void renderMemory(RenderLevelStageEvent event) {
        if (!event.getStage().equals(RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS)) {
            return;
        }
        Camera camera = event.getCamera();
        CURRENT_CAMERA.set(camera);
        CURRENT_FRUSTUM.set(event.getFrustum());

        MemoryRenderer renderer = MemoryRenderer.INSTANCE;
        if (!renderer.hasActiveMemory()) {
            return;
        }

        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        poseStack.last().pose().translate(camera.getPosition().toVector3f().negate());

        renderer.render(poseStack, renderer.currentMemory.value(), null, RenderType.TRANSLUCENT, null,
                LightTexture.FULL_SKY, event.getPartialTick().getGameTimeDeltaPartialTick(false));

        poseStack.popPose();
    }

    @SubscribeEvent
    public static void escapeMemory(ClientTickEvent.Pre event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;

        MemoryRenderer renderer = MemoryRenderer.INSTANCE;
        if (!renderer.hasActiveMemory()) return;

        BoundingBox structureBounds = renderer.currentStructureBounds;
        if (structureBounds == null) return;
        structureBounds = structureBounds.inflatedBy(MAX_DISTANCE_FROM_MEMORY_BOUNDS);

        // if player is outside the current memory's boundaries, stop it
        if (!structureBounds.isInside(player.blockPosition())) {
            renderer.endCurrentMemory();
            player.sendSystemMessage(Component.translatable("motm.message.free_from_memory")
                    .withStyle(ChatFormatting.DARK_PURPLE));
        }
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

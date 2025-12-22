package dev.screret.motm.api.memory;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.registry.MOTMRegistries;
import dev.screret.motm.common.memory.MemoryKeyframeHandler;

import software.bernie.geckolib.GeckoLibServices;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.stateless.StatelessAnimationController;
import software.bernie.geckolib.animatable.stateless.StatelessGeoSingletonAnimatable;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.network.packet.StatelessSingletonPlayAnimPacket;
import software.bernie.geckolib.network.packet.StatelessSingletonStopAnimPacket;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;

import java.util.Optional;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class Memory implements StatelessGeoSingletonAnimatable {

    // spotless:off
    public static final Codec<Memory> DIRECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("animation_name").forGetter(Memory::getAnimationName),
            ResourceLocation.CODEC.fieldOf("model_name").forGetter(Memory::getModelName),
            ResourceLocation.CODEC.fieldOf("initial_structure").forGetter(Memory::getInitialStructure)
    ).apply(instance, Memory::new));
    public static final Codec<Holder<Memory>> CODEC = RegistryFileCodec.create(MOTMRegistries.MEMORY_REGISTRY, DIRECT_CODEC);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Memory>> STREAM_CODEC = ByteBufCodecs.holderRegistry(MOTMRegistries.MEMORY_REGISTRY);
    // spotless:on

    @Getter
    private final ResourceLocation animationName;
    @Getter
    private final ResourceLocation modelName;
    @Getter
    private final ResourceLocation initialStructure;

    /**
     * Create a new memory instance.
     * The asset path should be the truncated relative path from the base folder.
     * 
     * @param name animation/model/structure location relative to {@code <namespace>:animations|geo/motm/memory/}
     */
    public Memory(ResourceLocation name) {
        this(name, name, name);
    }

    /**
     * Create a new memory instance.
     * The asset path should be the truncated relative path from the base folder.
     *
     * @param animationName    The animation location relative to {@code <namespace>:animations/motm/memory/}
     * @param modelName        The model location relative to {@code <namespace>:geo/motm/memory/}
     * @param initialStructure The structure file to load initially.
     */
    public Memory(ResourceLocation animationName, ResourceLocation modelName, ResourceLocation initialStructure) {
        this.animationName = animationName;
        this.modelName = modelName;
        this.initialStructure = initialStructure;

        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    public static long getAnimatableId(BlockPos pos) {
        return pos.asLong();
    }

    // region animation play/stop methods

    /**
     * Start or continue an animation, letting its pre-defined loop type determine whether it should loop or not
     */
    public void playAnimation(String animation, Player player, BlockPos pos) {
        playAnimation(RawAnimation.begin().thenPlay(animation), player, pos);
    }

    /**
     * Start or continue an animation, forcing it to loop continuously until stopped
     */
    public void playLoopingAnimation(String animation, Player player, BlockPos pos) {
        playAnimation(RawAnimation.begin().thenLoop(animation), player, pos);
    }

    /**
     * Start or continue an animation, then hold the pose at the end of the animation until otherwise stopped
     */
    public void playAndHoldAnimation(String animation, Player player, BlockPos pos) {
        playAnimation(RawAnimation.begin().thenPlayAndHold(animation), player, pos);
    }

    /**
     * Stop an already-playing animation
     */
    public void stopAnimation(RawAnimation animation, Player player, BlockPos pos) {
        stopAnimation(
                animation.getStageCount() == 1 ? animation.getAnimationStages().getFirst().animationName() : animation.toString(),
                player, pos);
    }

    /**
     * Start or continue a pre-defined animation
     */
    public void playAnimation(RawAnimation animation, Player player, BlockPos pos) {
        if (player.level().isClientSide) {
            handleClientAnimationPlay(this, getAnimatableId(pos), animation);
        } else if (player instanceof ServerPlayer serverPlayer) {
            GeckoLibServices.NETWORK.sendToPlayer(
                    new StatelessSingletonPlayAnimPacket(GeckoLibUtil.getSyncedSingletonAnimatableId(this),
                            getAnimatableId(pos), animation),
                    serverPlayer);
        } else {
            MagicOfTheMind.LOGGER.error("Could not start memory animation for player {} ({}) at pos {}",
                    player.getName().getString(), player, pos.toShortString());
        }
    }

    /**
     * Stop an already-playing animation
     */
    public void stopAnimation(String animation, Player player, BlockPos pos) {
        if (player.level().isClientSide) {
            handleClientAnimationStop(this, getAnimatableId(pos), animation);
        } else if (player instanceof ServerPlayer serverPlayer) {
            GeckoLibServices.NETWORK.sendToPlayer(
                    new StatelessSingletonStopAnimPacket(GeckoLibUtil.getSyncedSingletonAnimatableId(this),
                            getAnimatableId(pos), animation),
                    serverPlayer);
        } else {
            MagicOfTheMind.LOGGER.error("Could not stop memory animation for player {} ({}) at pos {}",
                    player.getName().getString(), player, pos.toShortString());
        }
    }

    // endregion

    @SuppressWarnings("unchecked")
    public static AnimationController<GeoAnimatable> makeAnimationController(Memory animatable, String animation,
                                                                             long animatableID) {
        return new StatelessAnimationController(animatable, animation)
                .setCustomInstructionKeyframeHandler(
                        (AnimationController.CustomKeyframeHandler<GeoAnimatable>) (AnimationController.CustomKeyframeHandler<?>) new MemoryKeyframeHandler(
                                animatableID));
    }

    @Override
    public void handleClientAnimationPlay(GeoAnimatable animatable, long animatableId, RawAnimation animation) {
        if (!(animatable instanceof Memory instance)) return;

        AnimatableManager<GeoAnimatable> animatableManager = instance.getAnimatableInstanceCache().getManagerForId(animatableId);
        if (animatableManager == null) {
            return;
        }

        String animKey = animation.getStageCount() == 1 ? animation.getAnimationStages().getFirst().animationName() :
                animation.toString();
        AnimationController<?> controller = animatableManager.getAnimationControllers()
                .computeIfAbsent(animKey, anim -> makeAnimationController(instance, anim, animatableId));

        if (controller instanceof StatelessAnimationController statelessController) {
            statelessController.setCurrentAnimation(animation);
        }
    }

    // region required method overrides

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this, true);

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public double getTick(Object object) {
        return RenderUtil.getCurrentTick();
    }

    // endregion
    // region Unsupported method handlers; do not use

    /**
     * @deprecated use {@link #playAnimation(String, Player, BlockPos)} instead.
     */
    @Deprecated
    @Override
    public void playAnimation(String animation, Entity relatedEntity, long instanceId) {
        StatelessGeoSingletonAnimatable.super.playAnimation(animation, relatedEntity, instanceId);
    }

    /**
     * @deprecated use {@link #playAnimation(RawAnimation, Player, BlockPos)} instead.
     */
    @Deprecated
    @Override
    public void playAnimation(RawAnimation animation, Entity relatedEntity, long instanceId) {
        StatelessGeoSingletonAnimatable.super.playAnimation(animation, relatedEntity, instanceId);
    }

    /**
     * @deprecated use {@link #playLoopingAnimation(String, Player, BlockPos)} instead.
     */
    @Deprecated
    @Override
    public void playLoopingAnimation(String animation, Entity relatedEntity, long instanceId) {
        StatelessGeoSingletonAnimatable.super.playLoopingAnimation(animation, relatedEntity, instanceId);
    }

    /**
     * @deprecated use {@link #playAndHoldAnimation(String, Player, BlockPos)} instead.
     */
    @Deprecated
    @Override
    public void playAndHoldAnimation(String animation, Entity relatedEntity, long instanceId) {
        StatelessGeoSingletonAnimatable.super.playAndHoldAnimation(animation, relatedEntity, instanceId);
    }

    /**
     * @deprecated use {@link #stopAnimation(String, Player, BlockPos)} instead.
     */
    @Deprecated
    @Override
    public void stopAnimation(String animation, Entity relatedEntity, long instanceId) {
        StatelessGeoSingletonAnimatable.super.stopAnimation(animation, relatedEntity, instanceId);
    }

    /**
     * @deprecated use {@link #stopAnimation(RawAnimation, Player, BlockPos)} instead.
     */
    @Deprecated
    @Override
    public void stopAnimation(RawAnimation animation, Entity relatedEntity, long instanceId) {
        StatelessGeoSingletonAnimatable.super.stopAnimation(animation, relatedEntity, instanceId);
    }

    // These methods aren't used for MemoryAnimation
    @Override
    public final void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {}

    // These methods aren't used for MemoryAnimation
    @Override
    public final @Nullable Object getRenderProvider() {
        return null;
    }

    // endregion
}

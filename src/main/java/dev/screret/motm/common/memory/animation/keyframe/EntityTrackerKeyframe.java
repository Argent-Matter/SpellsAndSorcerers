package dev.screret.motm.common.memory.animation.keyframe;

import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.api.memory.animation.MemoryKeyframe;

import software.bernie.geckolib.animation.keyframe.BoneAnimationQueue;
import software.bernie.geckolib.animation.keyframe.event.CustomInstructionKeyframeEvent;

import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

public record EntityTrackerKeyframe(String rootBoneName, String entityMarker) implements MemoryKeyframe {

    // spotless:off
    public static final Codec<EntityTrackerKeyframe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("root_bone_name").forGetter(EntityTrackerKeyframe::rootBoneName),
            Codec.STRING.fieldOf("entity_marker").forGetter(EntityTrackerKeyframe::entityMarker)
    ).apply(instance, EntityTrackerKeyframe::new));
    public static final StreamCodec<ByteBuf, EntityTrackerKeyframe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, EntityTrackerKeyframe::rootBoneName,
            ByteBufCodecs.STRING_UTF8, EntityTrackerKeyframe::entityMarker,
            EntityTrackerKeyframe::new
    );
    // spotless:on

    @Override
    public void handleEvent(CustomInstructionKeyframeEvent<Memory> event, long packedPos) {
        Memory memory = event.getAnimatable();
        BlockPos pos = BlockPos.of(packedPos);

        BoneAnimationQueue boneAnimation = event.getController().getBoneAnimationQueues().get(this.rootBoneName);
        // boneAnimation.bone();
    }
}

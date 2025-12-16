package dev.screret.motm.common.memory.animation.keyframe;

import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.api.memory.animation.MemoryKeyframe;
import dev.screret.motm.client.memory.ClientMemoryCache;
import dev.screret.motm.client.memory.MemoryRenderer;

import software.bernie.geckolib.animation.keyframe.event.CustomInstructionKeyframeEvent;

import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

import java.util.concurrent.CompletableFuture;

import org.jetbrains.annotations.NotNull;

public record RenderStructureKeyframe(ResourceLocation structureName) implements MemoryKeyframe {

    // spotless:off
    public static final Codec<RenderStructureKeyframe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("structure").forGetter(RenderStructureKeyframe::structureName)
    ).apply(instance, RenderStructureKeyframe::new));
    public static final StreamCodec<ByteBuf, RenderStructureKeyframe> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, RenderStructureKeyframe::structureName,
            RenderStructureKeyframe::new
    );
    // spotless:on

    @Override
    public void handleEvent(@NotNull CustomInstructionKeyframeEvent<Memory> event, long packedPos) {
        CompletableFuture<StructureTemplate> future = ClientMemoryCache.MEMORY_STRUCTURE_CACHE.getUnchecked(this.structureName);
        future.whenComplete((structure, error) -> {
            if (error != null) {
                Minecraft.getInstance().delayCrash(CrashReport.forThrowable(error, "Rendering memory structure"));
            }
            if (MemoryRenderer.INSTANCE.hasActiveMemory()) {
                MemoryRenderer.INSTANCE.loadStructure(this.structureName, structure);
            }
        });
    }
}

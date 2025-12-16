package dev.screret.motm.api.memory.animation;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.common.memory.animation.MemoryAnimationProcessor;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.ResourceLocation;

import lombok.Getter;

/**
 * A singleton {@link GeoModel} for {@link Memory Memories}
 * <p>
 * Using this class automatically gets the asset paths from the provided {@link Memory}.
 * </p>
 * For example, {@code ResourceLocation.fromNamespaceAndPath("modid", "city/disaster_5")}'s animation will be at
 * {@code "modid:animations/motm/memory/city/disaster_5.animation.json"}
 */
public class MemoryGeoModel extends GeoModel<Memory> {

    public static final ResourceLocation MEMORY_MODEL_NAME = MOTMUtil.id("geo/motm/memory/memory.geo.json");
    public static final ResourceLocation MEMORY_TEXTURE_NAME = MOTMUtil.id("textures/block/empty.png");

    public static final MemoryGeoModel INSTANCE = new MemoryGeoModel();

    @Getter
    private final MemoryAnimationProcessor animationProcessor = new MemoryAnimationProcessor(this);

    @Override
    public ResourceLocation getModelResource(Memory animatable) {
        return MEMORY_MODEL_NAME;
    }

    @Override
    public ResourceLocation getTextureResource(Memory animatable) {
        return MEMORY_TEXTURE_NAME;
    }

    @Override
    public ResourceLocation getAnimationResource(Memory animatable) {
        return Memory.ANIMATION_ID_CONVERTER.idToFile(animatable.getAnimationName());
    }
}

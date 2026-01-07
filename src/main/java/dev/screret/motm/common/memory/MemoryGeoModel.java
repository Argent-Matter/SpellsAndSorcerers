package dev.screret.motm.common.memory;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.api.memory.Memory;
import dev.screret.motm.common.memory.animation.MemoryAnimationProcessor;

import software.bernie.geckolib.model.GeoModel;

import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;

import lombok.Getter;

/**
 * A singleton {@link GeoModel} for {@link Memory Memories}
 * <p>
 * Using this class automatically gets the asset paths from the provided {@link Memory}.
 * </p>
 * For example, {@code ResourceLocation.fromNamespaceAndPath("modid", "city/disaster_5")}'s animation will be at
 * {@code "modid:animations/motm/memory/city/disaster_5.animation.json"}.
 *
 * @apiNote The animation's name in the file <strong>must</strong> be {@code "memory"}.
 *          If it isn't, the animation will not load.
 */
public class MemoryGeoModel extends GeoModel<Memory> {

    public static final MemoryGeoModel INSTANCE = new MemoryGeoModel();

    public static final ResourceLocation MEMORY_TEXTURE_NAME = MOTMUtil.id("textures/block/empty.png");

    // spotless:off
    public static final FileToIdConverter ANIMATION_ID_CONVERTER = new FileToIdConverter("animations/" + MagicOfTheMind.MOD_ID + "/memory", ".animation.json");
    public static final FileToIdConverter MODEL_ID_CONVERTER = new FileToIdConverter("geo/" + MagicOfTheMind.MOD_ID + "/memory", ".geo.json");
    // spotless:on

    @Getter
    private final MemoryAnimationProcessor animationProcessor = new MemoryAnimationProcessor(this);

    @Override
    public ResourceLocation getModelResource(Memory animatable) {
        return MODEL_ID_CONVERTER.idToFile(animatable.getModelName());
    }

    @Override
    public ResourceLocation getTextureResource(Memory animatable) {
        return MEMORY_TEXTURE_NAME;
    }

    @Override
    public ResourceLocation getAnimationResource(Memory animatable) {
        return ANIMATION_ID_CONVERTER.idToFile(animatable.getAnimationName());
    }
}

package dev.screret.motm.api.model;

import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.model.DefaultedGeoModel;

import net.minecraft.resources.ResourceLocation;

/**
 * {@link DefaultedGeoModel} specific to {@link net.minecraft.world.item.ArmorItem ArmorItems}
 * <p>
 * Using this class pre-sorts provided asset paths into the "armor" subdirectory
 */
public class DefaultedGeoArmorModel<T extends GeoAnimatable> extends DefaultedGeoModel<T> {

    /**
     * Create a new instance of this model class
     * <p>
     * The asset path should be the truncated relative path from the base folder
     * <p>
     * E.G.
     * 
     * <pre>{@code
     * new ResourceLocation("myMod", "obsidian")
     * }</pre>
     */
    public DefaultedGeoArmorModel(ResourceLocation assetSubpath) {
        super(assetSubpath);
    }

    /**
     * Returns the subtype string for this type of model
     * <p>
     * This allows for sorting of asset files into neat subdirectories for clean management
     */
    @Override
    protected String subtype() {
        return "armor";
    }

    /**
     * Changes the constructor-defined model path for this model to an alternate
     * <p>
     * This is useful if your animatable shares a model path with another animatable that differs in path to the texture and
     * animations for this model
     */
    @Override
    public DefaultedGeoArmorModel<T> withAltModel(ResourceLocation altPath) {
        return (DefaultedGeoArmorModel<T>) super.withAltModel(altPath);
    }

    /**
     * Changes the constructor-defined animations path for this model to an alternate
     * <p>
     * This is useful if your animatable shares an animations path with another animatable that differs in path to the model and
     * texture for this model
     */
    @Override
    public DefaultedGeoArmorModel<T> withAltAnimations(ResourceLocation altPath) {
        return (DefaultedGeoArmorModel<T>) super.withAltAnimations(altPath);
    }

    /**
     * Changes the constructor-defined texture path for this model to an alternate
     * <p>
     * This is useful if your animatable shares a texture path with another animatable that differs in path to the model and
     * animations for this model
     */
    @Override
    public DefaultedGeoArmorModel<T> withAltTexture(ResourceLocation altPath) {
        return (DefaultedGeoArmorModel<T>) super.withAltTexture(altPath);
    }
}

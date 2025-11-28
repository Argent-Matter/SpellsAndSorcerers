package dev.screret.mitm.client.model.item;

import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.api.ability.WandAbilityInstance;
import dev.screret.mitm.common.item.component.WandComponent;
import dev.screret.mitm.data.MITMDataComponents;
import dev.screret.mitm.data.MITMWandAbilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WandAbilityOverrideHandler extends ItemOverrides {

    protected final WandModel model;
    protected final ModelBaker baker;
    protected final IGeometryBakingContext owner;
    protected final Function<Material, TextureAtlasSprite> spriteGetter;
    protected final ModelState modelTransform;
    private final Cache<ResourceLocation, BakedModel> bakedModelCache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .softValues()
            .build();

    public WandAbilityOverrideHandler(WandModel model, IGeometryBakingContext owner, ModelBaker baker,
                                      Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform) {
        this.model = model;
        this.owner = owner;
        this.baker = baker;
        this.spriteGetter = spriteGetter;
        this.modelTransform = modelTransform;
    }

    @NotNull
    @Override
    public BakedModel resolve(@NotNull BakedModel originalModel, @NotNull ItemStack stack, @Nullable ClientLevel level,
                              @Nullable LivingEntity entity, int seed) {
        BakedModel output = originalModel;

        ResourceLocation key = getCacheKey(stack);
        try {
            output = bakedModelCache.get(key, () -> getBakedModel(originalModel, stack, level, entity, key));
        } catch (ExecutionException e) {
            MagicOfTheMind.LOGGER.error("Error baking model!");
        }
        return output;
    }

    private BakedModel getBakedModel(BakedModel originalModel, ItemStack stack, @Nullable Level world,
                                     @Nullable LivingEntity entity, ResourceLocation key) {
        return this.model.bake(Minecraft.getInstance()
                .getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)
                .getSprite(key.withPrefix("item/wand/")), this.owner, this.baker, this.spriteGetter, this.modelTransform, this);
    }

    private ResourceLocation getCacheKey(ItemStack stack) {
        WandComponent component = stack.get(MITMDataComponents.WAND);
        if (component != null) {
            WandAbilityInstance current = component.primary();
            while (!current.getChildren().isEmpty()) {
                current = current.getChildren().getFirst();
            }
            return current.getId();
        }
        return MITMWandAbilities.DUMMY.getId();
    }
}

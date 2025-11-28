package dev.screret.mitm.client.model.item;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
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
import dev.screret.mitm.MagicOfTheMind;
import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.api.capability.ability.CapabilityWandAbility;
import dev.screret.mitm.api.ability.WandAbilityInstance;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

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

    public WandAbilityOverrideHandler(WandModel model, IGeometryBakingContext owner, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform) {
        this.model = model;
        this.owner = owner;
        this.baker = baker;
        this.spriteGetter = spriteGetter;
        this.modelTransform = modelTransform;
    }

    @NotNull
    @Override
    public BakedModel resolve(@NotNull BakedModel originalModel, @NotNull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        BakedModel output = originalModel;

        ResourceLocation key = getCacheKey(stack);
        try {
            output = bakedModelCache.get(key, () -> getBakedModel(originalModel, stack, level, entity, key));
        } catch (ExecutionException e) {
            MagicOfTheMind.LOGGER.error("Error baking model!");
        }
        return output;
    }

    protected BakedModel getBakedModel(BakedModel originalModel, ItemStack stack, @Nullable Level world, @Nullable LivingEntity entity, ResourceLocation key) {
        return this.model.bake(Minecraft.getInstance()
                .getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS)
                .getSprite(key.withPrefix("item/wand/")), this.owner, this.baker, this.spriteGetter, this.modelTransform, this);
    }

    ResourceLocation getCacheKey(ItemStack stack) {
        CapabilityWandAbility ability = stack.getCapability(CapabilityWandAbility.WAND_ABILITY);
        if (ability != null) {
            WandAbilityInstance current = ability.getMainAbility();
            while (current.getChildren() != null && current.getChildren().size() > 0) {
                current = ability.getMainAbility().getChildren().get(0);
            }
            return current.getId();
        }
        return MITMUtil.id("dummy");
    }
}
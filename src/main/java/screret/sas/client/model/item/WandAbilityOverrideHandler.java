package screret.sas.client.model.item;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import screret.sas.SpellsAndSorcerers;
import screret.sas.Util;
import screret.sas.api.capability.ability.WandAbilityProvider;
import screret.sas.api.wand.ability.WandAbilityInstance;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

public class WandAbilityOverrideHandler extends ItemOverrides {

    protected final WandModel model;
    protected final ModelBakery bakery;
    protected final IGeometryBakingContext owner;
    protected final Function<Material, TextureAtlasSprite> spriteGetter;
    protected final ModelState modelTransform;
    protected final ResourceLocation modelLocation;
    private final Cache<ResourceLocation, BakedModel> bakedModelCache = CacheBuilder.newBuilder().maximumSize(1000).build();

    public WandAbilityOverrideHandler(WandModel model, IGeometryBakingContext owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
        super();
        this.model = model;
        this.owner = owner;
        this.bakery = bakery;
        this.spriteGetter = spriteGetter;
        this.modelLocation = modelLocation;
        this.modelTransform = modelTransform;
    }

    @Nonnull
    @Override
    public BakedModel resolve(@Nonnull BakedModel originalModel, @Nonnull ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int pSeed) {
        BakedModel output = originalModel;

        ResourceLocation key = getCacheKey(stack);
        try {
            output = bakedModelCache.get(key, () -> getBakedModel(originalModel, stack, level, entity, key));
        } catch (ExecutionException e) {
            SpellsAndSorcerers.LOGGER.error("Error baking model!");
        }

        return output;
    }

    protected BakedModel getBakedModel(BakedModel originalModel, ItemStack stack, @Nullable Level world, @Nullable LivingEntity entity, ResourceLocation key) {
        return this.model.bake(Minecraft.getInstance().getModelManager().getAtlas(InventoryMenu.BLOCK_ATLAS).getSprite(new ResourceLocation(key.getNamespace(), "item/wand/" + key.getPath())), this.owner, this.bakery, this.spriteGetter, this.modelTransform, this, this.modelLocation);
    }

    ResourceLocation getCacheKey(ItemStack stack) {
        return stack.getCapability(WandAbilityProvider.WAND_ABILITY).map(cap -> {
            WandAbilityInstance current = cap.getAbility();
            while (current.getChildren() != null && current.getChildren().size() > 0) {
                current = cap.getAbility().getChildren().get(0);
            }
            return current.getId();
        }).orElse(Util.id("dummy"));
    }
}
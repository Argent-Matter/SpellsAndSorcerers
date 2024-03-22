package screret.sas.client.model.item;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.NeoForgeRenderTypes;
import net.neoforged.neoforge.client.RenderTypeGroup;
import net.neoforged.neoforge.client.model.CompositeModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.geometry.UnbakedGeometryHelper;
import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class WandModel implements IUnbakedGeometry<WandModel> {

    private static final RenderTypeGroup RENDER_TYPE_GROUP = new RenderTypeGroup(RenderType.translucent(), NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());

    @Override
    public BakedModel bake(IGeometryBakingContext owner, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        return CompositeModel.Baked.builder(owner, spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, modelLocation)), new WandAbilityOverrideHandler(this, owner, bakery, spriteGetter, modelTransform, modelLocation), owner.getTransforms()).addQuads(new RenderTypeGroup(RenderType.translucent(), NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get())).build();
    }

    @Override
    public Collection<Material> getMaterials(IGeometryBakingContext context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
        return Collections.emptySet();
    }

    public BakedModel bake(TextureAtlasSprite sprite, IGeometryBakingContext context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, sprite);
        var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> sprite, modelState, modelLocation);

        var builder = CompositeModel.Baked.builder(context, sprite, new WandAbilityOverrideHandler(this, context, bakery, spriteGetter, modelState, modelLocation), context.getTransforms());


        builder.addQuads(RENDER_TYPE_GROUP, quads);

        return builder.build();
        //return new SimpleBakedModel(builder.build(), new HashMap<>(), false, false, false, particleSprite, owner.getTransforms(), new WandAbilityOverrideHandler(this, owner, bakery, spriteGetter, modelTransform, modelLocation), new RenderTypeGroup(RenderType.translucent(), ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get()));
    }

    public static final class Loader implements IGeometryLoader<WandModel>
    {
        public static final Loader INSTANCE = new Loader();

        @Nonnull
        @Override
        public WandModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            return new WandModel();
        }
    }
}

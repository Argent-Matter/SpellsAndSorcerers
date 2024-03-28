package screret.sas.client.model.item;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
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
import java.util.function.Function;

public class WandModel implements IUnbakedGeometry<WandModel> {

    private static final RenderTypeGroup RENDER_TYPE_GROUP = new RenderTypeGroup(RenderType.translucent(), NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        return CompositeModel.Baked.builder(context, spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, modelLocation)), new WandAbilityOverrideHandler(this, context, baker, spriteGetter, modelState, modelLocation), context.getTransforms()).addQuads(new RenderTypeGroup(RenderType.translucent(), NeoForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get())).build();
    }

    public BakedModel bake(TextureAtlasSprite sprite, IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, sprite);
        var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> sprite, modelState, modelLocation);

        var builder = CompositeModel.Baked.builder(context, sprite, new WandAbilityOverrideHandler(this, context, baker, spriteGetter, modelState, modelLocation), context.getTransforms());


        builder.addQuads(RENDER_TYPE_GROUP, quads);

        return builder.build();
    }

    public static final class Loader implements IGeometryLoader<WandModel> {
        public static final Loader INSTANCE = new Loader();

        @Nonnull
        @Override
        public WandModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            return new WandModel();
        }
    }
}

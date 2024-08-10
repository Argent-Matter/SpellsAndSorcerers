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
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;

public class WandModel implements IUnbakedGeometry<WandModel> {

    private static final RenderTypeGroup RENDER_TYPE_GROUP = new RenderTypeGroup(RenderType.translucent(), ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());

    @Override
    public BakedModel bake(IGeometryBakingContext owner, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        return CompositeModel.Baked.builder(owner, spriteGetter.apply(new Material(InventoryMenu.BLOCK_ATLAS, modelLocation)), new WandAbilityOverrideHandler(this, owner, baker, spriteGetter, modelTransform, modelLocation), owner.getTransforms()).addQuads(new RenderTypeGroup(RenderType.translucent(), ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get())).build();
    }

    public BakedModel bake(TextureAtlasSprite sprite, IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        var unbaked = UnbakedGeometryHelper.createUnbakedItemElements(0, sprite.contents());
        var quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> sprite, modelState, modelLocation);

        var builder = CompositeModel.Baked.builder(context, sprite, new WandAbilityOverrideHandler(this, context, baker, spriteGetter, modelState, modelLocation), context.getTransforms());


        builder.addQuads(RENDER_TYPE_GROUP, quads);

        return builder.build();
    }

    public static final class Loader implements IGeometryLoader<WandModel>
    {
        public static final WandModel.Loader INSTANCE = new WandModel.Loader();

        @Nonnull
        @Override
        public WandModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            return new WandModel();
        }
    }
}

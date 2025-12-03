package dev.screret.modularui.drawable;

import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

import java.util.function.Function;

public class ModularUIRenderTypes extends RenderType {

    private static final Function<ResourceLocation, RenderType> GUI_TEXTURE = Util.memoize((texture) -> {
        return create("gui_texture", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS,
                RenderType.TRANSIENT_BUFFER_SIZE, false, true,
                RenderType.CompositeState.builder()
                        .setShaderState(RENDERTYPE_TEXT_SHADER)
                        .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                        .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                        .setLightmapState(LIGHTMAP)
                        .createCompositeState(false));
    });

    private static final RenderType GUI_TRIANGLE_FAN = RenderType.create("gui_triangle_fan",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_FAN, 256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_GUI_SHADER)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST)
                    .createCompositeState(false));

    private static final RenderType GUI_OVERLAY_TRIANGLE_FAN = RenderType.create("gui_overlay_triangle_fan",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLE_FAN, 256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.RENDERTYPE_GUI_OVERLAY_SHADER)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE)
                    .createCompositeState(false));

    public static RenderType guiTexture(ResourceLocation texture) {
        return GUI_TEXTURE.apply(texture);
    }

    public static RenderType guiTriangleFan() {
        return GUI_TRIANGLE_FAN;
    }

    public static RenderType guiOverlayTriangleFan() {
        return GUI_OVERLAY_TRIANGLE_FAN;
    }

    private ModularUIRenderTypes() {
        super("", VertexFormat.builder().build(), VertexFormat.Mode.QUADS, 0, false, false, () -> {}, () -> {});
        throw new IllegalStateException("Do not instantiate MuiRenderTypes directly!");
    }
}

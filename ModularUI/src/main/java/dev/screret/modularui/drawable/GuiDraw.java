package dev.screret.modularui.drawable;

import dev.screret.modularui.client.screen.RichTooltip;
import dev.screret.modularui.client.screen.event.RichTooltipEvent;
import dev.screret.modularui.client.screen.viewport.GuiContext;
import dev.screret.modularui.client.screen.viewport.ModularGuiContext;
import dev.screret.modularui.drawable.text.TextRenderer;
import dev.screret.modularui.utils.Alignment;
import dev.screret.modularui.utils.Color;
import dev.screret.modularui.utils.FormattingUtil;
import dev.screret.modularui.widget.sizer.Area;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import org.joml.*;

import java.lang.Math;
import java.util.List;

import org.jetbrains.annotations.Nullable;

import static net.minecraft.util.Mth.HALF_PI;
import static net.minecraft.util.Mth.TWO_PI;

public class GuiDraw {

    private static final TextRenderer textRenderer = new TextRenderer();

    public static void drawRect(GuiGraphics graphics, float x0, float y0, float w, float h, int color) {
        Matrix4f pose = graphics.pose().last().pose();
        VertexConsumer builder = graphics.bufferSource().getBuffer(RenderType.guiOverlay());
        drawRectRaw(builder, pose, x0, y0, x0 + w, y0 + h, color);
    }

    public static void drawHorizontalGradientRect(GuiGraphics graphics, float x0, float y0, float w, float h,
                                                  int colorLeft, int colorRight) {
        drawRect(graphics, x0, y0, w, h, colorLeft, colorRight, colorLeft, colorRight);
    }

    public static void drawVerticalGradientRect(GuiGraphics graphics, float x0, float y0, float w, float h,
                                                int colorTop, int colorBottom) {
        drawRect(graphics, x0, y0, w, h, colorTop, colorTop, colorBottom, colorBottom);
    }

    public static void drawRect(GuiGraphics graphics, float x0, float y0, float w, float h,
                                int colorTL, int colorTR, int colorBL, int colorBR) {
        Matrix4f pose = graphics.pose().last().pose();
        VertexConsumer bufferbuilder = graphics.bufferSource().getBuffer(RenderType.guiOverlay());

        float x1 = x0 + w, y1 = y0 + h;
        bufferbuilder.addVertex(pose, x0, y0, 0.0f)
                .setColor(Color.getRed(colorTL), Color.getGreen(colorTL), Color.getBlue(colorTL), Color.getAlpha(colorTL));
        bufferbuilder.addVertex(pose, x0, y1, 0.0f)
                .setColor(Color.getRed(colorBL), Color.getGreen(colorBL), Color.getBlue(colorBL), Color.getAlpha(colorBL));
        bufferbuilder.addVertex(pose, x1, y1, 0.0f)
                .setColor(Color.getRed(colorBR), Color.getGreen(colorBR), Color.getBlue(colorBR), Color.getAlpha(colorBR));
        bufferbuilder.addVertex(pose, x1, y0, 0.0f)
                .setColor(Color.getRed(colorTR), Color.getGreen(colorTR), Color.getBlue(colorTR), Color.getAlpha(colorTR));
    }

    public static void drawRectRaw(VertexConsumer buffer, Matrix4f pose, float x0, float y0, float x1, float y1,
                                   int color) {
        int r = Color.getRed(color);
        int g = Color.getGreen(color);
        int b = Color.getBlue(color);
        int a = Color.getAlpha(color);
        drawRectRaw(buffer, pose, x0, y0, x1, y1, r, g, b, a);
    }

    public static void drawRectRaw(VertexConsumer buffer, Matrix4f pose, float x0, float y0, float x1, float y1,
                                   int r, int g, int b, int a) {
        buffer.addVertex(pose, x0, y0, 0.0f).setColor(r, g, b, a);
        buffer.addVertex(pose, x0, y1, 0.0f).setColor(r, g, b, a);
        buffer.addVertex(pose, x1, y1, 0.0f).setColor(r, g, b, a);
        buffer.addVertex(pose, x1, y0, 0.0f).setColor(r, g, b, a);
    }

    public static void drawCircle(GuiGraphics graphics, float x0, float y0, float diameter, int color, int segments) {
        drawEllipse(graphics, x0, y0, diameter, diameter, color, color, segments);
    }

    public static void drawCircle(GuiGraphics graphics, float x0, float y0, float diameter,
                                  int centerColor, int outerColor, int segments) {
        drawEllipse(graphics, x0, y0, diameter, diameter, centerColor, outerColor, segments);
    }

    public static void drawEllipse(GuiGraphics graphics, float x0, float y0, float w, float h,
                                   int color, int segments) {
        drawEllipse(graphics, x0, y0, w, h, color, color, segments);
    }

    public static void drawEllipse(GuiGraphics graphics, float x0, float y0, float w, float h,
                                   int centerColor, int outerColor, int segments) {
        Matrix4f pose = graphics.pose().last().pose();
        VertexConsumer bufferbuilder = graphics.bufferSource().getBuffer(ModularUIRenderTypes.guiOverlayTriangleFan());

        float x_2 = x0 + w / 2f, y_2 = y0 + h / 2f;
        // start at center
        bufferbuilder.addVertex(pose, x_2, y_2, 0.0f)
                .setColor(Color.getRed(centerColor), Color.getGreen(centerColor), Color.getBlue(centerColor),
                        Color.getAlpha(centerColor));
        int a = Color.getAlpha(outerColor), r = Color.getRed(outerColor), g = Color.getGreen(outerColor),
                b = Color.getBlue(outerColor);
        float incr = (float) (TWO_PI / segments);
        for (int i = 0; i <= segments; i++) {
            float angle = incr * i;
            float x = (float) (Math.sin(angle) * (w / 2) + x_2);
            float y = (float) (Math.cos(angle) * (h / 2) + y_2);
            bufferbuilder.addVertex(x, y, 0.0f).setColor(r, g, b, a);
        }
        RenderSystem.disableBlend();
    }

    public static void drawRoundedRect(GuiGraphics graphics, float x0, float y0, float w, float h, int color,
                                       int cornerRadius, int segments) {
        drawRoundedRect(graphics, x0, y0, w, h, color, color, color, color, cornerRadius, segments);
    }

    public static void drawVerticalGradientRoundedRect(GuiGraphics graphics, float x0, float y0, float w, float h,
                                                       int colorTop, int colorBottom, int cornerRadius, int segments) {
        drawRoundedRect(graphics, x0, y0, w, h, colorTop, colorTop, colorBottom, colorBottom, cornerRadius, segments);
    }

    public static void drawHorizontalGradientRoundedRect(GuiGraphics graphics, float x0, float y0, float w, float h,
                                                         int colorLeft, int colorRight, int cornerRadius, int segments) {
        drawRoundedRect(graphics, x0, y0, w, h, colorLeft, colorRight, colorLeft, colorRight, cornerRadius, segments);
    }

    public static void drawRoundedRect(GuiGraphics graphics, float x0, float y0, float w, float h,
                                       int colorTL, int colorTR, int colorBL, int colorBR,
                                       int cornerRadius, int segments) {
        Matrix4f pose = graphics.pose().last().pose();
        VertexConsumer buffer = graphics.bufferSource().getBuffer(ModularUIRenderTypes.guiOverlayTriangleFan());

        float x1 = x0 + w, y1 = y0 + h;
        int color = Color.average(colorBL, colorBR, colorTR, colorTL);
        // start at center
        buffer.addVertex(pose, x0 + w / 2f, y0 + h / 2f, 0.0f).setColor(color);
        // left side
        buffer.addVertex(pose, x0, y0 + cornerRadius, 0.0f).setColor(colorTL);
        buffer.addVertex(pose, x0, y1 - cornerRadius, 0.0f)
                .setColor(colorBL);
        // bottom left corner
        for (int i = 1; i <= segments; i++) {
            float x = (float) (x0 + cornerRadius - Math.cos(HALF_PI / segments * i) * cornerRadius);
            float y = (float) (y1 - cornerRadius + Math.sin(HALF_PI / segments * i) * cornerRadius);
            buffer.addVertex(x, y, 0.0f).setColor(colorBL);
        }
        // bottom side
        buffer.addVertex(pose, x1 - cornerRadius, y1, 0.0f).setColor(colorBR);
        // bottom right corner
        for (int i = 1; i <= segments; i++) {
            float x = (float) (x1 - cornerRadius + Math.sin(HALF_PI / segments * i) * cornerRadius);
            float y = (float) (y1 - cornerRadius + Math.cos(HALF_PI / segments * i) * cornerRadius);
            buffer.addVertex(pose, x, y, 0.0f).setColor(colorBR);
        }
        // right side
        buffer.addVertex(pose, x1, y0 + cornerRadius, 0.0f).setColor(colorTR);
        // top right corner
        for (int i = 1; i <= segments; i++) {
            float x = (float) (x1 - cornerRadius + Math.cos(HALF_PI / segments * i) * cornerRadius);
            float y = (float) (y0 + cornerRadius - Math.sin(HALF_PI / segments * i) * cornerRadius);
            buffer.addVertex(pose, x, y, 0.0f).setColor(colorTR);
        }
        // top side
        buffer.addVertex(pose, x0 + cornerRadius, y0, 0.0f).setColor(colorTL);
        // top left corner
        for (int i = 1; i <= segments; i++) {
            float x = (float) (x0 + cornerRadius - Math.sin(HALF_PI / segments * i) * cornerRadius);
            float y = (float) (y0 + cornerRadius - Math.cos(HALF_PI / segments * i) * cornerRadius);
            buffer.addVertex(pose, x, y, 0.0f).setColor(colorTL);
        }
        buffer.addVertex(pose, x0, y0 + cornerRadius, 0.0f).setColor(colorTL);
    }

    public static void drawTexture(Matrix4f pose, ResourceLocation location, float x, float y, float w, float h,
                                   int u, int v, int textureWidth, int textureHeight) {
        RenderSystem.setShaderTexture(0, location);
        drawTexture(pose, x, y, u, v, w, h, textureWidth, textureHeight);
    }

    public static void drawTexture(Matrix4f pose, float x, float y, int u, int v, float w, float h,
                                   int textureW, int textureH) {
        drawTexture(pose, x, y, u, v, w, h, textureW, textureH, 0);
    }

    /**
     * Draw a textured quad with given UV, dimensions and custom texture size
     */
    public static void drawTexture(Matrix4f pose, float x, float y, int u, int v, float w, float h,
                                   int textureW, int textureH, float z) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        drawTexture(pose, buffer, x, y, u, v, w, h, textureW, textureH, z);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    public static void drawTexture(Matrix4f pose, VertexConsumer buffer, float x, float y, int u, int v,
                                   float w, float h, int textureW, int textureH, float z) {
        float tw = 1F / textureW;
        float th = 1F / textureH;

        buffer.addVertex(pose, x, y + h, z).setUv(u * tw, (v + h) * th);
        buffer.addVertex(pose, x + w, y + h, z).setUv((u + w) * tw, (v + h) * th);
        buffer.addVertex(pose, x + w, y, z).setUv((u + w) * tw, v * th);
        buffer.addVertex(pose, x, y, z).setUv(u * tw, v * th);
    }

    public static void drawTexture(Matrix4f pose, float x, float y, int u, int v, float w, float h,
                                   int textureW, int textureH, int tu, int tv) {
        drawTexture(pose, x, y, u, v, w, h, textureW, textureH, tu, tv, 0);
    }

    /**
     * Draw a textured quad with given UV, dimensions and custom texture size
     */
    public static void drawTexture(Matrix4f pose, float x, float y, int u, int v, float w, float h,
                                   int textureW, int textureH, int tu, int tv, float z) {
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        drawTexture(pose, buffer, x, y, u, v, w, h, textureW, textureH, tu, tv, z);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    public static void drawTexture(Matrix4f pose, VertexConsumer buffer, float x, float y, int u, int v,
                                   float w, float h, int textureW, int textureH, int tu, int tv, float z) {
        float tw = 1F / textureW;
        float th = 1F / textureH;

        buffer.addVertex(pose, x, y + h, z).setUv(u * tw, tv * th);
        buffer.addVertex(pose, x + w, y + h, z).setUv(tu * tw, tv * th);
        buffer.addVertex(pose, x + w, y, z).setUv(tu * tw, v * th);
        buffer.addVertex(pose, x, y, z).setUv(u * tw, v * th);
    }

    public static void drawTexture(Matrix4f pose, ResourceLocation location, float x0, float y0, float x1, float y1,
                                   float u0, float v0, float u1, float v1) {
        drawTexture(pose, location, x0, y0, x1, y1, u0, v0, u1, v1, false);
    }

    public static void drawTexture(Matrix4f pose, ResourceLocation location, float x0, float y0, float x1, float y1,
                                   float u0, float v0, float u1, float v1, boolean withBlend) {
        RenderSystem.setShaderTexture(0, location);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        if (withBlend) {
            RenderSystem.enableBlend();
        } else {
            RenderSystem.disableBlend();
        }
        drawTexture(pose, x0, y0, x1, y1, u0, v0, u1, v1, 0);
    }

    public static void drawTexture(Matrix4f pose, float x0, float y0, float x1, float y1,
                                   float u0, float v0, float u1, float v1) {
        drawTexture(pose, x0, y0, x1, y1, u0, v0, u1, v1, 0);
    }

    public static void drawTexture(Matrix4f pose, float x0, float y0, float x1, float y1,
                                   float u0, float v0, float u1, float v1, float z) {
        RenderSystem.disableDepthTest();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        drawTexture(pose, buffer, x0, y0, x1, y1, u0, v0, u1, v1, z);
        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    public static void drawTexture(Matrix4f pose, VertexConsumer buffer, float x0, float y0, float x1, float y1,
                                   float u0, float v0, float u1, float v1, float z) {
        buffer.addVertex(pose, x0, y1, z).setUv(u0, v1);
        buffer.addVertex(pose, x1, y1, z).setUv(u1, v1);
        buffer.addVertex(pose, x1, y0, z).setUv(u1, v0);
        buffer.addVertex(pose, x0, y0, z).setUv(u0, v0);
    }

    public static void drawTiledTexture(Matrix4f pose, ResourceLocation location, float x, float y, float w, float h,
                                        int u, int v, int tileW, int tileH, int tw, int th, float z) {
        RenderSystem.setShaderTexture(0, location);
        drawTiledTexture(pose, x, y, w, h, u, v, tileW, tileH, tw, th, z);
    }

    public static void drawTiledTexture(Matrix4f pose, float x, float y, float w, float h, int u, int v,
                                        int tileW, int tileH, int tw, int th, float z) {
        int countX = (((int) w - 1) / tileW) + 1;
        int countY = (((int) h - 1) / tileH) + 1;
        float fillerX = w - (countX - 1) * tileW;
        float fillerY = h - (countY - 1) * tileH;

        RenderSystem.disableDepthTest();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for (int i = 0, c = countX * countY; i < c; i++) {
            int ix = i % countX;
            int iy = i / countX;
            float xx = x + ix * tileW;
            float yy = y + iy * tileH;
            float xw = ix == countX - 1 ? fillerX : tileW;
            float yh = iy == countY - 1 ? fillerY : tileH;

            drawTexture(pose, buffer, xx, yy, u, v, xw, yh, tw, th, z);
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    public static void drawTiledTexture(Matrix4f pose, ResourceLocation location, float x, float y, float w, float h,
                                        float u0, float v0, float u1, float v1, int textureWidth, int textureHeight, float z) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, location);
        drawTiledTexture(pose, x, y, w, h, u0, v0, u1, v1, textureWidth, textureHeight, z);
        RenderSystem.disableBlend();
    }

    public static void drawTiledTexture(Matrix4f pose, float x, float y, float w, float h,
                                        float u0, float v0, float u1, float v1,
                                        int tileWidth, int tileHeight, float z) {
        int countX = (((int) w - 1) / tileWidth) + 1;
        int countY = (((int) h - 1) / tileHeight) + 1;
        float fillerX = w - (countX - 1) * tileWidth;
        float fillerY = h - (countY - 1) * tileHeight;
        float fillerU = u0 + (u1 - u0) * fillerX / tileWidth;
        float fillerV = v0 + (v1 - v0) * fillerY / tileHeight;

        RenderSystem.disableDepthTest();
        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        for (int i = 0, c = countX * countY; i < c; i++) {
            int ix = i % countX;
            int iy = i / countX;
            float xx = x + ix * tileWidth;
            float yy = y + iy * tileHeight;
            float xw = tileWidth, yh = tileHeight, uEnd = u1, vEnd = v1;
            if (ix == countX - 1) {
                xw = fillerX;
                uEnd = fillerU;
            }
            if (iy == countY - 1) {
                yh = fillerY;
                vEnd = fillerV;
            }

            drawTexture(pose, buffer, xx, yy, xx + xw, yy + yh, u0, v0, uEnd, vEnd, z);
        }

        BufferUploader.drawWithShader(buffer.buildOrThrow());
    }

    public static void drawLivingEntity(GuiGraphics graphics, LivingEntity entity,
                                        int x, int y, float width, float height, int z) {
        int scale = 132;
        Quaternionf pose = new Quaternionf(1.414f, 0.0f, 1.0f, 0.0f);
        graphics.pose().pushPose();
        graphics.pose().translate(x + width / 2, y + height, 50.0f);
        graphics.pose()
                .mulPose(new Matrix4f().scaling(width / 2, height / 2, -scale));
        graphics.pose().mulPose(pose);
        Lighting.setupForEntityInInventory();

        EntityRenderDispatcher entityRenderer = Minecraft.getInstance().getEntityRenderDispatcher();
        entityRenderer.setRenderShadow(false);

        RenderSystem.runAsFancy(() -> {
            entityRenderer.render(entity, 0.0d, 0.0d, 0.0d, 0.0f, 1.0f,
                    graphics.pose(), graphics.bufferSource(), LightTexture.FULL_BRIGHT);
        });
        graphics.flush();
        entityRenderer.setRenderShadow(true);
        graphics.pose().popPose();
        Lighting.setupFor3DItems();
    }

    public static void drawItem(GuiGraphics graphics, ItemStack item, int x, int y, float width, float height, int z) {
        if (item.isEmpty()) return;
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, z);
        graphics.pose().scale(width / 16f, height / 16f, 1);
        graphics.renderItem(item, 0, 0);
        graphics.renderItemDecorations(Minecraft.getInstance().font, item, 0, 0);
        graphics.pose().popPose();
    }

    public static void drawFluidTexture(GuiGraphics graphics, FluidStack content,
                                        float x0, float y0, float width, float height, float z) {
        if (content == null || content.isEmpty()) {
            return;
        }
        Fluid fluid = content.getFluid();
        ResourceLocation fluidStill = IClientFluidTypeExtensions.of(fluid).getStillTexture(content);
        TextureAtlasSprite sprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(fluidStill);
        int fluidColor = IClientFluidTypeExtensions.of(fluid).getTintColor(content);
        graphics.setColor(Color.getRedF(fluidColor), Color.getGreenF(fluidColor), Color.getBlueF(fluidColor),
                Color.getAlphaF(fluidColor));
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        drawTiledTexture(graphics.pose().last().pose(), InventoryMenu.BLOCK_ATLAS, x0, y0, width, height,
                sprite.getU0(), sprite.getV0(),
                sprite.getU1(), sprite.getV1(), sprite.contents().width(), sprite.contents().height(), z);
        graphics.setColor(1f, 1f, 1f, 1f);
    }

    public static void drawStandardSlotAmountText(ModularGuiContext context, int amount, String format, Area area,
                                                  float z) {
        drawAmountText(context, amount, format, 0, 0, area.width, area.height, Alignment.BottomRight, z);
    }

    public static void drawAmountText(ModularGuiContext context, int amount, String format, int x, int y, int width,
                                      int height, Alignment alignment, float z) {
        if (amount == 1) return;

        String amountText = FormattingUtil.formatNumberReadable(amount, false);
        if (format != null) {
            amountText = format + amountText;
        }
        drawScaledAlignedTextInBox(context, amountText, x, y, width, height, alignment, 1f, z);
    }

    public static void drawScaledAlignedTextInBox(ModularGuiContext context, String amountText, int x, int y, int width,
                                                  int height, Alignment alignment) {
        drawScaledAlignedTextInBox(context, amountText, x, y, width, height, alignment, 1f, 0.0f);
    }

    public static void drawScaledAlignedTextInBox(ModularGuiContext context, String amountText, int x, int y, int width,
                                                  int height,
                                                  Alignment alignment, float maxScale, float z) {
        if (amountText == null || amountText.isEmpty()) return;
        // render the amount overlay
        textRenderer.setShadow(true);
        textRenderer.setScale(1f);
        textRenderer.setColor(Color.WHITE.main);
        textRenderer.setAlignment(alignment, width, height);
        textRenderer.setPos(x, y);
        textRenderer.setHardWrapOnBorder(false);
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        if (amountText.length() > 2 && width > 16) { // we know that numbers below 100 will always fit in standard slots
            // simulate and calculate scale with width
            textRenderer.setSimulate(true);
            textRenderer.draw(context.getGraphics(), amountText);
            textRenderer.setSimulate(false);
            textRenderer.setScale(Math.min(maxScale, width / textRenderer.getLastWidth()));
        }
        context.graphicsPose().translate(0, 0, 100 + z);
        textRenderer.draw(context.getGraphics(), amountText);
        textRenderer.setHardWrapOnBorder(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
    }

    public static void drawSprite(Matrix4f pose, TextureAtlasSprite sprite, float x0, float y0, float w, float h) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        drawTexture(pose, x0, y0, x0 + w, y0 + h, sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1());
        RenderSystem.disableBlend();
    }

    public static void drawTiledSprite(Matrix4f pose, TextureAtlasSprite sprite, float x0, float y0, float w, float h) {
        RenderSystem.enableBlend();
        RenderSystem.setShaderTexture(0, sprite.atlasLocation());
        drawTiledTexture(pose, sprite.atlasLocation(), x0, y0, x0 + w, y0 + h, sprite.getU0(), sprite.getV0(),
                sprite.getU1(),
                sprite.getV1(), sprite.contents().width(), sprite.contents().height(), 0);
        RenderSystem.disableBlend();
    }

    public static void drawOutlineCenter(GuiGraphics graphics, int x, int y, int offset, int color) {
        drawOutlineCenter(graphics, x, y, offset, color, 1);
    }

    public static void drawOutlineCenter(GuiGraphics graphics, int x, int y, int offset, int color, int border) {
        drawOutline(graphics, x - offset, y - offset, x + offset, y + offset, color, border);
    }

    public static void drawOutline(GuiGraphics graphics, int left, int top, int right, int bottom, int color) {
        drawOutline(graphics, left, top, right, bottom, color, 1);
    }

    /**
     * Draw rectangle outline with given border
     */
    public static void drawOutline(GuiGraphics graphics, int left, int top, int right, int bottom, int color,
                                   int border) {
        graphics.fill(left, top, left + border, bottom, color);
        graphics.fill(right - border, top, right, bottom, color);
        graphics.fill(left + border, top, right - border, top + border, color);
        graphics.fill(left + border, bottom - border, right - border, bottom, color);
    }

    private static void drawBorderLTRB(GuiGraphics graphics, float left, float top, float right, float bottom,
                                       float border, int color, boolean outside) {
        if (outside) {
            left -= border;
            top -= border;
            right += border;
            bottom += border;
        }
        float x0 = left, y0 = top, x1 = right, y1 = bottom, d = border;

        var buffer = graphics.bufferSource().getBuffer(ModularUIRenderTypes.guiTriangleStrip());
        var pose = graphics.pose().last().pose();
        pc(buffer, pose, x0, y0, color);
        pc(buffer, pose, x1 - d, y0 + d, color);
        pc(buffer, pose, x1, y0, color);
        pc(buffer, pose, x1 - d, y1 - d, color);
        pc(buffer, pose, x1, y1, color);
        pc(buffer, pose, x0 + d, y1 - d, color);
        pc(buffer, pose, x0, y1, color);
        pc(buffer, pose, x0 + d, y0 + d, color);
        pc(buffer, pose, x0, y0, color);
        pc(buffer, pose, x1 - d, y0 + d, color);
    }

    public static void drawBorderOutsideLTRB(GuiGraphics graphics, float left, float top, float right, float bottom,
                                             int color) {
        drawBorderLTRB(graphics, left, top, right, bottom, 1, color, true);
    }

    public static void drawBorderOutsideLTRB(GuiGraphics graphics, float left, float top, float right, float bottom,
                                             float border, int color) {
        drawBorderLTRB(graphics, left, top, right, bottom, border, color, true);
    }

    public static void drawBorderInsideLTRB(GuiGraphics graphics, float left, float top, float right, float bottom,
                                            int color) {
        drawBorderLTRB(graphics, left, top, right, bottom, 1, color, false);
    }

    public static void drawBorderInsideLTRB(GuiGraphics graphics, float left, float top, float right, float bottom,
                                            float border, int color) {
        drawBorderLTRB(graphics, left, top, right, bottom, border, color, false);
    }

    private static void drawBorderXYWH(GuiGraphics graphics, float x, float y, float w, float h, float border,
                                       int color, boolean outside) {
        drawBorderLTRB(graphics, x, y, x + w, y + h, border, color, outside);
    }

    public static void drawBorderOutsideXYWH(GuiGraphics graphics, float x, float y, float w, float h, float border,
                                             int color) {
        drawBorderXYWH(graphics, x, y, w, h, border, color, true);
    }

    public static void drawBorderOutsideXYWH(GuiGraphics graphics, float x, float y, float w, float h, int color) {
        drawBorderXYWH(graphics, x, y, w, h, 1, color, true);
    }

    public static void drawBorderInsideXYWH(GuiGraphics graphics, float x, float y, float w, float h, float border,
                                            int color) {
        drawBorderXYWH(graphics, x, y, w, h, border, color, false);
    }

    public static void drawBorderInsideXYWH(GuiGraphics graphics, float x, float y, float w, float h, int color) {
        drawBorderXYWH(graphics, x, y, w, h, 1, color, false);
    }

    private static void pc(VertexConsumer buffer, Matrix4f pose, float x, float y, int c) {
        buffer.addVertex(pose, x, y, 0).setColor(c);
    }

    /**
     * Draws a rectangular shadow
     *
     * @param x      left of solid shadow part
     * @param y      top of solid shadow part
     * @param w      width of solid shadow part
     * @param h      height of solid shadow part
     * @param oX     shadow gradient size in x
     * @param oY     shadow gradient size in y
     * @param opaque solid shadow color
     * @param shadow gradient end color
     */
    public static void drawDropShadow(Matrix4f pose, int x, int y, int w, int h, int oX, int oY,
                                      int opaque, int shadow) {
        float a1 = Color.getAlphaF(opaque);
        float r1 = Color.getRedF(opaque);
        float g1 = Color.getGreenF(opaque);
        float b1 = Color.getBlueF(opaque);
        float a2 = Color.getAlphaF(shadow);
        float r2 = Color.getRedF(shadow);
        float g2 = Color.getGreenF(shadow);
        float b2 = Color.getBlueF(shadow);

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        float x1 = x + w, y1 = y + h;

        /* Draw opaque part */
        buffer.addVertex(pose, x1, y, 0).setColor(r1, g1, b1, a1);
        buffer.addVertex(pose, x, y, 0).setColor(r1, g1, b1, a1);
        buffer.addVertex(pose, x, y1, 0).setColor(r1, g1, b1, a1);
        buffer.addVertex(pose, x1, y1, 0).setColor(r1, g1, b1, a1);

        /* Draw top shadow */
        buffer.addVertex(pose, x1 + oX, y - oY, 0).setColor(r2, g2, b2, a2);
        buffer.addVertex(pose, x - oX, y - oY, 0).setColor(r2, g2, b2, a2);
        buffer.addVertex(pose, x, y, 0).setColor(r1, g1, b1, a1);
        buffer.addVertex(pose, x1, y, 0).setColor(r1, g1, b1, a1);

        /* Draw bottom shadow */
        buffer.addVertex(pose, x1, y1, 0).setColor(r1, g1, b1, a1);
        buffer.addVertex(pose, x, y1, 0).setColor(r1, g1, b1, a1);
        buffer.addVertex(pose, x - oX, y1 + oY, 0).setColor(r2, g2, b2, a2);
        buffer.addVertex(pose, x1 + oX, y1 + oY, 0).setColor(r2, g2, b2, a2);

        /* Draw left shadow */
        buffer.addVertex(pose, x, y, 0).setColor(r1, g1, b1, a1);
        buffer.addVertex(pose, x - oX, y - oY, 0).setColor(r2, g2, b2, a2);
        buffer.addVertex(pose, x - oX, y1 + oY, 0).setColor(r2, g2, b2, a2);
        buffer.addVertex(pose, x, y1, 0).setColor(r1, g1, b1, a1);

        /* Draw right shadow */
        buffer.addVertex(pose, x1 + oX, y - oY, 0).setColor(r2, g2, b2, a2);
        buffer.addVertex(pose, x1, y, 0).setColor(r1, g1, b1, a1);
        buffer.addVertex(pose, x1, y1, 0).setColor(r1, g1, b1, a1);
        buffer.addVertex(pose, x1 + oX, y1 + oY, 0).setColor(r2, g2, b2, a2);

        BufferUploader.drawWithShader(buffer.buildOrThrow());
        RenderSystem.disableBlend();
    }

    public static void drawDropCircleShadow(GuiGraphics graphics, int x, int y, int radius, int segments,
                                            int opaque, int shadow) {
        Matrix4f pose = graphics.pose().last().pose();
        Matrix4d poseD = new Matrix4d(pose);

        float a1 = Color.getAlphaF(opaque);
        float r1 = Color.getRedF(opaque);
        float g1 = Color.getGreenF(opaque);
        float b1 = Color.getBlueF(opaque);
        float a2 = Color.getAlphaF(shadow);
        float r2 = Color.getRedF(shadow);
        float g2 = Color.getGreenF(shadow);
        float b2 = Color.getBlueF(shadow);

        VertexConsumer buffer = graphics.bufferSource().getBuffer(ModularUIRenderTypes.guiOverlayTriangleFan());
        buffer.addVertex(pose, x, y, 0).setColor(r1, g1, b1, a1);

        Vector3f pos = new Vector3f();
        for (int i = 0; i <= segments; i++) {
            float a = i / (float) segments * TWO_PI - HALF_PI;
            circleVertex(buffer, pose, pos, x, Mth.cos(a), y, Mth.sin(a), radius).setColor(r2, g2, b2, a2);
        }
    }

    public static void drawDropCircleShadow(GuiGraphics graphics, int x, int y, int radius, int offset, int segments,
                                            int opaque, int shadow) {
        if (offset >= radius) {
            drawDropCircleShadow(graphics, x, y, radius, segments, opaque, shadow);
            return;
        }
        Matrix4f pose = graphics.pose().last().pose();

        float a1 = Color.getAlphaF(opaque);
        float r1 = Color.getRedF(opaque);
        float g1 = Color.getGreenF(opaque);
        float b1 = Color.getBlueF(opaque);
        float a2 = Color.getAlphaF(shadow);
        float r2 = Color.getRedF(shadow);
        float g2 = Color.getGreenF(shadow);
        float b2 = Color.getBlueF(shadow);

        VertexConsumer buffer = graphics.bufferSource().getBuffer(ModularUIRenderTypes.guiOverlayTriangleFan());
        /* Draw opaque base */
        buffer.addVertex(pose, x, y, 0).setColor(r1, g1, b1, a1);

        Vector3f pos = new Vector3f();
        for (int i = 0; i <= segments; i++) {
            float a = i / (float) segments * TWO_PI - HALF_PI;
            circleVertex(buffer, pose, pos, x, Mth.cos(a), y, Mth.sin(a), offset).setColor(r1, g1, b1, a1);
        }

        /* Draw outer shadow */
        buffer = graphics.bufferSource().getBuffer(RenderType.gui());

        for (int i = 0; i < segments; i++) {
            float alpha1 = i / (float) segments * TWO_PI - HALF_PI;
            float alpha2 = (i + 1) / (float) segments * TWO_PI - HALF_PI;

            float cosA1 = Mth.cos(alpha1);
            float cosA2 = Mth.cos(alpha2);
            float sinA1 = Mth.sin(alpha1);
            float sinA2 = Mth.sin(alpha2);

            circleVertex(buffer, pose, pos, x, cosA2, y, sinA2, offset).setColor(r1, g1, b1, a1);
            circleVertex(buffer, pose, pos, x, cosA1, y, sinA1, offset).setColor(r1, g1, b1, a1);
            circleVertex(buffer, pose, pos, x, cosA1, y, sinA1, radius).setColor(r2, g2, b2, a2);
            circleVertex(buffer, pose, pos, x, cosA2, y, sinA2, radius).setColor(r2, g2, b2, a2);
        }
    }

    private static VertexConsumer circleVertex(VertexConsumer buffer, Matrix4f pose, Vector3f pos,
                                               float x, float xOffset, float y, float yOffset, float mul) {
        pos.x = x - xOffset * mul;
        pos.y = y + yOffset * mul;
        pose.transformPosition(pos);
        return buffer.addVertex(pos.x, pos.y, pos.z);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawBorder(GuiGraphics graphics, float x, float y, float width, float height, int color,
                                  float border) {
        drawBorderLTRB(graphics, x, y, x + width, y + height, border, color, false);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawText(GuiGraphics graphics, String text, float x, float y,
                                float scale, int color, boolean shadow) {
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 0f);
        float sf = 1 / scale;
        graphics.drawString(Minecraft.getInstance().font, text, x * sf, y * sf, color, shadow);
        graphics.pose().popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawText(GuiGraphics graphics, Component text, float x, float y, float scale,
                                int color, boolean shadow) {
        drawText(graphics, text.getVisualOrderText(), x, y, scale, color, shadow);
    }

    @OnlyIn(Dist.CLIENT)
    public static void drawText(GuiGraphics graphics, FormattedCharSequence text, float x, float y, float scale,
                                int color, boolean shadow) {
        graphics.pose().pushPose();
        graphics.pose().scale(scale, scale, 0f);
        float sf = 1 / scale;
        graphics.drawString(Minecraft.getInstance().font, text, x * sf, y * sf, color, shadow);
        graphics.pose().popPose();
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void drawTooltipBackground(GuiContext context, ItemStack stack, List<ClientTooltipComponent> lines,
                                             int x, int y, int textWidth, int height, @Nullable RichTooltip tooltip) {
        GuiGraphics graphics = context.getGraphics();

        // TODO theme color
        int backgroundTop = 0xF0100010;
        int backgroundBottom = backgroundTop;
        int borderColorStart = 0x505000FF;
        int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
        RenderTooltipEvent.Color colorEvent;

        if (tooltip != null) {
            colorEvent = new RichTooltipEvent.Color(stack, graphics, x, y, context.getFont(),
                    backgroundTop, borderColorStart, borderColorEnd, lines, tooltip);
        } else {
            colorEvent = new RenderTooltipEvent.Color(stack, graphics, x, y, context.getFont(),
                    backgroundTop, borderColorStart, borderColorEnd, lines);
        }

        NeoForge.EVENT_BUS.post(colorEvent);
        backgroundTop = colorEvent.getBackgroundStart();
        backgroundBottom = colorEvent.getBackgroundEnd();
        borderColorStart = colorEvent.getBorderStart();
        borderColorEnd = colorEvent.getBorderEnd();

        // top background border
        drawVerticalGradientRect(graphics, x - 3, y - 4, textWidth + 6, 1, backgroundTop, backgroundTop);
        // bottom background border
        drawVerticalGradientRect(graphics, x - 3, y + height + 3, textWidth + 6, 1, backgroundBottom, backgroundBottom);
        // center background
        drawVerticalGradientRect(graphics, x - 3, y - 3, textWidth + 6, height + 6, backgroundTop, backgroundBottom);
        // left background border
        drawVerticalGradientRect(graphics, x - 4, y - 3, 1, height + 6, backgroundTop, backgroundBottom);
        // right background border
        drawVerticalGradientRect(graphics, x + textWidth + 3, y - 3, 1, height + 6, backgroundTop, backgroundBottom);

        // left accent border
        drawVerticalGradientRect(graphics, x - 3, y - 2, 1, height + 4, borderColorStart, borderColorEnd);
        // right accent border
        drawVerticalGradientRect(graphics, x + textWidth + 2, y - 2, 1, height + 4, borderColorStart, borderColorEnd);
        // top accent border
        drawVerticalGradientRect(graphics, x - 3, y - 3, textWidth + 6, 1, borderColorStart, borderColorStart);
        // bottom accent border
        drawVerticalGradientRect(graphics, x - 3, y + height + 2, textWidth + 6, 1, borderColorEnd, borderColorEnd);
    }
}

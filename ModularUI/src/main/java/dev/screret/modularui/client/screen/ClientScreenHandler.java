package dev.screret.modularui.client.screen;

import dev.screret.modularui.GuiErrorHandler;
import dev.screret.modularui.ModularUI;
import dev.screret.modularui.ModularUIConfig;
import dev.screret.modularui.api.IMuiScreen;
import dev.screret.modularui.api.MCHelper;
import dev.screret.modularui.api.widget.IGuiElement;
import dev.screret.modularui.api.widget.IVanillaSlot;
import dev.screret.modularui.api.widget.IWidget;
import dev.screret.modularui.api.widget.Interactable;
import dev.screret.modularui.client.screen.viewport.GuiContext;
import dev.screret.modularui.client.screen.viewport.LocatedWidget;
import dev.screret.modularui.client.screen.viewport.ModularGuiContext;
import dev.screret.modularui.core.mixins.client.AbstractContainerScreenAccessor;
import dev.screret.modularui.core.mixins.client.ScreenAccessor;
import dev.screret.modularui.drawable.GuiDraw;
import dev.screret.modularui.integration.xei.handlers.RecipeViewerHandler;
import dev.screret.modularui.overlay.OverlayManager;
import dev.screret.modularui.overlay.OverlayStack;
import dev.screret.modularui.utils.Color;
import dev.screret.modularui.utils.FpsCounter;
import dev.screret.modularui.utils.Stencil;
import dev.screret.modularui.widget.sizer.Area;
import dev.screret.modularui.widgets.RichTextWidget;
import dev.screret.modularui.widgets.slot.ItemSlot;
import dev.screret.modularui.widgets.slot.ModularSlot;
import dev.screret.modularui.widgets.slot.SlotGroup;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ContainerScreenEvent;
import net.neoforged.neoforge.client.event.RenderFrameEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.common.NeoForge;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

@ApiStatus.Internal
@EventBusSubscriber(modid = ModularUI.MOD_ID, value = Dist.CLIENT)
public class ClientScreenHandler {

    @Getter
    private static final GuiContext defaultContext = new GuiContext();
    private static final FpsCounter fpsCounter = new FpsCounter();
    private static ModularScreen currentScreen = null;
    @Getter
    private static long ticks = 0L;
    private static IMuiScreen lastMui;
    private static final ObjectArrayList<IMuiScreen> muiStack = new ObjectArrayList<>(8);

    private static boolean debugToggleActive = false;

    // we need to know the actual gui and not some fake screen some other mod overwrites
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onOpenScreen(ScreenEvent.Opening event) {
        onGuiChanged(event.getCurrentScreen(), event.getNewScreen());
    }

    @SubscribeEvent
    public static void onCloseScreen(ScreenEvent.Closing event) {
        onGuiChanged(event.getScreen(), null);
    }

    @SubscribeEvent
    public static void onInitScreenPost(ScreenEvent.Init.Post event) {
        defaultContext.updateScreenArea(event.getScreen().width, event.getScreen().height);
        if (validateGui(event.getScreen())) {
            currentScreen.onResize(event.getScreen().width, event.getScreen().height);
        }
        OverlayStack.foreach(ms -> ms.onResize(event.getScreen().width, event.getScreen().height), false);
    }

    @SubscribeEvent
    public static void onScreenKeyPressedHigh(ScreenEvent.KeyPressed.Pre event) {
        defaultContext.updateLatestKey(event.getKeyCode(), event.getScanCode(), event.getModifiers());
        // TODO: early needs to be before XEI, but emi does mixin into KeyboardHandler so it is before everything
        if (keyPressedEvent(event, InputPhase.EARLY)) {
            keyPressedEvent(event, InputPhase.LATE);
        }
    }

    private static boolean keyPressedEvent(ScreenEvent.KeyPressed.Pre event, InputPhase phase) {
        if (validateGui(event.getScreen())) {
            currentScreen.getContext().updateLatestKey(event.getKeyCode(), event.getScanCode(), event.getModifiers());
        }
        if (handleKeyboardInput(currentScreen, event.getScreen(), true, phase,
                event.getKeyCode(), event.getScanCode(), event.getModifiers())) {
            event.setCanceled(true);
            return false;
        }
        return true;
    }

    @SubscribeEvent
    public static void onScreenKeyReleasedHigh(ScreenEvent.KeyReleased.Pre event) {
        defaultContext.updateLatestKey(event.getKeyCode(), event.getScanCode(), event.getModifiers());
        // TODO also needs to be before XEI
        // dont need late for release event
        keyReleasedEvent(event, InputPhase.EARLY);
    }

    private static boolean keyReleasedEvent(ScreenEvent.KeyReleased.Pre event, InputPhase phase) {
        if (validateGui(event.getScreen())) {
            currentScreen.getContext().updateLatestKey(event.getKeyCode(), event.getScanCode(), event.getModifiers());
        }
        if (handleKeyboardInput(currentScreen, event.getScreen(), false, phase,
                event.getKeyCode(), event.getScanCode(), event.getModifiers())) {
            event.setCanceled(true);
            return false;
        }
        return true;
    }

    // before JEI
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenCharTyped(ScreenEvent.CharacterTyped.Pre event) {
        int codePoint = event.getCodePoint();
        int modifiers = event.getModifiers();
        defaultContext.updateLatestTypedChar(codePoint, modifiers);
        if (validateGui(event.getScreen())) currentScreen.getContext().updateLatestTypedChar(codePoint, modifiers);

        // vanilla also casts to char here
        if (doAction(currentScreen, ms -> ms.charTyped((char) codePoint, modifiers))) {
            event.setCanceled(true);
        }
    }

    // before JEI
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenMousePressed(ScreenEvent.MouseButtonPressed.Pre event) {
        int button = event.getButton();
        double mouseX = event.getMouseX();
        double mouseY = event.getMouseY();
        defaultContext.updateMouseButton(button);
        if (validateGui(event.getScreen())) currentScreen.getContext().updateMouseButton(button);

        if (button == -1) {
            return;
        }
        if (currentScreen != null && currentScreen.handleDraggableInput(mouseX, mouseY, button, true) ||
                doAction(currentScreen, ms -> ms.onMousePressed(mouseX, mouseY, button))) {
            RecipeViewerHandler.getCurrent().setSearchFocused(false);
            event.setCanceled(true);
        }
    }

    // before JEI
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenMouseReleased(ScreenEvent.MouseButtonReleased.Pre event) {
        int button = event.getButton();
        double mouseX = event.getMouseX();
        double mouseY = event.getMouseY();
        defaultContext.updateMouseButton(button);
        if (validateGui(event.getScreen())) currentScreen.getContext().updateMouseButton(button);

        if (currentScreen != null && currentScreen.handleDraggableInput(mouseX, mouseY, button, false) ||
                doAction(currentScreen, ms -> ms.mouseReleased(mouseX, mouseY, button))) {
            RecipeViewerHandler.getCurrent().setSearchFocused(false);
            event.setCanceled(true);
        }
    }

    // before JEI
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenMouseScrolled(ScreenEvent.MouseScrolled.Pre event) {
        double wx = event.getScrollDeltaX(), wy = event.getScrollDeltaY();
        if (wx == 0) return;
        defaultContext.updateMouseWheel(wx, wy);
        if (validateGui(event.getScreen())) currentScreen.getContext().updateMouseWheel(wx, wy);

        if (doAction(currentScreen, ms -> ms.mouseScrolled(event.getMouseX(), event.getMouseY(), wx, wy))) {
            event.setCanceled(true);
        }
    }

    // before JEI
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onScreenMouseDragged(ScreenEvent.MouseDragged.Pre event) {
        if (doAction(currentScreen, ms -> ms.mouseDragged(event.getMouseX(), event.getMouseY(),
                event.getMouseButton(), event.getDragX(), event.getDragY()))) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onScreenRenderLow(ScreenEvent.Render.Pre event) {
        int mx = event.getMouseX(), my = event.getMouseY();
        float pt = event.getPartialTick();
        GuiGraphics gc = event.getGuiGraphics();
        defaultContext.setGraphics(gc);
        defaultContext.updateState(mx, my, pt);
        defaultContext.reset();
        if (validateGui(event.getScreen())) {
            currentScreen.getContext().setGraphics(gc);
            currentScreen.getContext().updateState(mx, my, pt);
            drawScreen(gc, currentScreen, currentScreen.getScreenWrapper().getWrappedScreen(), mx, my, pt);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onScreenRenderNormal(ScreenEvent.Render.Post event) {
        OverlayStack.draw(event.getGuiGraphics(), event.getMouseX(), event.getMouseY(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        OverlayStack.onTick();
        defaultContext.tick();
        if (validateGui()) {
            currentScreen.onUpdate();
        }
        ticks++;
    }

    @SubscribeEvent
    public static void onRenderTickPre(RenderFrameEvent.Pre event) {
        GL11.glEnable(GL11.GL_STENCIL_TEST);
        Stencil.reset();
    }

    @SubscribeEvent
    public static void onRenderTickPost(RenderFrameEvent.Post event) {
        Stencil.reset();
    }

    public static void onFrameUpdate() {
        OverlayStack.foreach(ModularScreen::onFrameUpdate, true);
        if (currentScreen != null) currentScreen.onFrameUpdate();
    }

    private static void onGuiChanged(Screen oldScreen, Screen newScreen) {
        if (oldScreen == newScreen) return;
        defaultContext.reset();
        fpsCounter.reset();
        GuiErrorHandler.INSTANCE.clear();

        IMuiScreen lastLastMui = lastMui;
        if (lastMui != null) {
            // called on open and close
            // invalidate last mui screen, but keep it in stack
            invalidateCurrentScreen();
        }

        if (newScreen instanceof IMuiScreen muiScreen) {
            lastMui = muiScreen;
            currentScreen = muiScreen.getScreen();
            muiStack.remove(muiScreen);
            muiStack.add(muiScreen); // move screen to the top of the stack;
            Screen lastParent = lastLastMui != null ? lastLastMui.getScreen().getContext().getParent() : null;
            if (lastParent != muiScreen) {
                // new screen in the stack
                currentScreen.getContext().setParent(oldScreen);
            } else {
                // last parent is equal to new screen -> effectively popping the current screen from the stack
                // the current screen will disconnect from the stack and therefore need to dispose it
                muiStack.remove(lastLastMui);
                lastLastMui.getScreen().getPanelManager().dispose();
            }
        } else if (newScreen == null) {
            // closing -> clear stack and dispose every screen
            invalidateMuiStack();
        }

        OverlayManager.onOpenScreen(newScreen);
    }

    private static void invalidateCurrentScreen() {
        // reset mouse inputs, relevant when screen gets reopened
        if (lastMui != null) {
            lastMui.getScreen().getPanelManager().closeAll();
            lastMui = null;
        }
        currentScreen = null;
    }

    private static void invalidateMuiStack() {
        muiStack.forEach(muiScreen -> muiScreen.getScreen().getPanelManager().dispose());
        muiStack.clear();
    }

    private static boolean doAction(@Nullable ModularScreen muiScreen, Predicate<ModularScreen> action) {
        return OverlayStack.interact(action, true) || (muiScreen != null && action.test(muiScreen));
    }

    /**
     * This replicates vanilla behavior while also injecting custom behavior for consistency
     */
    private static boolean handleKeyboardInput(@Nullable ModularScreen muiScreen, Screen mcScreen,
                                               boolean isPress, InputPhase inputPhase,
                                               int keyCode, int scanCode, int modifiers) {
        if (isPress) {
            // pressing a key
            return inputPhase.isEarly() ? doAction(muiScreen, ms -> ms.keyPressed(keyCode, scanCode, modifiers)) :
                    keyTyped(mcScreen, keyCode, scanCode, modifiers);
        } else {
            // releasing a key
            return inputPhase.isEarly() && doAction(muiScreen, ms -> ms.keyReleased(keyCode, scanCode, modifiers));
        }
    }

    private static boolean keyTyped(Screen screen, int keyCode, int scanCode, int modifiers) {
        if (currentScreen == null) return false;
        // debug mode C + CTRL + SHIFT + ALT
        if (keyCode == 'C' && Interactable.isControl(modifiers) && Interactable.isShift(modifiers) &&
                Interactable.isAlt(modifiers)) {
            if (!debugToggleActive) {
                ModularUIConfig.GUI_DEBUG_MODE.set(!ModularUIConfig.GUI_DEBUG_MODE.getAsBoolean());
                debugToggleActive = true;
            }
            return true;
        } else {
            debugToggleActive = false;
        }
        if (keyCode == InputConstants.KEY_ESCAPE && screen.shouldCloseOnEsc()) {
            onClose();
            return true;
        }
        boolean isInventoryKey = Minecraft.getInstance().options.keyInventory
                .isActiveAndMatches(InputConstants.getKey(keyCode, scanCode));
        if (keyCode == 1 || isInventoryKey) {
            onClose();
            return true;
        }
        return false;
    }

    private static void onClose() {
        if (currentScreen.getContext().hasDraggable()) {
            currentScreen.getContext().dropDraggable();
        } else {
            currentScreen.getPanelManager().closeTopPanel();
        }
    }

    public static void dragSlot(double mouseX, double mouseY, int button, double dragX, double dragY) {
        ModularGuiContext ctx = currentScreen.getContext();
        getMCScreen().mouseDragged(ctx.getMouseX(), ctx.getMouseY(), button, dragX, dragY);
    }

    public static void clickSlot(ModularScreen ms, Slot slot) {
        Screen screen = ms.getScreenWrapper().getWrappedScreen();
        if (screen instanceof ScreenAccessor acc && screen instanceof IClickableContainerScreen clickableScreen &&
                validateGui(screen)) {
            ModularGuiContext ctx = ms.getContext();
            var buttonList = screen.children();
            try {
                // remove buttons to make sure they are not clicked
                acc.setChildren(Collections.emptyList());
                // set clicked slot to make sure the container clicks the desired slot
                clickableScreen.mui$setClickedSlot(slot);
                screen.mouseClicked(ctx.getMouseX(), ctx.getMouseY(), ctx.getMouseButton());
            } finally {
                // undo modifications
                clickableScreen.mui$setClickedSlot(null);
                acc.setChildren(buttonList);
            }
        }
    }

    public static void releaseSlot() {
        if (hasScreen() && getMCScreen() != null) {
            ModularGuiContext ctx = currentScreen.getContext();
            getMCScreen().mouseReleased(ctx.getMouseX(), ctx.getMouseY(), ctx.getMouseButton());
        }
    }

    public static boolean shouldDrawWorldBackground() {
        return /* ModularUI.isBlurLoaded() || */Minecraft.getInstance().level == null;
    }

    public static void drawDarkBackground(Screen screen, GuiGraphics guiGraphics) {
        if (hasScreen()) {
            float alpha = currentScreen.getMainPanel().getAlpha();
            // vanilla color values as hex
            int color = 0x101010;
            int startAlpha = 0xc0;
            int endAlpha = 0xd0;
            // we need to use normal render type, not overlay here
            guiGraphics.fillGradient(0, 0, screen.width, screen.height,
                    Color.withAlpha(color, (int) (startAlpha * alpha)),
                    Color.withAlpha(color, (int) (endAlpha * alpha)));
            // noinspection removal,UnstableApiUsage
            NeoForge.EVENT_BUS.post(new ScreenEvent.BackgroundRendered(screen, guiGraphics));
        }
    }

    public static void drawScreen(GuiGraphics graphics, ModularScreen muiScreen, Screen mcScreen, int mouseX,
                                  int mouseY, float partialTicks) {
        if (mcScreen instanceof AbstractContainerScreen<?> container) {
            drawContainer(graphics, muiScreen, container, mouseX, mouseY, partialTicks);
        } else {
            drawScreenInternal(graphics, muiScreen, mcScreen, mouseX, mouseY, partialTicks);
        }
    }

    public static void drawScreenInternal(GuiGraphics graphics, ModularScreen muiScreen, Screen mcScreen, int mouseX,
                                          int mouseY, float partialTicks) {
        Stencil.reset();
        muiScreen.getContext().getStencil().push(muiScreen.getScreenArea());
        muiScreen.render(graphics, mouseX, mouseY, partialTicks);
        RenderSystem.disableDepthTest();
        drawVanillaElements(graphics, mcScreen, mouseX, mouseY, partialTicks);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        Lighting.setupForFlatItems();
        muiScreen.drawForeground(graphics, partialTicks);
        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();
        muiScreen.getContext().getStencil().pop();
    }

    public static void drawContainer(GuiGraphics graphics, ModularScreen muiScreen, AbstractContainerScreen<?> mcScreen,
                                     int mouseX, int mouseY, float partialTicks) {
        AbstractContainerScreenAccessor acc = (AbstractContainerScreenAccessor) mcScreen;

        Stencil.reset();
        muiScreen.getContext().getStencil().push(muiScreen.getScreenArea());
        mcScreen.renderBackground(graphics, mouseX, mouseY, partialTicks);
        int x = mcScreen.getGuiLeft();
        int y = mcScreen.getGuiTop();

        acc.invokeRenderBg(graphics, partialTicks, mouseX, mouseY);
        muiScreen.render(graphics, mouseX, mouseY, partialTicks);

        RenderSystem.disableDepthTest();
        // mainly for invtweaks compat
        drawVanillaElements(graphics, mcScreen, mouseX, mouseY, partialTicks);
        acc.setHoveredSlot(null);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        Lighting.setupForFlatItems();
        // acc.invokeRenderLabels(graphics, mouseX, mouseY);

        acc.setHoveredSlot(null);
        IGuiElement hovered = muiScreen.getContext().getTopHovered();
        if (hovered instanceof IVanillaSlot vanillaSlot && vanillaSlot.handleAsVanillaSlot()) {
            acc.setHoveredSlot(vanillaSlot.getVanillaSlot());
        }

        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.pose().pushPose();
        graphics.pose().translate(x, y, 0);
        // noinspection UnstableApiUsage
        NeoForge.EVENT_BUS.post(new ContainerScreenEvent.Render.Foreground(mcScreen, graphics, mouseX, mouseY));

        AbstractContainerMenu menu = mcScreen.getMenu();
        ItemStack draggingItem = acc.getDraggingItem().isEmpty() ? menu.getCarried() : acc.getDraggingItem();
        if (!draggingItem.isEmpty()) {
            int xOffset = 8;
            int yOffset = acc.getDraggingItem().isEmpty() ? 8 : 16;
            String text = null;

            if (!acc.getDraggingItem().isEmpty() && acc.getIsSplittingStack()) {
                draggingItem = draggingItem.copyWithCount(Mth.ceil(draggingItem.getCount() / 2.0F));
            } else if (acc.getIsQuickCrafting() && acc.getQuickCraftSlots().size() > 1) {
                draggingItem = draggingItem.copyWithCount(acc.getQuickCraftingRemainder());
                if (draggingItem.isEmpty()) {
                    text = ChatFormatting.YELLOW + "0";
                }
            }

            drawFloatingItemStack(mcScreen, graphics, draggingItem, mouseX - x - xOffset, mouseY - y - yOffset, text);
        }
        graphics.pose().popPose();

        if (!acc.getSnapbackItem().isEmpty()) {
            float delta = (float) (Util.getMillis() - acc.getSnapbackTime()) / 100.0F;

            if (delta >= 1.0F) {
                delta = 1.0F;
                acc.setSnapbackItem(ItemStack.EMPTY);
            }

            int snapBackOffsetX = acc.getSnapbackEnd().x - acc.getSnapbackStartX();
            int snapBackOffsetY = acc.getSnapbackEnd().y - acc.getSnapbackStartY();
            int snapBackX = acc.getSnapbackStartX() + (int) ((float) snapBackOffsetX * delta);
            int snapBackY = acc.getSnapbackStartY() + (int) ((float) snapBackOffsetY * delta);
            drawFloatingItemStack(mcScreen, graphics, acc.getSnapbackItem(), snapBackX, snapBackY, null);
        }

        muiScreen.drawForeground(graphics, partialTicks);

        RenderSystem.enableDepthTest();
        Lighting.setupFor3DItems();
        muiScreen.getContext().getStencil().pop();
    }

    private static void drawFloatingItemStack(AbstractContainerScreen<?> mcScreen, GuiGraphics graphics,
                                              ItemStack stack, int x, int y, String altText) {
        graphics.pose().pushPose();
        graphics.pose().translate(0.0F, 0.0F, 232.0F);

        var font = IClientItemExtensions.of(stack).getFont(stack, IClientItemExtensions.FontContext.ITEM_COUNT);
        if (font == null) font = ((ScreenAccessor) mcScreen).getFont();
        graphics.renderItem(stack, x, y);
        graphics.renderItemDecorations(font, stack,
                x, y - (((AbstractContainerScreenAccessor) mcScreen).getDraggingItem().isEmpty() ? 0 : 8), altText);
        graphics.pose().popPose();
    }

    private static void drawVanillaElements(GuiGraphics graphics, Screen mcScreen, int mouseX, int mouseY,
                                            float partialTicks) {
        for (Renderable renderable : mcScreen.renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    public static void drawDebugScreen(GuiGraphics graphics, @Nullable ModularScreen muiScreen,
                                       @Nullable ModularScreen fallback) {
        fpsCounter.onDraw();
        if (!ModularUIConfig.isGuiDebugMode()) return;
        if (muiScreen == null) {
            if (validateGui()) {
                muiScreen = currentScreen;
            } else {
                if (fallback == null) return;
                muiScreen = fallback;
            }
        }
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        ModularGuiContext context = muiScreen.getContext();
        Matrix4f pose = graphics.pose().last().pose();

        int mouseX = context.getAbsMouseX(), mouseY = context.getAbsMouseY();
        int screenH = muiScreen.getScreenArea().height;
        int color = Color.argb(180, 40, 115, 220);
        float scale = 0.80f;
        int shift = (int) (11 * scale + 0.5f);
        int lineY = screenH - shift - 2;
        GuiDraw.drawText(graphics, "Mouse Pos: " + mouseX + ", " + mouseY, 5, lineY, scale, color, true);
        lineY -= shift + 2;
        GuiDraw.drawText(graphics, "FPS: " + fpsCounter.getFps(), 5, screenH - 23, scale, color, true);
        lineY -= shift;
        GuiDraw.drawText(graphics, "Theme ID: " + context.getTheme().getId(), 5, lineY, scale, color, true);
        LocatedWidget locatedHovered = muiScreen.getPanelManager().getTopWidgetLocated(true);
        if (locatedHovered != null) {
            drawSegmentLine(graphics, lineY -= 4, scale, color);
            lineY -= 10;

            IWidget hovered = locatedHovered.getElement();
            locatedHovered.applyMatrix(context);
            graphics.pose().pushPose();
            context.applyTo(graphics.pose());

            Area area = hovered.getArea();
            IWidget parent = hovered.getParent();

            GuiDraw.drawBorder(graphics, 0, 0, area.width, area.height, color, scale);
            if (hovered.hasParent()) {
                GuiDraw.drawBorder(graphics, -area.rx, -area.ry, parent.getArea().width, parent.getArea().height,
                        Color.withAlpha(color, 0.3f), scale);
            }
            graphics.pose().popPose();
            locatedHovered.unapplyMatrix(context);
            GuiDraw.drawText(graphics,
                    "Widget Theme: " + hovered.getWidgetTheme(muiScreen.getCurrentTheme()).getKey().getFullName(), 5,
                    lineY, scale, color, true);
            lineY -= shift;
            GuiDraw.drawText(graphics, "Size: " + area.width + ", " + area.height, 5, lineY, scale, color, true);
            lineY -= shift;
            GuiDraw.drawText(graphics, "Pos: " + area.x + ", " + area.y + "  Rel: " + area.rx + ", " + area.ry, 5,
                    lineY, scale, color, false);
            lineY -= shift;
            GuiDraw.drawText(graphics, "Class: " + hovered, 5, lineY, 1, color, true);
            if (hovered.hasParent()) {
                drawSegmentLine(graphics, lineY -= 4, scale, color);
                lineY -= 10;
                GuiDraw.drawText(graphics,
                        "Widget Theme: " + parent.getWidgetTheme(muiScreen.getCurrentTheme()).getKey().getFullName(), 5,
                        lineY, scale, color, true);
                lineY -= shift;
                area = parent.getArea();
                GuiDraw.drawText(graphics, "Parent size: " + area.width + ", " + area.height, 5, lineY, scale, color,
                        true);
                lineY -= shift;
                GuiDraw.drawText(graphics, "Parent: " + parent, 5, lineY, 1, color, true);
            }
            if (hovered instanceof ItemSlot slotWidget) {
                drawSegmentLine(graphics, lineY -= 4, scale, color);
                lineY -= 10;
                ModularSlot slot = slotWidget.getSlot();
                GuiDraw.drawText(graphics, "Slot Index: " + slot.getSlotIndex(), 5, lineY, scale, color, false);
                lineY -= shift;
                GuiDraw.drawText(graphics, "Slot Number: " + ((Slot) slot).index, 5, lineY, scale, color, false);
                lineY -= shift;
                if (slotWidget.isSynced()) {
                    SlotGroup slotGroup = slot.getSlotGroup();
                    boolean allowShiftTransfer = slotGroup != null && slotGroup.isAllowShiftTransfer();
                    GuiDraw.drawText(graphics,
                            "Shift-Click Priority: " +
                                    (allowShiftTransfer ? slotGroup.getShiftClickPriority() : "DISABLED"),
                            5, lineY, scale, color, true);
                }
            } else if (hovered instanceof RichTextWidget richTextWidget) {
                drawSegmentLine(graphics, lineY -= 4, scale, color);
                lineY -= 10;
                locatedHovered.applyMatrix(context);
                Object hoveredElement = richTextWidget.getHoveredElement();
                locatedHovered.unapplyMatrix(context);
                GuiDraw.drawText(graphics, "Hovered: " + hoveredElement, 5, lineY, scale, color, true);
            }
        }
        // dot at mouse pos
        GuiDraw.drawRect(graphics, mouseX, mouseY, 1, 1, Color.withAlpha(Color.GREEN.main, 0.8f));
        graphics.setColor(1f, 1f, 1f, 1f);
    }

    private static void drawSegmentLine(GuiGraphics graphics, int y, float scale, int color) {
        GuiDraw.drawRect(graphics, 5, y, 140 * scale, 1 * scale, color);
    }

    public static boolean hasScreen() {
        return currentScreen != null;
    }

    @Nullable
    public static Screen getMCScreen() {
        return MCHelper.getCurrentScreen();
    }

    @Nullable
    public static ModularScreen getMuiScreen() {
        return currentScreen;
    }

    @UnmodifiableView
    public static List<IMuiScreen> getMuiStack() {
        return Collections.unmodifiableList(muiStack);
    }

    private static boolean validateGui() {
        return validateGui(MCHelper.getCurrentScreen());
    }

    private static boolean validateGui(Screen screen) {
        if (currentScreen == null || !(screen instanceof IMuiScreen muiScreen)) {
            // no mui screen currently open
            return false;
        }
        if (screen != MCHelper.getCurrentScreen() || muiScreen.getScreen() != currentScreen) {
            defaultContext.reset();
            invalidateCurrentScreen();
            if (MCHelper.getCurrentScreen() == null) {
                invalidateMuiStack();
            }
            return false;
        }
        return true;
    }

    public static GuiContext getBestContext() {
        if (validateGui()) {
            return currentScreen.getContext();
        }
        return defaultContext;
    }

    private enum InputPhase {

        // for mui interactions
        EARLY,
        // for mc interactions (like E and ESC)
        LATE;

        public boolean isEarly() {
            return this == EARLY;
        }

        public boolean isLate() {
            return this == LATE;
        }
    }
}

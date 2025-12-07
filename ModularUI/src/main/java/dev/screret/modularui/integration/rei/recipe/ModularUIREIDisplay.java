package dev.screret.modularui.integration.rei.recipe;

import dev.screret.modularui.api.widget.ITooltip;
import dev.screret.modularui.api.widget.IWidget;
import dev.screret.modularui.client.screen.ModularPanel;
import dev.screret.modularui.client.screen.ModularScreen;
import dev.screret.modularui.drawable.text.RichText;
import dev.screret.modularui.integration.recipeviewer.RecipeSlotRole;
import dev.screret.modularui.integration.recipeviewer.RecipeViewerScreenWrapper;
import dev.screret.modularui.integration.recipeviewer.handlers.IngredientProvider;
import dev.screret.modularui.integration.recipeviewer.handlers.fluid.EmptyFluidTank;
import dev.screret.modularui.integration.recipeviewer.util.RecipeScreenRenderingUtil;
import dev.screret.modularui.integration.rei.REIStackConverter;
import dev.screret.modularui.utils.WidgetUtil;
import dev.screret.modularui.utils.memoization.MemoizedSupplier;
import dev.screret.modularui.utils.memoization.Memoizer;
import dev.screret.modularui.widget.sizer.Area;
import dev.screret.modularui.widgets.slot.FluidSlot;
import dev.screret.modularui.widgets.slot.ItemSlot;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.*;
import net.minecraft.network.chat.contents.PlainTextContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import lombok.Getter;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.TooltipContext;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.impl.client.gui.widget.EntryWidget;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

public class ModularUIREIDisplay<T extends Recipe<?>, W extends IWidget> implements Display {

    @Getter
    protected final RecipeHolder<T> recipe;
    protected final MemoizedSupplier<ModularScreen> screen;

    @Getter
    protected final List<EntryIngredient> inputEntries;
    @Getter
    protected final List<EntryIngredient> outputEntries;
    protected final List<EntryIngredient> catalysts;
    @Getter
    protected final CategoryIdentifier<?> categoryIdentifier;

    public ModularUIREIDisplay(RecipeHolder<T> recipe, Supplier<W> widgetSupplier, CategoryIdentifier<?> category) {
        this.recipe = recipe;

        this.inputEntries = new ArrayList<>();
        this.outputEntries = new ArrayList<>();
        this.catalysts = new ArrayList<>();
        this.categoryIdentifier = category;

        this.screen = Memoizer.memoize(() -> {
            W widget = widgetSupplier.get();
            ModularPanel panel = ModularPanel.defaultPanel(recipe.id().toString(), widget.getArea().w(), widget.getArea().h());
            panel.child(widget);
            return new ModularScreen(recipe.id().getNamespace(), panel);
        }, Duration.ofSeconds(10));

        for (IWidget widget : WidgetUtil.getFlatWidgetCollection(widgetSupplier.get())) {
            if (!(widget instanceof IngredientProvider<?> provider)) {
                continue;
            }
            RecipeSlotRole role = provider.recipeRole();
            if (role == RecipeSlotRole.RENDER_ONLY) {
                continue;
            }

            REIStackConverter.Converter<?> converter = REIStackConverter.getForNullable(provider.ingredientClass());
            if (converter == null) {
                continue;
            }
            @SuppressWarnings({ "rawtypes", "unchecked" })
            EntryIngredient ingredient = ((REIStackConverter.Converter) converter).convertTo(provider);

            switch (role) {
                case INPUT -> inputEntries.add(ingredient);
                case OUTPUT -> outputEntries.add(ingredient);
                case CATALYST -> catalysts.add(ingredient);
            }
        }
    }

    @Override
    public Optional<ResourceLocation> getDisplayLocation() {
        return Optional.of(this.recipe.id());
    }

    public List<Widget> createWidget(Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        widgets.add(new UIWrapperWidget());

        for (IWidget widget : WidgetUtil.getFlatWidgetCollection(this.screen.get())) {
            if (!(widget instanceof IngredientProvider<?> provider)) {
                continue;
            }
            RecipeSlotRole role = provider.recipeRole();
            if (role == RecipeSlotRole.RENDER_ONLY) {
                continue;
            }
            REIStackConverter.Converter<?> converter = REIStackConverter.getForNullable(provider.ingredientClass());
            if (converter == null) {
                continue;
            }
            @SuppressWarnings({ "rawtypes", "unchecked" })
            EntryIngredient ingredient = ((REIStackConverter.Converter) converter).convertTo(provider);
            Area area = widget.getArea();

            EntryWidget entryWidget = new EntryWidget(new Rectangle(area.x(), area.y(), area.w(), area.h()));
            // Clear the MUI slots and add EMI slots based on them.
            if (provider instanceof ItemSlot itemSlot) {
                itemSlot.slot(RecipeScreenRenderingUtil.EMPTY_ITEM_HANDLER, 0).invisible();
            } else if (provider instanceof FluidSlot fluidSlot) {
                fluidSlot.syncHandler(EmptyFluidTank.INSTANCE).invisible();
            }

            entryWidget.background(false)
                    .entries(ingredient.castAsList());
            if (role == RecipeSlotRole.INPUT) {
                entryWidget.markIsInput();
            } else if (role == RecipeSlotRole.OUTPUT) {
                entryWidget.markIsOutput();
            } else {
                entryWidget.unmarkInputOrOutput();
            }
            if (widget instanceof ITooltip<?> tooltip && tooltip.hasTooltip()) {
                if (tooltip.tooltip().getRichText() instanceof RichText richText) {
                    var textList = richText.getAsText();
                    entryWidget.tooltipProcessor(text -> {
                        for (FormattedText line : textList) {
                            text = text.add(MutableComponent.create(new ComponentContents() {

                                @Override
                                public <R> Optional<R> visit(FormattedText.ContentConsumer<R> acceptor) {
                                    return line.visit(acceptor);
                                }

                                @Override
                                public <R> Optional<R> visit(FormattedText.StyledContentConsumer<R> acceptor, Style style) {
                                    return line.visit(acceptor, style);
                                }

                                @Override
                                public Type<?> type() {
                                    return PlainTextContents.TYPE;
                                }
                            }));
                        }
                        return text;
                    });
                }
            }
            widgets.add(entryWidget);
        }
        widgets.add(new UIForegroundRenderWidget());

        return widgets;
    }

    @Override
    public List<EntryIngredient> getRequiredEntries() {
        var required = new ArrayList<>(catalysts);
        required.addAll(inputEntries);
        return required;
    }

    public class UIWrapperWidget extends Widget {

        public UIWrapperWidget() {
            ModularScreen screen = ModularUIREIDisplay.this.screen.get();
            screen.construct(new RecipeViewerScreenWrapper(screen));
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            ModularScreen screen = ModularUIREIDisplay.this.screen.get();
            RecipeScreenRenderingUtil.drawScreenBackground(guiGraphics, screen, mouseX, mouseY, partialTick);
        }

        @Override
        public @Nullable Tooltip getTooltip(TooltipContext context) {
            return null;
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        @Override
        public void mouseMoved(double mouseX, double mouseY) {
            screen.get().mouseMoved(mouseX, mouseY);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return screen.get().mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return screen.get().mouseReleased(mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
            return screen.get().mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }

        @Override
        public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
            return screen.get().mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return screen.get().keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
            return screen.get().keyReleased(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char codePoint, int modifiers) {
            return screen.get().charTyped(codePoint, modifiers);
        }
    }

    public class UIForegroundRenderWidget extends Widget {

        public UIForegroundRenderWidget() {}

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            ModularScreen screen = ModularUIREIDisplay.this.screen.get();
            RecipeScreenRenderingUtil.drawScreenForeground(guiGraphics, screen, mouseX, mouseY, partialTick);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }
    }
}

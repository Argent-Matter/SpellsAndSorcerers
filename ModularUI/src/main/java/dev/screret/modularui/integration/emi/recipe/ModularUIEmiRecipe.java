package dev.screret.modularui.integration.emi.recipe;

import dev.screret.modularui.api.widget.ITooltip;
import dev.screret.modularui.api.widget.IWidget;
import dev.screret.modularui.client.screen.ModularPanel;
import dev.screret.modularui.client.screen.ModularScreen;
import dev.screret.modularui.drawable.text.RichText;
import dev.screret.modularui.integration.emi.EmiStackConverter;
import dev.screret.modularui.integration.recipeviewer.RecipeSlotRole;
import dev.screret.modularui.integration.recipeviewer.RecipeViewerScreenWrapper;
import dev.screret.modularui.integration.recipeviewer.handlers.IngredientProvider;
import dev.screret.modularui.integration.recipeviewer.handlers.fluid.EmptyFluidTank;
import dev.screret.modularui.integration.recipeviewer.util.RecipeScreenRenderingUtil;
import dev.screret.modularui.utils.WidgetUtil;
import dev.screret.modularui.utils.memoization.MemoizedSupplier;
import dev.screret.modularui.utils.memoization.Memoizer;
import dev.screret.modularui.widget.sizer.Area;
import dev.screret.modularui.widgets.slot.FluidSlot;
import dev.screret.modularui.widgets.slot.ItemSlot;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.*;
import lombok.Getter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

public abstract class ModularUIEmiRecipe<T extends Recipe<?>, W extends IWidget> implements EmiRecipe {

    @Getter
    protected final RecipeHolder<T> recipe;
    protected final MemoizedSupplier<ModularScreen> screen;

    @Getter
    public final List<EmiIngredient> inputs;
    @Getter
    public final List<EmiStack> outputs;
    @Getter
    public final List<EmiIngredient> catalysts;

    @Getter
    private final Bounds bounds;
    @Getter
    private final int displayWidth, displayHeight;

    public boolean allowRecipeTree = true;

    public ModularUIEmiRecipe(RecipeHolder<T> recipe, Supplier<W> widgetSupplier) {
        this.recipe = recipe;

        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.catalysts = new ArrayList<>();

        W recipeWidget = widgetSupplier.get();
        this.displayWidth = recipeWidget.getArea().width;
        this.displayHeight = recipeWidget.getArea().height;
        this.bounds = new Bounds(0, 0, this.displayWidth, this.displayHeight);

        this.screen = Memoizer.memoize(() -> {
            W widget = widgetSupplier.get();
            ModularPanel panel = ModularPanel.defaultPanel(recipe.id().toString(), widget.getArea().w(), widget.getArea().h());
            panel.child(widget);
            return new ModularScreen(recipe.id().getNamespace(), panel);
        }, Duration.ofSeconds(10));

        for (IWidget widget : WidgetUtil.getFlatWidgetCollection(recipeWidget)) {
            if (!(widget instanceof IngredientProvider<?> provider)) {
                continue;
            }
            RecipeSlotRole role = provider.recipeRole();
            if (role == RecipeSlotRole.RENDER_ONLY) {
                continue;
            }

            EmiStackConverter.Converter<?> converter = EmiStackConverter.getForNullable(provider.ingredientClass());
            if (converter == null) {
                continue;
            }
            @SuppressWarnings({ "rawtypes", "unchecked" })
            EmiIngredient ingredient = ((EmiStackConverter.Converter) converter).convertTo(provider);

            switch (role) {
                case INPUT -> inputs.add(ingredient);
                case OUTPUT -> {
                    if (ingredient.getEmiStacks().size() > 1) {
                        allowRecipeTree = false;
                    }
                    outputs.addAll(ingredient.getEmiStacks());
                }
                case CATALYST -> catalysts.add(ingredient);
            }
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.add(new UIWrapperWidget());

        for (IWidget widget : WidgetUtil.getFlatWidgetCollection(this.screen.get())) {
            if (!(widget instanceof IngredientProvider<?> provider)) {
                continue;
            }
            RecipeSlotRole role = provider.recipeRole();
            if (role == RecipeSlotRole.RENDER_ONLY) {
                continue;
            }
            EmiStackConverter.Converter<?> converter = EmiStackConverter.getForNullable(provider.ingredientClass());
            if (converter == null) {
                continue;
            }
            @SuppressWarnings({ "rawtypes", "unchecked" })
            EmiIngredient ingredient = ((EmiStackConverter.Converter) converter).convertTo(provider);
            Area widgetArea = widget.getArea();

            SlotWidget slotWidget = null;
            // Clear the MUI slots and add EMI slots based on them.
            if (provider instanceof ItemSlot itemSlot) {
                itemSlot.slot(RecipeScreenRenderingUtil.EMPTY_ITEM_HANDLER, 0)
                        .invisible();
            } else if (provider instanceof FluidSlot fluidSlot) {
                fluidSlot.syncHandler(EmptyFluidTank.INSTANCE)
                        .invisible();

                long capacity = Math.max(1, ingredient.getAmount());
                slotWidget = new TankWidget(ingredient, widgetArea.x, widgetArea.y, widgetArea.width, widgetArea.height,
                        capacity);
            }
            if (slotWidget == null) {
                slotWidget = new SlotWidget(ingredient, widgetArea.x, widgetArea.y);
            }

            slotWidget.customBackground(null, widgetArea.x, widgetArea.y, widgetArea.width, widgetArea.height)
                    .drawBack(false);

            if (role == RecipeSlotRole.CATALYST) {
                slotWidget.catalyst(true);
            } else if (role == RecipeSlotRole.OUTPUT) {
                slotWidget.recipeContext(this);
            }
            if (widget instanceof ITooltip<?> tooltip && tooltip.hasTooltip()) {
                if (tooltip.tooltip().getRichText() instanceof RichText richText) {
                    var textList = richText.getAsText();
                    for (FormattedText line : textList) {
                        slotWidget
                                .appendTooltip(() -> ClientTooltipComponent.create(Language.getInstance().getVisualOrder(line)));
                    }
                }
            }
            widgets.add(slotWidget);
        }
        widgets.add(new UIForegroundRenderWidget());
    }

    @Override
    public @Nullable ResourceLocation getId() {
        return this.recipe.id();
    }

    @Override
    public boolean supportsRecipeTree() {
        return this.allowRecipeTree && EmiRecipe.super.supportsRecipeTree();
    }

    public class UIWrapperWidget extends Widget {

        public UIWrapperWidget() {
            ModularScreen screen = ModularUIEmiRecipe.this.screen.get();
            screen.construct(new RecipeViewerScreenWrapper(screen));
        }

        @Override
        public Bounds getBounds() {
            return ModularUIEmiRecipe.this.bounds;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            ModularScreen screen = ModularUIEmiRecipe.this.screen.get();
            RecipeScreenRenderingUtil.drawScreenBackground(guiGraphics, screen, mouseX, mouseY, partialTick);
        }

        @Override
        public boolean mouseClicked(int mouseX, int mouseY, int button) {
            return screen.get().onMousePressed(mouseX, mouseY, button);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return screen.get().keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public class UIForegroundRenderWidget extends Widget {

        public UIForegroundRenderWidget() {}

        @Override
        public Bounds getBounds() {
            return ModularUIEmiRecipe.this.bounds;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            ModularScreen screen = ModularUIEmiRecipe.this.screen.get();
            RecipeScreenRenderingUtil.drawScreenForeground(guiGraphics, screen, mouseX, mouseY, partialTick);
        }
    }
}

package dev.screret.modularui.integration.emi;

import dev.screret.modularui.api.widget.ITooltip;
import dev.screret.modularui.api.widget.IWidget;
import dev.screret.modularui.client.screen.ClientScreenHandler;
import dev.screret.modularui.client.screen.ModularPanel;
import dev.screret.modularui.client.screen.ModularScreen;
import dev.screret.modularui.drawable.text.RichText;
import dev.screret.modularui.integration.recipeviewer.RecipeSlotRole;
import dev.screret.modularui.integration.recipeviewer.RecipeViewerScreenWrapper;
import dev.screret.modularui.integration.recipeviewer.handlers.IngredientProvider;
import dev.screret.modularui.integration.recipeviewer.handlers.fluid.EmptyFluidTank;
import dev.screret.modularui.utils.Stencil;
import dev.screret.modularui.widget.sizer.Area;
import dev.screret.modularui.widgets.slot.FluidSlot;
import dev.screret.modularui.widgets.slot.ItemSlot;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.wrapper.EmptyItemHandler;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.SlotWidget;
import dev.emi.emi.api.widget.TankWidget;
import dev.emi.emi.api.widget.WidgetHolder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class ModularUIEmiRecipe<T extends IWidget> implements EmiRecipe {

    private static final IItemHandlerModifiable EMPTY_ITEM_HANDLER = new EmptyItemHandler();

    @Getter
    protected final ResourceLocation id;

    protected final T widget;
    protected final ModularPanel panel;
    protected final ModularScreen screen;

    @Getter
    public final List<EmiIngredient> inputs;
    @Getter
    public final List<EmiStack> outputs;
    @Getter
    public final List<EmiIngredient> catalysts;

    @Getter
    private final Bounds bounds;

    public boolean allowRecipeTree = true;

    public ModularUIEmiRecipe(ResourceLocation recipeId, Supplier<T> widgetSupplier) {
        this.id = recipeId;
        this.widget = widgetSupplier.get();

        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.catalysts = new ArrayList<>();

        this.panel = ModularPanel.defaultPanel(id.toString(), this.widget.getArea().width, this.widget.getArea().height);
        this.screen = new ModularScreen(id.getNamespace(), this.panel);
        this.bounds = new Bounds(screen.getX(), screen.getY(), screen.getWidth(), screen.getHeight());

        for (IWidget child : getFlatWidgetCollection(this.widget)) {
            if (child instanceof IngredientProvider<?> provider) {
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
    }

    public static List<IWidget> getFlatWidgetCollection(IWidget widget) {
        List<IWidget> list = new ArrayList<>();
        addToFlatWidgetCollection(widget, list);
        return list;
    }

    public static void addToFlatWidgetCollection(IWidget widget, List<IWidget> list) {
        list.add(widget);
        for (IWidget child : widget.getChildren()) {
            addToFlatWidgetCollection(child, list);
        }
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.add(new UIWrapperWidget());

        for (IWidget widget : getFlatWidgetCollection(widget)) {
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
                itemSlot.slot(EMPTY_ITEM_HANDLER, 0)
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
    public boolean supportsRecipeTree() {
        return this.allowRecipeTree && EmiRecipe.super.supportsRecipeTree();
    }

    @Override
    public int getDisplayWidth() {
        return this.bounds.width();
    }

    @Override
    public int getDisplayHeight() {
        return this.bounds.height();
    }

    public class UIWrapperWidget extends dev.emi.emi.api.widget.Widget {

        public UIWrapperWidget() {
            ModularUIEmiRecipe.this.screen.construct(new RecipeViewerScreenWrapper(ModularUIEmiRecipe.this.screen));
        }

        @Override
        public Bounds getBounds() {
            return ModularUIEmiRecipe.this.bounds;
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            screen.getContext().setGraphics(graphics);
            screen.getContext().updateState(mouseX, mouseY, partialTick);
            screen.getContext().graphicsPose().pushPose();

            // copied from ClientScreenHandler#drawScreenInternal to
            // let us draw foreground elements separately after everything else.
            Stencil.reset();
            screen.getContext().getStencil().push(screen.getScreenArea());

            screen.render(graphics, mouseX, mouseY, partialTick);

            RenderSystem.disableDepthTest();

            ClientScreenHandler.drawVanillaElements(graphics, screen.getScreenWrapper().getWrappedScreen(),
                    mouseX, mouseY, partialTick);

            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            screen.getContext().getStencil().pop();
            screen.getContext().graphicsPose().popPose();
        }

        @Override
        public boolean mouseClicked(int mouseX, int mouseY, int button) {
            return screen.onMousePressed(mouseX, mouseY, button);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return screen.keyPressed(keyCode, scanCode, modifiers);
        }
    }

    public class UIForegroundRenderWidget extends dev.emi.emi.api.widget.Widget {

        public UIForegroundRenderWidget() {}

        @Override
        public Bounds getBounds() {
            return ModularUIEmiRecipe.this.bounds;
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            screen.getContext().setGraphics(graphics);
            screen.getContext().updateState(mouseX, mouseY, partialTick);
            screen.getContext().graphicsPose().pushPose();

            // copied from ClientScreenHandler#drawScreenInternal to
            // let us draw foreground elements separately after everything else.
            screen.getContext().getStencil().push(screen.getScreenArea());
            RenderSystem.disableDepthTest();
            Lighting.setupForFlatItems();

            screen.drawForeground(graphics, partialTick);

            RenderSystem.enableDepthTest();
            Lighting.setupFor3DItems();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

            screen.getContext().getStencil().pop();
            screen.getContext().graphicsPose().popPose();
        }
    }
}

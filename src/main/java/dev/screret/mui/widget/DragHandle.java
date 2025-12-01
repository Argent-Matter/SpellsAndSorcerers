package dev.screret.mui.widget;

import dev.screret.mui.api.layout.IViewportStack;
import dev.screret.mui.api.widget.IDraggable;
import dev.screret.mui.api.widget.IGuiElement;
import dev.screret.mui.api.widget.IWidget;
import dev.screret.mui.utils.HoveredWidgetList;
import dev.screret.mui.widget.sizer.Area;
import dev.screret.mui.client.screen.DraggablePanelWrapper;
import dev.screret.mui.client.screen.ModularPanel;
import dev.screret.mui.client.screen.viewport.ModularGuiContext;

import net.minecraft.client.gui.GuiGraphics;

import org.jetbrains.annotations.Nullable;

public class DragHandle extends Widget<DragHandle> implements IDraggable {

    private IDraggable parentDraggable;

    @Override
    public void onInit() {
        IWidget parent = getParent();
        while (!(parent instanceof ModularPanel)) {
            if (parent instanceof IDraggable draggable) {
                this.parentDraggable = draggable;
                return;
            }
            parent = parent.getParent();
        }
        if (((ModularPanel) parent).isDraggable()) {
            this.parentDraggable = new DraggablePanelWrapper((ModularPanel) parent);
        }
    }

    @Override
    public void drawMovingState(GuiGraphics graphics, ModularGuiContext context, float partialTicks) {
        if (this.parentDraggable != null) {
            this.parentDraggable.drawMovingState(graphics, context, partialTicks);
        }
    }

    @Override
    public boolean onDragStart(int button) {
        return this.parentDraggable != null && this.parentDraggable.onDragStart(button);
    }

    @Override
    public void onDragEnd(boolean successful) {
        if (this.parentDraggable != null) {
            this.parentDraggable.onDragEnd(successful);
        }
    }

    @Override
    public void onDrag(int mouseButton, double timeSinceLastClick) {
        if (this.parentDraggable != null) {
            this.parentDraggable.onDrag(mouseButton, timeSinceLastClick);
        }
    }

    @Override
    public boolean canDropHere(int x, int y, @Nullable IGuiElement widget) {
        return this.parentDraggable != null && this.parentDraggable.canDropHere(x, y, widget);
    }

    @Override
    public @Nullable Area getMovingArea() {
        Area.SHARED.reset();
        return this.parentDraggable != null ? this.parentDraggable.getMovingArea() : Area.SHARED;
    }

    @Override
    public boolean isMoving() {
        return this.parentDraggable != null && this.parentDraggable.isMoving();
    }

    @Override
    public void setMoving(boolean moving) {
        if (this.parentDraggable != null) {
            this.parentDraggable.setMoving(moving);
        }
    }

    @Override
    public void transform(IViewportStack stack) {
        super.transform(stack);
    }

    @Override
    public void transformChildren(IViewportStack stack) {
        if (this.parentDraggable != null) {
            this.parentDraggable.transformChildren(stack);
        }
    }

    @Override
    public void getWidgetsAt(IViewportStack stack, HoveredWidgetList widgets, int x, int y) {
        if (this.parentDraggable != null) {
            this.parentDraggable.getWidgetsAt(stack, widgets, x, y);
        }
    }

    @Override
    public void getSelfAt(IViewportStack stack, HoveredWidgetList widgets, int x, int y) {
        if (this.parentDraggable != null) {
            this.parentDraggable.getSelfAt(stack, widgets, x, y);
        }
    }
}

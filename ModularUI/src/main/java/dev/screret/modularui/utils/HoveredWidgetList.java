package dev.screret.modularui.utils;

import dev.screret.modularui.api.widget.IWidget;
import dev.screret.modularui.client.screen.viewport.LocatedWidget;
import dev.screret.modularui.client.screen.viewport.TransformationMatrix;

import it.unimi.dsi.fastutil.objects.ObjectList;

import org.jetbrains.annotations.Nullable;

public class HoveredWidgetList {

    private final ObjectList<LocatedWidget> delegate;

    public HoveredWidgetList(ObjectList<LocatedWidget> delegate) {
        this.delegate = delegate;
    }

    public void add(IWidget widget, TransformationMatrix viewports, Object additionalHoverInfo) {
        this.delegate.add(0, new LocatedWidget(widget, viewports, additionalHoverInfo));
    }

    @Nullable
    public IWidget peek() {
        return isEmpty() ? null : this.delegate.get(0).getElement();
    }

    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    public int size() {
        return this.delegate.size();
    }
}

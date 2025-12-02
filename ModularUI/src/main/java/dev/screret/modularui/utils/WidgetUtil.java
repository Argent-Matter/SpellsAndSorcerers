package dev.screret.modularui.utils;

import dev.screret.modularui.api.widget.IWidget;
import dev.screret.modularui.widget.ParentWidget;

import java.util.Objects;

public class WidgetUtil {

    public static IWidget getWidget(ParentWidget<?> parent, String name) {
        for (IWidget child : parent.getChildren()) {
            if (Objects.equals(child.getName(), name)) {
                return child;
            }
            if (child instanceof ParentWidget<?> childParent) {
                IWidget found = getWidget(childParent, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}

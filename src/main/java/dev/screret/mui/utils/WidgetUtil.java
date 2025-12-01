package dev.screret.mui.utils;

import dev.screret.mui.api.widget.IWidget;
import dev.screret.mui.widget.ParentWidget;

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

package dev.screret.modularui.utils;

import dev.screret.modularui.api.widget.IWidget;
import dev.screret.modularui.client.screen.ModularScreen;
import dev.screret.modularui.widget.ParentWidget;

import java.util.ArrayList;
import java.util.List;
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

    public static List<IWidget> getFlatWidgetCollection(ModularScreen screen) {
        List<IWidget> list = new ArrayList<>();
        addToFlatWidgetCollection(screen.getMainPanel(), list);
        return list;
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
}

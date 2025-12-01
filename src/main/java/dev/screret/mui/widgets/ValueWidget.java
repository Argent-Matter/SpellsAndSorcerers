package dev.screret.mui.widgets;

import dev.screret.mui.api.widget.IValueWidget;
import dev.screret.mui.widget.Widget;

import lombok.Getter;

public class ValueWidget<W extends ValueWidget<W, T>, T> extends Widget<W> implements IValueWidget<T> {

    @Getter
    private final T widgetValue;

    public ValueWidget(T widgetValue) {
        this.widgetValue = widgetValue;
    }
}

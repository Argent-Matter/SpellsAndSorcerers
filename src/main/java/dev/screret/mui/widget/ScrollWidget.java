package dev.screret.mui.widget;

import dev.screret.mui.api.widget.IParentWidget;
import dev.screret.mui.api.widget.IWidget;
import dev.screret.mui.widget.scroll.HorizontalScrollData;
import dev.screret.mui.widget.scroll.VerticalScrollData;

public class ScrollWidget<W extends ScrollWidget<W>> extends AbstractScrollWidget<IWidget, W>
                         implements IParentWidget<IWidget, W> {

    public ScrollWidget() {
        super(null, null);
    }

    public ScrollWidget(VerticalScrollData data) {
        super(null, data);
    }

    public ScrollWidget(HorizontalScrollData data) {
        super(data, null);
    }

    @Override
    public boolean addChild(IWidget child, int index) {
        return super.addChild(child, index);
    }
}

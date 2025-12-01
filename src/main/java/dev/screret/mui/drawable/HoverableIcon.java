package dev.screret.mui.drawable;

import dev.screret.mui.api.drawable.IHoverable;
import dev.screret.mui.api.drawable.IIcon;
import dev.screret.mui.api.widget.ITooltip;
import dev.screret.mui.widget.sizer.Area;
import dev.screret.mui.client.screen.RichTooltip;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HoverableIcon extends DelegateIcon implements IHoverable, ITooltip<HoverableIcon> {

    private final Area area = new Area();
    @Getter
    private @Nullable RichTooltip tooltip;

    public HoverableIcon(IIcon icon) {
        super(icon);
        setRenderedAt(0, 0);
    }

    @Override
    public void setRenderedAt(int x, int y) {
        this.area.set(x, y, getWidth(), getHeight());
    }

    @Override
    public Area getRenderedArea() {
        this.area.setSize(getWidth(), getHeight());
        return this.area;
    }

    @Override
    public @NotNull RichTooltip tooltip() {
        if (this.tooltip == null) this.tooltip = new RichTooltip().parent(area -> area.set(getRenderedArea()));
        return tooltip;
    }

    @Override
    public HoverableIcon tooltip(RichTooltip tooltip) {
        this.tooltip = tooltip;
        return this;
    }
}

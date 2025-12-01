package dev.screret.mui.api.drawable;

import dev.screret.mui.drawable.text.RichText;
import dev.screret.mui.widget.sizer.Area;
import dev.screret.mui.client.screen.RichTooltip;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

/**
 * This marks an {@link IDrawable} as hoverable in a {@link RichText RichText}. This should not be
 * extended in most cases instead obtain an instance by calling {@link IIcon#asHoverable()}.
 */
@ApiStatus.NonExtendable
public interface IHoverable extends IIcon {

    /**
     * Called every frame this hoverable is hovered inside a
     * {@link dev.screret.mui.drawable.text.RichText RichText}.
     */
    default void onHover() {}

    @Nullable
    default RichTooltip getTooltip() {
        return null;
    }

    /**
     * An internal function to set the current rendered position. This is used to detect if this element is under the
     * mouse.
     */
    void setRenderedAt(int x, int y);

    /**
     * @return the last area this drawable was drawn at.
     */
    Area getRenderedArea();
}

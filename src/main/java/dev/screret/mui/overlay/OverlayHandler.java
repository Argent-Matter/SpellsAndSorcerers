package dev.screret.mui.overlay;

import dev.screret.mui.client.screen.ModularScreen;

import net.minecraft.client.gui.screens.Screen;

import java.util.function.Function;
import java.util.function.Predicate;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
public class OverlayHandler implements Comparable<OverlayHandler> {

    private final Predicate<Screen> test;
    private final Function<Screen, ModularScreen> overlayFunction;
    private final int priority;

    public OverlayHandler(Predicate<Screen> test, Function<Screen, ModularScreen> overlayFunction) {
        this(test, overlayFunction, 1000);
    }

    public OverlayHandler(Predicate<Screen> test, Function<Screen, ModularScreen> overlayFunction, int priority) {
        this.test = test;
        this.overlayFunction = overlayFunction;
        this.priority = priority;
    }

    public boolean isValidFor(Screen screen) {
        return this.test.test(screen);
    }

    public ModularScreen createOverlay(Screen screen) {
        return this.overlayFunction.apply(screen);
    }

    @Override
    public int compareTo(@NotNull OverlayHandler o) {
        return Integer.compare(this.priority, o.priority);
    }
}

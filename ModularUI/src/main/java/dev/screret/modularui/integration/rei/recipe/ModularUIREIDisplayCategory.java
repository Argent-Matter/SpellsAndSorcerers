package dev.screret.modularui.integration.rei.recipe;

import dev.screret.modularui.api.widget.IWidget;

import net.minecraft.world.item.crafting.Recipe;

import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;

import java.util.List;

public abstract class ModularUIREIDisplayCategory<T extends Recipe<?>, W extends IWidget, D extends ModularUIREIDisplay<T, W>> implements DisplayCategory<D> {

    @Override
    public List<Widget> setupDisplay(D display, Rectangle bounds) {
        return display.createWidgets(bounds);
    }
}

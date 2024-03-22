package screret.sas.integration.rei.wand;

import com.google.common.collect.Lists;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.InputIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import screret.sas.SpellsAndSorcerers;
import screret.sas.block.ModBlocks;

import java.util.List;

public class WandRecipeCategory implements DisplayCategory<DefaultWandDisplay> {

    public static final CategoryIdentifier<DefaultWandDisplay> WANDS = CategoryIdentifier.of(SpellsAndSorcerers.MODID, "plugins/wands");

    @Override
    public CategoryIdentifier<DefaultWandDisplay> getCategoryIdentifier() {
        return WANDS;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("container.sas.wand_table");
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModBlocks.WAND_TABLE.get());
    }

    @Override
    public List<Widget> setupDisplay(DefaultWandDisplay display, Rectangle bounds) {
        Point startPoint = new Point(bounds.getCenterX() - 58, bounds.getCenterY() - 18);
        List<Widget> widgets = Lists.newArrayList();
        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createArrow(new Point(startPoint.x + 60, startPoint.y + 9)));
        widgets.add(Widgets.createResultSlotBackground(new Point(startPoint.x + 104, startPoint.y + 19)));
        List<InputIngredient<EntryStack<?>>> input = display.getInputIngredients(3, 2);
        List<Slot> slots = Lists.newArrayList();
        for (int y = 0; y < 3; y++)
            for (int x = 0; x < 2; x++)
                slots.add(Widgets.createSlot(new Point(startPoint.x + 1 + x * 18, startPoint.y + 1 + y * 18)).markInput());
        for (InputIngredient<EntryStack<?>> ingredient : input) {
            slots.get(ingredient.getIndex()).entries(ingredient.get());
        }
        widgets.addAll(slots);
        widgets.add(Widgets.createSlot(new Point(startPoint.x + 104, startPoint.y + 19)).entries(display.getOutputEntries().get(0)).disableBackground().markOutput());
        if (display.isShapeless()) {
            widgets.add(Widgets.createShapelessIcon(bounds));
        }
        return widgets;
    }
}

package dev.screret.modularui.integration.jei.handler;

import dev.screret.modularui.api.IMuiScreen;
import dev.screret.modularui.core.mixins.jei.IngredientListOverlayAccessor;
import dev.screret.modularui.integration.jei.MuiJeiPlugin;
import dev.screret.modularui.integration.recipeviewer.handlers.GhostIngredientSlot;
import dev.screret.modularui.integration.recipeviewer.handlers.RecipeViewerHandler;

import net.minecraft.client.gui.screens.Screen;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import mezz.jei.api.gui.handlers.IGhostIngredientHandler;
import mezz.jei.api.ingredients.ITypedIngredient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class JeiScreenHandler<T extends Screen & IMuiScreen> extends RecipeViewerHandler
                             implements IGhostIngredientHandler<T> {

    private static final Map<Class<?>, JeiScreenHandler<?>> CACHE = new Reference2ReferenceOpenHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends Screen & IMuiScreen> JeiScreenHandler<T> of(Class<T> cls) {
        return (JeiScreenHandler<T>) CACHE.computeIfAbsent(cls, c -> new JeiScreenHandler<T>());
    }

    private JeiScreenHandler() {}

    @Override
    public <I> @NotNull List<Target<I>> getTargetsTyped(T screen,
                                                        @NotNull ITypedIngredient<I> ingredient,
                                                        boolean doStart) {
        currentIngredient = ingredient;

        List<GhostIngredientSlot<?>> ghostSlots = screen.getScreen().getContext()
                .getRecipeViewerSettings().getGhostIngredientSlots();
        List<Target<I>> ghostHandlerTargets = new ArrayList<>();
        for (var slot : ghostSlots) {
            if (slot.isEnabled() && slot.castGhostIngredientIfValid(ingredient.getIngredient()) != null) {
                @SuppressWarnings("unchecked")
                GhostIngredientSlot<I> slotWithType = (GhostIngredientSlot<I>) slot;
                ghostHandlerTargets.add(new GhostIngredientTarget<>(slotWithType));
            }
        }
        return ghostHandlerTargets;
    }

    @Override
    public void onComplete() {
        currentIngredient = null;
    }

    static ITypedIngredient<?> currentIngredient = null;

    @Override
    public void setSearchFocused(boolean focused) {
        // only set the search field state if it's JEI's actual search field and not JEMI
        if (MuiJeiPlugin.getRuntime().getIngredientListOverlay() instanceof IngredientListOverlayAccessor accessor) {
            accessor.getSearchField().setFocused(focused);
        }
    }

    @Override
    public @Nullable Object getCurrentlyDragged() {
        if (currentIngredient == null) return null;
        return currentIngredient.getIngredient();
    }
}

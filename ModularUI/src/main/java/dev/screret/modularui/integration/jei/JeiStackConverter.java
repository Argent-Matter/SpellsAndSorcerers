package dev.screret.modularui.integration.jei;

import dev.screret.modularui.integration.recipeviewer.entry.EntryList;
import dev.screret.modularui.integration.recipeviewer.entry.fluid.FluidStackList;
import dev.screret.modularui.integration.recipeviewer.entry.fluid.FluidTagList;
import dev.screret.modularui.integration.recipeviewer.entry.item.ItemStackList;
import dev.screret.modularui.integration.recipeviewer.entry.item.ItemTagList;
import dev.screret.modularui.integration.recipeviewer.handlers.IngredientProvider;
import dev.screret.modularui.utils.math.MathHelper;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.fluids.FluidStack;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.neoforge.NeoForgeTypes;

import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Custom {@link mezz.jei.library.ingredients.TypedIngredient TypedIngredient} -> vanilla/forge/mod stack converters
 */
public class JeiStackConverter {

    public static final Map<Class<?>, Converter<?>> CONVERTERS = new Reference2ReferenceOpenHashMap<>();

    public static final Converter<ItemStack> ITEM = register(ItemStack.class, new Converter<>() {

        private static final List<ITypedIngredient<ItemStack>> EMPTY_STACK = toTypedIngredient(ItemStack.EMPTY)
                .map(Collections::singletonList).orElse(Collections.emptyList());

        @Override
        public ItemStack convertFrom(ITypedIngredient<ItemStack> stack) {
            return stack.getIngredient();
        }

        private static Optional<ITypedIngredient<ItemStack>> toTypedIngredient(ItemStack stack) {
            return ModularUIJeiPlugin.getRuntime().getIngredientManager()
                    .createTypedIngredient(VanillaTypes.ITEM_STACK, stack);
        }

        private static Stream<ITypedIngredient<ItemStack>> toTypedIngredient(Stream<ItemStack> stream) {
            return stream.map(stack -> toTypedIngredient(stack))
                    .filter(Optional::isPresent)
                    .map(Optional::get);
        }

        @Override
        public List<ITypedIngredient<ItemStack>> convertTo(EntryList<ItemStack> stack, float chance,
                                                                 UnaryOperator<ItemStack> mapper) {
            if (stack.isEmpty()) {
                return EMPTY_STACK;
            }
            if (stack instanceof ItemStackList stackList) {
                return toTypedIngredient(stackList.stream()).toList();
            } else if (stack instanceof ItemTagList tagList) {
                return tagList.getEntries().stream()
                        .map(ItemTagList.ItemTagEntry::stacks)
                        .flatMap(stream -> toTypedIngredient(stream))
                        .toList();
            }
            return EMPTY_STACK;
        }
    });
    public static final Converter<FluidStack> FLUID = register(FluidStack.class, new Converter<>() {

        private static final List<ITypedIngredient<FluidStack>> EMPTY_STACK = toTypedIngredient(FluidStack.EMPTY)
                .map(Collections::singletonList).orElse(Collections.emptyList());

        @Override
        public FluidStack convertFrom(ITypedIngredient<FluidStack> stack) {
            return stack.getIngredient();
        }

        private static Optional<ITypedIngredient<FluidStack>> toTypedIngredient(FluidStack stack) {
            return ModularUIJeiPlugin.getRuntime().getIngredientManager()
                    .createTypedIngredient(NeoForgeTypes.FLUID_STACK, stack);
        }

        private static Stream<ITypedIngredient<FluidStack>> toTypedIngredient(Stream<FluidStack> stream) {
            return stream.map(stack -> toTypedIngredient(stack))
                    .filter(Optional::isPresent)
                    .map(Optional::get);
        }

        @Override
        public List<ITypedIngredient<FluidStack>> convertTo(EntryList<FluidStack> stack, float chance, UnaryOperator<FluidStack> mapper) {
            if (stack.isEmpty()) {
                return EMPTY_STACK;
            }
            if (stack instanceof FluidStackList stackList) {
                return toTypedIngredient(stackList.stream().map(mapper)).toList();
            } else if (stack instanceof FluidTagList tagList) {
                return tagList.getEntries().stream()
                        .map(FluidTagList.FluidTagEntry::stacks)
                        .flatMap(stream -> toTypedIngredient(stream))
                        .toList();
            }
            return EMPTY_STACK;
        }
    });

    public static <T> Converter<T> register(Class<T> clazz, Converter<T> converter) {
        CONVERTERS.put(clazz, converter);
        return converter;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    public static <T> Converter<T> getForNullable(Class<T> clazz) {
        return (Converter<T>) CONVERTERS.get(clazz);
    }

    public static <T> Optional<Converter<T>> getFor(Class<T> clazz) {
        return Optional.ofNullable(getForNullable(clazz));
    }

    public interface Converter<T> {

        T convertFrom(ITypedIngredient<T> stack);

        List<ITypedIngredient<T>> convertTo(EntryList<T> stack, float chance, UnaryOperator<T> mapper);

        default List<ITypedIngredient<T>> convertTo(IngredientProvider<T> slot) {
            return this.convertTo(slot.getIngredients(), slot.chance(), slot.renderMappingFunction());
        }
    }
}

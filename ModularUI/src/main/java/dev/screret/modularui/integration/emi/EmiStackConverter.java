package dev.screret.modularui.integration.emi;

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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;

import dev.emi.emi.api.neoforge.NeoForgeEmiStack;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;

import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

/**
 * Custom {@link EmiStack} <-> vanilla/forge/mod stack converters
 */
public class EmiStackConverter {

    public static final Map<Class<?>, Converter<?>> CONVERTERS = new Reference2ReferenceOpenHashMap<>();

    public static final Converter<ItemStack> ITEM = register(ItemStack.class, new Converter<>() {

        @Override
        public @Nullable ItemStack convertFrom(EmiStack stack) {
            Item key = stack.getKeyOfType(Item.class);
            if (key == null || key == Items.AIR) {
                return null;
            }
            ItemStack itemStack = new ItemStack(key, MathHelper.saturatedCast(stack.getAmount()));
            itemStack.applyComponents(stack.getComponentChanges());
            return itemStack;
        }

        private static EmiIngredient toEMIIngredient(Stream<ItemStack> stream) {
            return EmiIngredient.of(stream.map(EmiStack::of).toList());
        }

        @Override
        public EmiIngredient convertTo(EntryList<ItemStack> stack, float chance,
                                       UnaryOperator<ItemStack> mapper) {
            if (stack.isEmpty()) {
                return EmiStack.EMPTY;
            }
            if (stack instanceof ItemStackList stackList) {
                return toEMIIngredient(stackList.stream()).setChance(chance);
            } else if (stack instanceof ItemTagList tagList) {
                return EmiIngredient.of(tagList.getEntries().stream()
                        .map(ItemTagList.ItemTagEntry::stacks)
                        .map(stream -> toEMIIngredient(stream))
                        .collect(Collectors.toList()), tagList.getEntries().get(0).amount()).setChance(chance);
            }
            return EmiStack.EMPTY;
        }
    });
    public static final Converter<FluidStack> FLUID = register(FluidStack.class, new Converter<>() {

        @Override
        public @Nullable FluidStack convertFrom(EmiStack stack) {
            Fluid key = stack.getKeyOfType(Fluid.class);
            if (key == null || key == Fluids.EMPTY) {
                return null;
            }
            return new FluidStack(key.builtInRegistryHolder(), MathHelper.saturatedCast(stack.getAmount()),
                    stack.getComponentChanges());
        }

        private static EmiIngredient toEMIIngredient(Stream<FluidStack> stream) {
            return EmiIngredient.of(stream.map(NeoForgeEmiStack::of).toList());
        }

        @Override
        public EmiIngredient convertTo(EntryList<FluidStack> stack, float chance,
                                       UnaryOperator<FluidStack> mapper) {
            if (stack.isEmpty()) {
                return EmiStack.EMPTY;
            }
            if (stack instanceof FluidStackList stackList) {
                return toEMIIngredient(stackList.stream().map(mapper)).setChance(chance);
            } else if (stack instanceof FluidTagList tagList) {
                return EmiIngredient.of(tagList.getEntries().stream()
                        .map(FluidTagList.FluidTagEntry::stacks)
                        .map(stream -> toEMIIngredient(stream))
                        .collect(Collectors.toList()), tagList.getEntries().getFirst().amount()).setChance(chance);
            }
            return EmiStack.EMPTY;
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

        @Nullable
        T convertFrom(EmiStack stack);

        EmiIngredient convertTo(EntryList<T> stack, float chance, UnaryOperator<T> mapper);

        default EmiIngredient convertTo(IngredientProvider<T> slot) {
            return this.convertTo(slot.getIngredients(), slot.chance(), slot.renderMappingFunction());
        }
    }
}

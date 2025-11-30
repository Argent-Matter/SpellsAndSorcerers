package dev.screret.mitm.common.recipe.ingredient;

import dev.screret.mitm.api.ability.WandAbilityInstance;
import dev.screret.mitm.common.item.component.WandComponent;
import dev.screret.mitm.data.MITMDataComponents;
import dev.screret.mitm.data.MITMIngredientTypes;
import dev.screret.mitm.data.MITMItems;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WandAbilityIngredient implements ICustomIngredient {

    // spotless:off
    public static final MapCodec<WandAbilityIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WandAbilityInstance.CODEC.fieldOf("primary").forGetter(WandAbilityIngredient::getPrimary),
            WandAbilityInstance.CODEC.optionalFieldOf("secondary").forGetter(WandAbilityIngredient::getSecondaryOptional),
            ItemStack.ITEM_NON_AIR_CODEC.fieldOf("item").forGetter(WandAbilityIngredient::getItemHolder),
            Codec.BOOL.optionalFieldOf("powered_up", false).forGetter(WandAbilityIngredient::isPoweredUp)
    ).apply(instance, WandAbilityIngredient::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, WandAbilityIngredient> STREAM_CODEC = StreamCodec.composite(
            WandAbilityInstance.STREAM_CODEC, WandAbilityIngredient::getPrimary,
            ByteBufCodecs.optional(WandAbilityInstance.STREAM_CODEC), WandAbilityIngredient::getSecondaryOptional,
            ByteBufCodecs.holderRegistry(Registries.ITEM), WandAbilityIngredient::getItemHolder,
            ByteBufCodecs.BOOL, WandAbilityIngredient::isPoweredUp,
            WandAbilityIngredient::new
    );
    // spotless:on

    @Getter(AccessLevel.PRIVATE)
    private final WandAbilityInstance primary;
    @Nullable
    private final WandAbilityInstance secondary;

    @Getter
    private final boolean poweredUp;
    @Getter
    private final ItemStack stack;

    public WandAbilityIngredient(WandAbilityInstance primary, @Nullable WandAbilityInstance secondary, Item item,
                                 boolean poweredUp) {
        this.primary = primary;
        this.stack = item.getDefaultInstance();
        if (item == MITMItems.WAND_CORE.get()) {
            this.stack.set(MITMDataComponents.WAND_CORE, primary);
        } else {
            this.stack.set(MITMDataComponents.WAND, new WandComponent(primary, Optional.ofNullable(secondary), poweredUp));
        }

        this.secondary = secondary;
        this.poweredUp = poweredUp;
    }

    private WandAbilityIngredient(WandAbilityInstance primary, Optional<WandAbilityInstance> secondary, Holder<Item> item,
                                 boolean poweredUp) {
        this(primary, secondary.orElse(null), item.value(), poweredUp);
    }

    private Optional<WandAbilityInstance> getSecondaryOptional() {
        return Optional.ofNullable(this.secondary);
    }

    private Holder<Item> getItemHolder() {
        return this.stack.getItemHolder();
    }

    @Override
    public @NotNull Stream<ItemStack> getItems() {
        return Stream.of(stack);
    }

    @Nullable
    public static WandAbilityIngredient fromStack(ItemStack stack) {
        WandComponent wand = stack.get(MITMDataComponents.WAND);
        if (wand != null) {
            return new WandAbilityIngredient(wand.primary(), wand.secondaryOrNull(), stack.getItem(),
                    wand.poweredUp());
        } else if (stack.has(MITMDataComponents.WAND_CORE)) {
            WandAbilityInstance wandCore = stack.get(MITMDataComponents.WAND_CORE);
            return new WandAbilityIngredient(wandCore, null, stack.getItem(), false);
        }
        return null;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public @NotNull IngredientType<?> getType() {
        return MITMIngredientTypes.WAND_ABILITY.get();
    }

    public boolean test(@Nullable ItemStack input) {
        if (input == null || !ItemStack.isSameItem(this.stack, input)) return false;

        if (this.stack.has(MITMDataComponents.WAND_CORE)) {
            return Objects.equals(this.stack.get(MITMDataComponents.WAND_CORE), input.get(MITMDataComponents.WAND_CORE));
        } else if (this.stack.has(MITMDataComponents.WAND) && input.has(MITMDataComponents.WAND)) {
            WandComponent thisWand = this.stack.get(MITMDataComponents.WAND);
            WandComponent inputWand = input.get(MITMDataComponents.WAND);
            // noinspection DataFlowIssue
            return thisWand.poweredUp() == inputWand.poweredUp() &&
                    thisWand.primary().equals(inputWand.primary()) &&
                    thisWand.secondary().equals(inputWand.secondary());
        }
        return false;
    }
}

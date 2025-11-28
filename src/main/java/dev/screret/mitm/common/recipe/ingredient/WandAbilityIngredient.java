package dev.screret.mitm.common.recipe.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;

import dev.screret.mitm.api.ability.WandAbilityInstance;
import dev.screret.mitm.common.item.component.WandComponent;
import dev.screret.mitm.data.MITMDataComponents;
import dev.screret.mitm.data.MITMIngredientTypes;
import dev.screret.mitm.data.MITMItems;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class WandAbilityIngredient implements ICustomIngredient {

    // spotless:off
    public static final MapCodec<WandAbilityIngredient> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            WandAbilityInstance.CODEC.fieldOf("main_ability").forGetter(val -> val.ability),
            WandAbilityInstance.CODEC.optionalFieldOf("crouch_ability").forGetter(val -> Optional.ofNullable(val.crouchAbility)),
            ItemStack.ITEM_NON_AIR_CODEC.fieldOf("item").forGetter(val -> val.stack.getItemHolder()),
            Codec.BOOL.optionalFieldOf("powered_up", false).forGetter(val -> val.poweredUp)
    ).apply(instance, (main, crouchOptional, item, poweredUp) ->
            new WandAbilityIngredient(main, crouchOptional.orElse(null), item.value(), poweredUp)));
    // spotless:on

    private final WandAbilityInstance ability;
    @Nullable
    private final WandAbilityInstance crouchAbility;

    @Getter
    private final boolean poweredUp;
    @Getter
    private final ItemStack stack;

    public WandAbilityIngredient(WandAbilityInstance primary, @Nullable WandAbilityInstance secondary, Item item, boolean poweredUp) {
        this.ability = primary;
        this.stack = item.getDefaultInstance();
        if (item == MITMItems.WAND_CORE.get()) {
            this.stack.set(MITMDataComponents.WAND_CORE, primary);
        } else {
            this.stack.set(MITMDataComponents.WAND, new WandComponent(primary, Optional.ofNullable(secondary), poweredUp));
        }

        this.crouchAbility = secondary;
        this.poweredUp = poweredUp;
    }

    @Override
    public @NotNull Stream<ItemStack> getItems() {
        return Stream.of(stack);
    }

    @Nullable
    public static WandAbilityIngredient fromStack(ItemStack stack) {
        if (stack.has(MITMDataComponents.WAND)) {
            WandComponent component = stack.get(MITMDataComponents.WAND);
            //noinspection DataFlowIssue
            return new WandAbilityIngredient(component.primary(), component.secondaryOrNull(), stack.getItem(), component.poweredUp());
        } else if (stack.has(MITMDataComponents.WAND_CORE)) {
            WandAbilityInstance component = stack.get(MITMDataComponents.WAND_CORE);
            return new WandAbilityIngredient(component, null, stack.getItem(), false);
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
            //noinspection DataFlowIssue
            return thisWand.poweredUp() == inputWand.poweredUp() &&
                    thisWand.primary().equals(inputWand.primary()) &&
                    thisWand.secondary().equals(inputWand.secondary());
        }
        return false;
    }
}

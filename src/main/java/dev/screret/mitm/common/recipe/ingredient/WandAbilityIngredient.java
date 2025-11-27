package dev.screret.mitm.common.recipe.ingredient;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.api.capability.ability.CapabilityWandAbility;
import dev.screret.mitm.api.wand.ability.WandAbilityInstance;
import dev.screret.mitm.api.registry.MITMRegistries;
import dev.screret.mitm.data.MITMItems;
import dev.screret.mitm.common.item.WandCoreItem;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

public class WandAbilityIngredient extends Ingredient {
    public static final Codec<WandAbilityIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WandAbilityInstance.CODEC.fieldOf("main_ability").forGetter(val -> val.ability),
            WandAbilityInstance.CODEC.optionalFieldOf("crouch_ability").forGetter(val -> Optional.ofNullable(val.crouchAbility)),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(val -> val.item),
            Codec.BOOL.optionalFieldOf("powered_up", false).forGetter(val -> val.isPoweredUp)
    ).apply(instance, (main, crouchOptional, item, poweredUp) -> new WandAbilityIngredient(main, crouchOptional.orElse(null), item, poweredUp)));

    private final WandAbilityInstance ability;
    @Nullable
    private final WandAbilityInstance crouchAbility;

    private final boolean isPoweredUp;
    private final Item item;

    public WandAbilityIngredient(WandAbilityInstance ability, Item item, boolean isPoweredUp) {
        super(Stream.of(new ItemValue(item.getDefaultInstance())));
        this.ability = ability;
        this.crouchAbility = null;
        this.item = item;
        this.isPoweredUp = isPoweredUp;
    }

    public WandAbilityIngredient(WandAbilityInstance ability, @Nullable WandAbilityInstance crouchAbility, Item item, boolean isPoweredUp) {
        super(Stream.of(new ItemValue(item.getDefaultInstance())));
        this.ability = ability;
        this.item = item;
        this.crouchAbility = crouchAbility;
        this.isPoweredUp = isPoweredUp;
    }

    public static WandAbilityIngredient fromCapability(CapabilityWandAbility cap, Item item) {
        return new WandAbilityIngredient(cap.getMainAbility(), cap.getCrouchAbility(), item, cap.getPoweredUp());
    }

    @Nullable
    public static WandAbilityIngredient fromStack(ItemStack stack) {
        if (stack.getCapability(CapabilityWandAbility.WAND_ABILITY) != null) {
            var cap = stack.getCapability(CapabilityWandAbility.WAND_ABILITY);
            return new WandAbilityIngredient(cap.getMainAbility(), cap.getCrouchAbility(), stack.getItem(), cap.getPoweredUp());
        } else if (stack.getTag().contains("wand_ability")) {
            // For datagen as capabilities are not loaded for some reason.
            var cap = CapabilityWandAbility.wandAbility(stack);
            return new WandAbilityIngredient(cap.getMainAbility(), cap.getCrouchAbility(), stack.getItem(), cap.getPoweredUp());
        } else if (stack.is(MITMItems.WAND_CORE.get())) {
            return new WandAbilityIngredient(new WandAbilityInstance(MITMRegistries.WAND_ABILITIES.get(ResourceLocation.parse(stack.getTag().getString(WandCoreItem.ABILITY_KEY)))), null, stack.getItem(), false);
        }
        return null;
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    public ItemStack getStack() {
        return MITMUtil.createWand(this.item, this.ability, this.crouchAbility);
    }

    public boolean test(@Nullable ItemStack input) {
        if (input == null)
            return false;
        boolean isCorrectItem = this.item == input.getItem();
        if (this.item == MITMItems.WAND.get()) {
            boolean hasCorrectAbility;
            if (input.getCapability(CapabilityWandAbility.WAND_ABILITY) != null) {
                var cap = input.getCapability(CapabilityWandAbility.WAND_ABILITY);
                hasCorrectAbility = cap.getMainAbility().equals(this.ability) && cap.getCrouchAbility().equals(this.crouchAbility) && cap.getPoweredUp() == this.isPoweredUp;
            } else {
                return false;
            }
            return isCorrectItem && hasCorrectAbility;
        } else if (this.item == MITMItems.WAND_CORE.get()) {
            if (input.getTag().contains(WandCoreItem.ABILITY_KEY, Tag.TAG_COMPOUND)) {
                return new WandAbilityInstance(input.getTag().getCompound(WandCoreItem.ABILITY_KEY)).equals(ability);
            }
        }
        return false;
    }
}

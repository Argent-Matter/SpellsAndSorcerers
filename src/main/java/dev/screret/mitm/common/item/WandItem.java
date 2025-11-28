package dev.screret.mitm.common.item;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.common.item.component.WandComponent;
import dev.screret.mitm.data.MITMDataComponents;
import dev.screret.mitm.data.MITMWandAbilities;
import dev.screret.mitm.api.ability.WandAbilityInstance;
import dev.screret.mitm.data.MITMAttachmentTypes;
import dev.screret.mitm.config.MITMConfig;
import dev.screret.mitm.data.MITMEnchantments;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WandItem extends Item {

    public WandItem() {
        super(new Properties().durability(320).rarity(Rarity.UNCOMMON));
    }

    @Override
    public ItemStack getDefaultInstance() {
        return MITMUtil.createWand(MITMWandAbilities.DUMMY.get(), null);
    }

    @Override
    public Component getName(ItemStack stack) {
        MutableComponent name = Component.translatable(getDescriptionId());
        if (!stack.has(MITMDataComponents.WAND)) {
            return name.append(Component.translatable("ability.mitm.dummy"));
        }

        WandComponent component = stack.get(MITMDataComponents.WAND);

        var current = component.primary();
        name = name.append(Component.translatable(current.getId().toLanguageKey("ability")));

        while (!current.getChildren().isEmpty()) {
            current = current.getChildren().getFirst();
            name = name.append(Component.translatable("tooltip.mitm.joiner"))
                    .append(Component.translatable(current.getId().toLanguageKey("ability")));
        }
        return name;
    }

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return super.supportsEnchantment(stack, enchantment) ||
                enchantment.is(Enchantments.QUICK_CHARGE) ||
                enchantment.is(Enchantments.POWER);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResultHolder<ItemStack> reference = InteractionResultHolder.fail(itemstack);

        if (!itemstack.has(MITMDataComponents.WAND)) {
            return reference;
        }
        WandComponent component = itemstack.get(MITMDataComponents.WAND);
        if (player.isCrouching() && component.secondary().isPresent()) {
            var crouchAbility = component.secondary().get();
            if ((crouchAbility.isChargeable() || crouchAbility.isHoldable()) && !player.isUsingItem()) {
                player.startUsingItem(hand);
                reference = InteractionResultHolder.consume(itemstack);
            } else {
                reference = execute(level, player, itemstack, 0, component);
            }
        } else {
            var ability = component.primary();
            if ((ability.isChargeable() || ability.isHoldable()) && !player.isUsingItem()) {
                player.startUsingItem(hand);
                reference = InteractionResultHolder.consume(itemstack);
            } else {
                reference = execute(level, player, itemstack, 0, component);
            }
        }
        return reference;
    }

    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, int timeCharged, WandComponent component) {
        Item currentItem = stack.getItem();
        var returnValue = InteractionResultHolder.fail(stack);
        if (currentItem instanceof WandItem) {
            if (!deductManaFromUser(user, stack, timeCharged))
                return returnValue;

            if (user.isCrouching() && component.secondary().isPresent()) {
                returnValue = component.secondary().get().execute(level, user, stack, new WandAbilityInstance.WrappedVec3(user.getEyePosition()), timeCharged);
                if (user instanceof Player player) {
                    player.getCooldowns().addCooldown(currentItem, component.secondary().get().getAbility().getCooldownDuration());
                }
            } else {
                returnValue = component.primary().execute(level, user, stack, new WandAbilityInstance.WrappedVec3(user.getEyePosition()), timeCharged);
                if (user instanceof Player player) {
                    player.getCooldowns().addCooldown(currentItem, component.primary().getAbility().getCooldownDuration());
                }
            }
        }
        return returnValue;
    }


    public boolean deductManaFromUser(LivingEntity user, ItemStack stack, int timeCharged) {
        if (user instanceof Player player && player.isCreative())
            return true;
        if (user.hasData(MITMAttachmentTypes.MANA) && MITMConfig.Server.useMana.get()) {
            var manaCap = user.getData(MITMAttachmentTypes.MANA);
            var manaToDeduct = 1 + timeCharged / 4 * (6 - stack.getEnchantmentLevel(MITMEnchantments.MANA_EFFICIENCY));
            if (manaCap.getMana() < manaToDeduct) {
                return false;
            }
            manaCap.deductMana(manaToDeduct, false);
        }
        return true;
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int usageTicks) {
        if (level.isClientSide) {
            return;
        }
        if (!stack.has(MITMDataComponents.WAND)) {
            return;
        }
        WandComponent cap = stack.get(MITMDataComponents.WAND);
        if (cap.primary().isHoldable() || (cap.secondary().isPresent() && cap.secondary().get().isHoldable())) {
            this.execute(level, user, stack, usageTicks, cap);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!stack.has(MITMDataComponents.WAND)) {
            return;
        }
        WandComponent cap = stack.get(MITMDataComponents.WAND);
        int useDuration = cap.primary().getUseDuration();
        if (useDuration > 0) {
            if (cap.primary().isChargeable() || (cap.secondary().isPresent() && cap.secondary().get().isChargeable()))
                this.execute(level, entity, stack, useDuration - timeLeft, cap);
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CUSTOM;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        if (stack.has(MITMDataComponents.WAND)) {
            return stack.get(MITMDataComponents.WAND).primary().getUseDuration();
        }
        return 0;
    }
}

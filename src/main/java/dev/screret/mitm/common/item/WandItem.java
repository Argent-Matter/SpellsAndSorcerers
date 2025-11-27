package dev.screret.mitm.common.item;

import net.minecraft.network.chat.Component;
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
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.data.MITMWandAbilities;
import dev.screret.mitm.api.capability.ability.CapabilityWandAbility;
import dev.screret.mitm.api.wand.ability.WandAbilityInstance;
import dev.screret.mitm.data.MITMAttachmentTypes;
import dev.screret.mitm.client.model.item.WandItemClientExtensions;
import dev.screret.mitm.config.MITMConfig;
import dev.screret.mitm.data.MITMEnchantments;

import java.util.function.Consumer;

public class WandItem extends Item {

    private static final String WAND_LANG_KEY = "item.mitm.wand";


    public WandItem() {
        super(new Properties().durability(320).rarity(Rarity.UNCOMMON));
    }

    @Override
    public ItemStack getDefaultInstance() {
        return MITMUtil.createWand(MITMWandAbilities.DUMMY.get(), null);
    }

    @Override
    public Component getName(ItemStack stack) {
        String name = "item.mitm.basic";
        if (stack.getCapability(CapabilityWandAbility.WAND_ABILITY) != null) {
            var cap = stack.getCapability(CapabilityWandAbility.WAND_ABILITY);
            var current = cap.getMainAbility();
            while (current.getChildren() != null && current.getChildren().size() > 0) {
                current = cap.getMainAbility().getChildren().get(0);
            }
            name = current.getId().toLanguageKey("ability");
        }
        return Component.translatable(WAND_LANG_KEY, Component.translatable(name));
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return super.canApplyAtEnchantingTable(stack, enchantment) || enchantment == Enchantments.QUICK_CHARGE || enchantment == Enchantments.POWER_ARROWS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResultHolder<ItemStack> reference = InteractionResultHolder.fail(itemstack);
        if (itemstack.getCapability(CapabilityWandAbility.WAND_ABILITY) != null) {
            var cap = itemstack.getCapability(CapabilityWandAbility.WAND_ABILITY);
            if (player.isCrouching() && cap.getCrouchAbility() != null) {
                var crouchAbility = cap.getCrouchAbility();
                if ((crouchAbility.isChargeable() || crouchAbility.isHoldable()) && !player.isUsingItem()) {
                    player.startUsingItem(hand);
                    reference = InteractionResultHolder.consume(itemstack);
                } else {
                    reference = execute(level, player, itemstack, 0, cap);
                }
            } else {
                var ability = cap.getMainAbility();
                if ((ability.isChargeable() || ability.isHoldable()) && !player.isUsingItem()) {
                    player.startUsingItem(hand);
                    reference = InteractionResultHolder.consume(itemstack);
                } else {
                    reference = execute(level, player, itemstack, 0, cap);
                }
            }
        }
        return reference;
    }

    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, int timeCharged, CapabilityWandAbility cap) {
        Item currentItem = stack.getItem();
        var returnValue = InteractionResultHolder.fail(stack);
        if (currentItem instanceof WandItem) {
            if (!deductManaFromUser(user, stack, timeCharged))
                return returnValue;

            if (user.isCrouching() && cap.getCrouchAbility() != null) {
                returnValue = cap.getCrouchAbility().execute(level, user, stack, new WandAbilityInstance.WrappedVec3(user.getEyePosition()), timeCharged);
                if (user instanceof Player player) {
                    player.getCooldowns().addCooldown(currentItem, cap.getCrouchAbility().getAbility().getCooldownDuration());
                }
            } else {
                returnValue = cap.getMainAbility().execute(level, user, stack, new WandAbilityInstance.WrappedVec3(user.getEyePosition()), timeCharged);
                if (user instanceof Player player) {
                    player.getCooldowns().addCooldown(currentItem, cap.getMainAbility().getAbility().getCooldownDuration());
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
            var manaToDeduct = 1 + timeCharged / 4 * (6 - stack.getEnchantmentLevel(MITMEnchantments.MANA_EFFICIENCY.get()));
            if (manaCap.getManaStored() < manaToDeduct)
                return false;
            manaCap.deductMana(manaToDeduct, false);
        }
        return true;
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int usageTicks) {
        if (!level.isClientSide) {
            if (stack.getCapability(CapabilityWandAbility.WAND_ABILITY) != null) {
                var cap = stack.getCapability(CapabilityWandAbility.WAND_ABILITY);
                if (cap.getMainAbility().isHoldable() || (cap.getCrouchAbility() != null && cap.getCrouchAbility().isHoldable()))
                    this.execute(level, user, stack, usageTicks, cap);
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (stack.getCapability(CapabilityWandAbility.WAND_ABILITY) != null) {
            var cap = stack.getCapability(CapabilityWandAbility.WAND_ABILITY);
            var useDuration = cap.getMainAbility().getUseDuration();
            if (useDuration > 0) {
                if (cap.getMainAbility().isChargeable() || (cap.getCrouchAbility() != null && cap.getCrouchAbility().isChargeable()))
                    this.execute(level, entity, stack, useDuration - timeLeft, cap);
            }
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.CUSTOM;
    }

    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new WandItemClientExtensions());
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        if (stack.getCapability(CapabilityWandAbility.WAND_ABILITY) != null) {
            return stack.getCapability(CapabilityWandAbility.WAND_ABILITY).getMainAbility().getUseDuration();
        }
        return 0;
    }
}

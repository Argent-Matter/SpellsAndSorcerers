package dev.screret.mitm.common.item;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.api.ability.WandAbilityInstance;
import dev.screret.mitm.common.item.component.WandComponent;
import dev.screret.mitm.config.MITMConfig;
import dev.screret.mitm.data.MITMAttachmentTypes;
import dev.screret.mitm.data.MITMDataComponents;
import dev.screret.mitm.data.MITMWandAbilities;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
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

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WandItem extends Item {

    public static final Component JOINER = Component.translatable("tooltip.mitm.joiner");
    public static final Component JOINER_LAST = Component.translatable("tooltip.mitm.joiner.last");

    public WandItem() {
        super(new Properties().durability(320).rarity(Rarity.UNCOMMON));
    }

    @Override
    public ItemStack getDefaultInstance() {
        return MITMUtil.createWand(MITMWandAbilities.DUMMY.get(), null);
    }

    @Override
    public Component getName(ItemStack stack) {
        WandComponent component = stack.get(MITMDataComponents.WAND);
        if (component == null) {
            return super.getName(stack).copy().append(Component.translatable("ability.mitm.dummy"));
        }
        Component abilityTranslation = addNamePart(component.primary());
        return Component.translatable(this.getDescriptionId(stack), abilityTranslation);
    }

    protected Component addNamePart(WandAbilityInstance ability) {
        MutableComponent name = Component.empty();

        String langKey = ability.getId().toLanguageKey("ability");
        if (Language.getInstance().has(langKey)) {
            name = Component.translatable(langKey);
        }

        var children = ability.getChildren();
        if (!children.isEmpty()) {
            if (children.size() == 1) {
                name.append(CommonComponents.SPACE).append(addNamePart(children.getFirst()));
            } else if (children.size() == 2) {
                name.append(addNamePart(children.getFirst()))
                        .append(JOINER_LAST)
                        .append(addNamePart(children.getLast()));
            } else {
                int lastIdx = children.size() - 1;
                for (int i = 0; i < children.size(); i++) {
                    if (i > 0 && i < lastIdx) name.append(JOINER);
                    if (i == lastIdx) name.append(JOINER_LAST);

                    name.append(addNamePart(children.get(i)));
                }
            }
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

        WandComponent component = itemstack.get(MITMDataComponents.WAND);
        if (component == null) {
            return reference;
        }
        if (player.isSecondaryUseActive() && component.secondary().isPresent()) {
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

    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack,
                                                      int timeCharged, WandComponent component) {
        Item currentItem = stack.getItem();
        var returnValue = InteractionResultHolder.fail(stack);
        if (currentItem instanceof WandItem) {
            if (!deductManaFromUser(user, stack, timeCharged))
                return returnValue;

            if (user.isShiftKeyDown() && component.secondary().isPresent()) {
                returnValue = component.secondary().get().execute(level, user, stack,
                        new WandAbilityInstance.WrappedVec3(user.getEyePosition()), timeCharged);
                if (user instanceof Player player) {
                    player.getCooldowns().addCooldown(currentItem,
                            component.secondary().get().getAbility().getCooldownDuration());
                }
            } else {
                returnValue = component.primary().execute(level, user, stack,
                        new WandAbilityInstance.WrappedVec3(user.getEyePosition()), timeCharged);
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
            var manaToDeduct = 1 + timeCharged / 4 * (6 - 0 /* stack.getEnchantmentLevel(MITMEnchantments.MANA_EFFICIENCY) */);
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
        WandComponent component = stack.get(MITMDataComponents.WAND);
        if (component == null) {
            return;
        }
        if (component.primary().isHoldable() || (component.secondary().isPresent() && component.secondary().get().isHoldable())) {
            this.execute(level, user, stack, usageTicks, component);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        WandComponent component = stack.get(MITMDataComponents.WAND);
        if (component == null) {
            return;
        }
        WandAbilityInstance ability = component.primary();
        if (ability.isChargeable() && ability.getUseDuration() > 0) {
            this.execute(level, entity, stack, ability.getUseDuration() - timeLeft, component);
        } else {
            ability = component.secondaryOrNull();
            if (ability != null && ability.isChargeable() && ability.getUseDuration() > 0) {
                this.execute(level, entity, stack, ability.getUseDuration() - timeLeft, component);
            }
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CUSTOM;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        WandComponent component = stack.get(MITMDataComponents.WAND);
        if (component != null) {
            return component.primary().getUseDuration();
        }
        return 0;
    }
}

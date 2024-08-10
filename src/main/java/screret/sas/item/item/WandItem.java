package screret.sas.item.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import screret.sas.ability.ModWandAbilities;
import screret.sas.api.capability.ability.ICapabilityWandAbility;
import screret.sas.api.capability.ability.WandAbilityProvider;
import screret.sas.api.capability.mana.ManaProvider;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;
import screret.sas.client.model.item.WandItemClientExtensions;
import screret.sas.config.SASConfig;
import screret.sas.enchantment.ModEnchantments;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class WandItem extends Item {

    private static final String WAND_LANG_KEY = "item.sas.wand";


    public WandItem() {
        super(new Properties().durability(320).rarity(Rarity.UNCOMMON));
    }

    @Override
    public Component getName(ItemStack stack) {
        String name = "item.sas.basic";
        if(stack.getCapability(WandAbilityProvider.WAND_ABILITY).isPresent()) {
            var cap = stack.getCapability(WandAbilityProvider.WAND_ABILITY).resolve().get();
            var current = cap.getMainAbility();
            while (current.getChildren() != null && current.getChildren().size() > 0) {
                current = cap.getMainAbility().getChildren().get(0);
            }
            name = "ability.sas." + current.getId().getPath();
        }
        return Component.translatable(WAND_LANG_KEY, Component.translatable(name));
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        if (WandAbilityProvider.WAND_ABILITY != null && nbt != null) {
            var main = new WandAbilityInstance(nbt.getCompound(WandAbility.BASIC_ABILITY_KEY));

            WandAbilityInstance crouch = null;
            if(nbt.contains(WandAbility.CROUCH_ABILITY_KEY, Tag.TAG_COMPOUND)) {
                crouch = new WandAbilityInstance(nbt.getCompound(WandAbility.CROUCH_ABILITY_KEY));
            }

            if(crouch == null && main.getAbility() != null && main.getAbility().equals(ModWandAbilities.HEAL.get())) {
                crouch = new WandAbilityInstance(ModWandAbilities.HEAL_SELF.get());
            }
            boolean isPoweredUp = false;
            if(nbt.contains(WandAbility.POWERED_UP_KEY, Tag.TAG_ANY_NUMERIC)) {
                isPoweredUp = nbt.getBoolean(WandAbility.POWERED_UP_KEY);
            }
            return new WandAbilityProvider(main, crouch, isPoweredUp);
        }
        return super.initCapabilities(stack, nbt);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        InteractionResultHolder<ItemStack> reference = InteractionResultHolder.fail(itemstack);
        if( itemstack.getCapability(WandAbilityProvider.WAND_ABILITY).isPresent()) {
            var cap = itemstack.getCapability(WandAbilityProvider.WAND_ABILITY).resolve().get();
            if(player.isCrouching() && cap.getCrouchAbility() != null) {
                var crouchAbility = cap.getCrouchAbility();
                if((crouchAbility.isChargeable() || crouchAbility.isHoldable()) && !player.isUsingItem()) {
                    player.startUsingItem(hand);
                    reference = InteractionResultHolder.consume(itemstack);
                }else{
                    reference = execute(level, player, itemstack, 0, cap);
                }
            }else{
                var ability = cap.getMainAbility();
                if((ability.isChargeable() || ability.isHoldable()) && !player.isUsingItem()) {
                    player.startUsingItem(hand);
                    reference = InteractionResultHolder.consume(itemstack);
                }else{
                    reference = execute(level, player, itemstack, 0, cap);
                }
            }
        }
        return reference;
    }

    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, int timeCharged, ICapabilityWandAbility cap) {
        Item currentItem = stack.getItem();
        var returnValue = InteractionResultHolder.fail(stack);
        if(currentItem instanceof WandItem) {
            if(!deductManaFromUser(user, stack, timeCharged)) return returnValue;

            if(user.isCrouching() && cap.getCrouchAbility() != null) {
                returnValue = cap.getCrouchAbility().execute(level, user, stack, new WandAbilityInstance.Vec3Wrapped(user.getEyePosition()), timeCharged);
                if(user instanceof Player player) {
                    player.getCooldowns().addCooldown(currentItem, cap.getCrouchAbility().getAbility().getCooldownDuration());
                }
            } else {
                returnValue = cap.getMainAbility().execute(level, user, stack, new WandAbilityInstance.Vec3Wrapped(user.getEyePosition()), timeCharged);
                if(user instanceof Player player) {
                    player.getCooldowns().addCooldown(currentItem, cap.getMainAbility().getAbility().getCooldownDuration());
                }
            }
        }
        return returnValue;
    }


    public boolean deductManaFromUser(LivingEntity user, ItemStack stack, int timeCharged) {
        if(user instanceof Player player && player.isCreative()) return true;
        if(user.getCapability(ManaProvider.MANA).isPresent() && SASConfig.Server.useMana.get()) {
            var manaCap = user.getCapability(ManaProvider.MANA).resolve().get();
            var manaToDeduct = 1 + timeCharged / 4 * (6 - stack.getEnchantmentLevel(ModEnchantments.MANA_EFFICIENCY.get()));
            if(manaCap.getManaStored() < manaToDeduct) return false;
            manaCap.deductMana(manaToDeduct, false);
        }
        return true;
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack, int usageTicks) {
        if(!level.isClientSide) {
            if(stack.getCapability(WandAbilityProvider.WAND_ABILITY).isPresent()) {
                var cap = stack.getCapability(WandAbilityProvider.WAND_ABILITY).resolve().get();
                if(cap.getMainAbility().isHoldable() || (cap.getCrouchAbility() != null && cap.getCrouchAbility().isHoldable()))  this.execute(level, user, stack, usageTicks, cap);
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        stack.getCapability(WandAbilityProvider.WAND_ABILITY).ifPresent(var -> {
            var useDuration = var.getMainAbility().getUseDuration();
            if(useDuration > 0) {
                if(var.getMainAbility().isChargeable() || (var.getCrouchAbility() != null && var.getCrouchAbility().isChargeable())) this.execute(level, entity, stack, useDuration - timeLeft, var);
            }
        });
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
        if(stack.getCapability(WandAbilityProvider.WAND_ABILITY).isPresent()) {
            return stack.getCapability(WandAbilityProvider.WAND_ABILITY).resolve().get().getMainAbility().getUseDuration();
        }
        return 0;
    }
}

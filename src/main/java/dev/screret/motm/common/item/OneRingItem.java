package dev.screret.motm.common.item;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.common.data.util.LangUtil;
import dev.screret.motm.data.MOTMEntityTypes;
import dev.screret.motm.mixin.accessor.ItemEntityAccessor;

import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;

import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class OneRingItem extends Item implements ICurioItem {

    public OneRingItem() {
        super(new Properties().stacksTo(1)
                .component(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY.withTooltip(false))
                .attributes(ItemAttributeModifiers.builder()
                        .add(Attributes.LUCK,
                                new AttributeModifier(MOTMUtil.id("the_one_ring"), -5.0f, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.ANY)
                        .add(Attributes.MOVEMENT_SPEED,
                                new AttributeModifier(MOTMUtil.id("the_one_ring"), 1.0f, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.ANY)
                        .add(Attributes.SNEAKING_SPEED,
                                new AttributeModifier(MOTMUtil.id("the_one_ring"), 0.3f, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.ANY)
                        .add(Attributes.STEP_HEIGHT,
                                new AttributeModifier(MOTMUtil.id("the_one_ring"), 0.4f, AttributeModifier.Operation.ADD_VALUE),
                                EquipmentSlotGroup.ANY)
                        .build().withTooltip(false))
                .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true));
    }

    public void tick(ItemStack stack, Level level, Entity entity) {
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        livingEntity.setInvisible(true);
    }

    @Override
    public void curioTick(SlotContext slotContext, ItemStack stack) {
        tick(stack, slotContext.entity().level(), slotContext.entity());
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        tick(stack, level, entity);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context,
                                List<Component> tooltip, TooltipFlag tooltipFlag) {
        tooltip.add(LangUtil.getFromMultiline("item.motm.the_one_ring.tooltip", 0)
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
        tooltip.add(LangUtil.getFromMultiline("item.motm.the_one_ring.tooltip", 1)
                .withStyle(ChatFormatting.DARK_RED));
    }

    @Override
    public List<Component> getAttributesTooltip(List<Component> tooltips, TooltipContext context, ItemStack stack) {
        return new ArrayList<>();
    }

    @Override
    public boolean onDroppedByPlayer(ItemStack item, Player player) {
        return false;
    }

    @Override
    public boolean canEquip(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canEquipFromUse(SlotContext slotContext, ItemStack stack) {
        return true;
    }

    @Override
    public boolean canUnequip(SlotContext slotContext, ItemStack stack) {
        return false;
    }

    @Override
    public @NotNull ICurio.DropRule getDropRule(SlotContext slotContext, DamageSource source,
                                                boolean recentlyHit, ItemStack stack) {
        return ICurio.DropRule.ALWAYS_KEEP;
    }

    @Override
    public boolean isEnderMask(ItemStack stack, Player player, EnderMan enderMan) {
        return true;
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return true;
    }

    @Override
    public boolean canBeHurtBy(ItemStack stack, DamageSource source) {
        return source.is(DamageTypes.LAVA);
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public @Nullable Entity createEntity(Level level, Entity original, ItemStack stack) {
        return RingItemEntity.fromDroppedItem(level, (ItemEntity) original, stack);
    }

    public static class RingItemEntity extends ItemEntity {

        public RingItemEntity(EntityType<RingItemEntity> entityType, Level level) {
            super(entityType, level);
        }

        @Override
        public boolean fireImmune() {
            return false;
        }

        @Override
        public boolean isOnFire() {
            // disable the fire effect
            return false;
        }

        @Override
        public boolean isInvulnerableTo(DamageSource source) {
            return source.is(DamageTypeTags.IS_EXPLOSION) || super.isInvulnerableTo(source);
        }

        public static @Nullable RingItemEntity fromDroppedItem(Level level, ItemEntity original, ItemStack stack) {
            // copy the original item entity's info over
            RingItemEntity newEntity = MOTMEntityTypes.RING_ITEM.get().create(level);
            if (newEntity == null) {
                return null;
            }

            newEntity.setPos(original.position());
            newEntity.setDeltaMovement(original.getDeltaMovement());
            newEntity.setItem(stack);

            Entity thrower = original.getOwner();
            if (thrower != null) {
                newEntity.setThrower(thrower);
            }
            newEntity.setTarget(original.getTarget());
            newEntity.setPickUpDelay(((ItemEntityAccessor) original).getPickupDelay());

            // also make it never despawn
            newEntity.setUnlimitedLifetime();

            return newEntity;
        }
    }
}

package dev.screret.motm.common.item;

import dev.screret.mitm.client.renderer.armor.SoulsteelArmorRenderer;
import dev.screret.motm.config.MOTMConfig;
import dev.screret.motm.data.MOTMItems;
import dev.screret.motm.data.MOTMMobEffects;

import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.client.GeoRenderProvider;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.Consumer;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MOTMArmorItem extends ArmorItem implements GeoItem {

    public static final MobEffectInstance SOUL_STEEL_EFFECT = new MobEffectInstance(MOTMMobEffects.MANA, 200, 1);

    private final MobEffectInstance fullSetEffect;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MOTMArmorItem(Holder<ArmorMaterial> material, MobEffectInstance fullSetEffect, ArmorItem.Type slot,
                         Properties builder) {
        super(material, slot, builder);
        this.fullSetEffect = fullSetEffect;
    }

    @Override
    public void createGeoRenderer(Consumer<GeoRenderProvider> consumer) {
        consumer.accept(new GeoRenderProvider() {

            private GeoArmorRenderer<?> renderer;

            @Override
            public <T extends LivingEntity> HumanoidModel<?> getGeoArmorRenderer(@Nullable T entity, ItemStack stack,
                                                                                 @Nullable EquipmentSlot slot,
                                                                                 @Nullable HumanoidModel<T> original) {
                if (this.renderer == null) this.renderer = new SoulsteelArmorRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        // only the chestplate actually applies effects, but the whole set is needed anyway so it's OK
        if (level.isClientSide || !stack.is(MOTMItems.SOULSTEEL_CHESTPLATE) ||
                MOTMConfig.Server.armorGiveEffects.getAsBoolean()) {
            return;
        }
        if (!(entity instanceof LivingEntity livingEntity)) {
            return;
        }
        if (slotId >= Inventory.INVENTORY_SIZE && slotId < Inventory.SLOT_OFFHAND &&
                hasCorrectArmorOn(this.material, livingEntity)) {
            livingEntity.addEffect(new MobEffectInstance(this.fullSetEffect));
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                DefaultAnimations.genericIdleController(this));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private boolean hasCorrectArmorOn(Holder<ArmorMaterial> material, LivingEntity entity) {
        for (ItemStack stack : entity.getArmorSlots()) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ArmorItem armor) || armor.getMaterial() != material) {
                return false;
            }
        }
        return true;
    }
}

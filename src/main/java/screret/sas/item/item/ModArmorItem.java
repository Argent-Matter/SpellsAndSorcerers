package screret.sas.item.item;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import screret.sas.alchemy.effect.ModMobEffects;
import screret.sas.client.renderer.armor.SoulsteelArmorRenderer;
import screret.sas.config.SASConfig;
import software.bernie.example.client.renderer.armor.GeckoArmorRenderer;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class ModArmorItem extends ArmorItem implements GeoItem {
    public static final MobEffectInstance SOUL_STEEL_EFFECT = new MobEffectInstance(ModMobEffects.MANA.get(), 200, 1);

    private final MobEffectInstance fullSetEffect;
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ModArmorItem(ArmorMaterial material, MobEffectInstance fullSetEffect, ArmorItem.Type slot, Properties builder) {
        super(material, slot, builder);
        this.fullSetEffect = fullSetEffect;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<?> original) {
                if (this.renderer == null)
                    this.renderer = new SoulsteelArmorRenderer();

                // This prepares our GeoArmorRenderer for the current render frame.
                // These parameters may be null however, so we don't do anything further with them
                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return this.renderer;
            }
        });
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        if (!world.isClientSide()) {
            if (SASConfig.Server.armorGiveEffects.get()) {
                if (hasFullSuitOfArmorOn(player)) {
                    evaluateArmorEffects(player);
                }
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
                DefaultAnimations.genericIdleController(this)
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    private void evaluateArmorEffects(Player player) {
        if (hasCorrectArmorOn(this.material, player)) {
            addStatusEffectForMaterial(player, this.material, this.fullSetEffect);
        }
    }

    private void addStatusEffectForMaterial(Player player, ArmorMaterial mapArmorMaterial, MobEffectInstance mapStatusEffect) {
        boolean hasPlayerEffect = player.hasEffect(mapStatusEffect.getEffect());

        if (hasCorrectArmorOn(mapArmorMaterial, player) && !hasPlayerEffect) {
            player.addEffect(new MobEffectInstance(mapStatusEffect.getEffect(),
                    mapStatusEffect.getDuration(), mapStatusEffect.getAmplifier()));
        }
    }

    private boolean hasFullSuitOfArmorOn(Player player) {
        ItemStack boots = player.getInventory().getArmor(0);
        ItemStack leggings = player.getInventory().getArmor(1);
        ItemStack breastplate = player.getInventory().getArmor(2);
        ItemStack helmet = player.getInventory().getArmor(3);

        return !helmet.isEmpty() && !breastplate.isEmpty()
                && !leggings.isEmpty() && !boots.isEmpty();
    }

    private boolean hasCorrectArmorOn(ArmorMaterial material, Player player) {
        ArmorItem boots = (ArmorItem) player.getInventory().getArmor(0).getItem();
        ArmorItem leggings = (ArmorItem) player.getInventory().getArmor(1).getItem();
        ArmorItem breastplate = (ArmorItem) player.getInventory().getArmor(2).getItem();
        ArmorItem helmet = (ArmorItem) player.getInventory().getArmor(3).getItem();

        return helmet.getMaterial() == material && breastplate.getMaterial() == material &&
                leggings.getMaterial() == material && boots.getMaterial() == material;
    }
}

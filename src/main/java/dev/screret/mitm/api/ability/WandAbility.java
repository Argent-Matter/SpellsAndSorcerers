package dev.screret.mitm.api.ability;

import dev.screret.mitm.api.registry.MITMRegistries;
import dev.screret.mitm.common.item.component.WandComponent;
import dev.screret.mitm.data.MITMDataComponents;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;

import java.util.function.Function;

public abstract class WandAbility<T extends WandAbility<T>> {

    public static final Codec<WandAbility<?>> CODEC = MITMRegistries.WAND_ABILITIES.byNameCodec()
            .dispatchStable(Function.identity(), WandAbility::codec);

    @Getter
    private final int useDuration, cooldownDuration;
    private final float damagePerHit;
    @Getter
    private final boolean applyEnchants;
    @Getter
    protected final ParticleOptions particle;

    @Getter
    private final int color;

    public WandAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, ParticleOptions particle,
                       int color) {
        this.useDuration = useDuration;
        this.cooldownDuration = cooldownDuration;
        this.damagePerHit = damagePerHit;
        this.applyEnchants = applyEnchants;
        this.particle = particle;
        this.color = color;
    }

    public abstract MapCodec<T> codec();

    public static <
            W extends WandAbility<?>> Products.P6<RecordCodecBuilder.Mu<W>, Integer, Integer, Float, Boolean, ParticleOptions, Integer> codecStart(RecordCodecBuilder.Instance<W> instance) {
        return instance.group(
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("use_duration").forGetter(WandAbility::getUseDuration),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("cooldown_duration").forGetter(WandAbility::getCooldownDuration),
                Codec.FLOAT.fieldOf("damage_per_hit").forGetter(WandAbility::getBaseDamagePerHit),
                Codec.BOOL.fieldOf("apply_enchants").forGetter(WandAbility::isApplyEnchants),
                ParticleTypes.CODEC.fieldOf("particle").forGetter(WandAbility::getParticle),
                Codec.INT.fieldOf("color").forGetter(WandAbility::getColor));
    }

    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack,
                                                      WandAbilityInstance.WrappedVec3 currentPosition, int timeCharged) {
        return InteractionResultHolder.fail(stack);
    }

    public boolean isHoldable() {
        return false;
    }

    public boolean isChargeable() {
        return false;
    }

    public float getBaseDamagePerHit() {
        return damagePerHit;
    }

    public float getDamagePerHit(ItemStack usedItem, LivingEntity user, DamageSource source) {
        float damage = getBaseDamagePerHit();
        if (isApplyEnchants() && user.level() instanceof ServerLevel serverLevel) {
            damage += EnchantmentHelper.modifyDamage(serverLevel, usedItem, user, source, damage / 5);
        }
        return damage;
    }

    public boolean getPoweredUpMultiplier(ItemStack stack) {
        WandComponent component = stack.get(MITMDataComponents.WAND);
        return component != null && component.poweredUp();
    }

    public ResourceLocation getKey() {
        return MITMRegistries.WAND_ABILITIES.getKey(this);
    }

    @Override
    public String toString() {
        return getKey().toString();
    }

    public WandAbilityInstance getBasicInstance() {
        return new WandAbilityInstance(this);
    }
}

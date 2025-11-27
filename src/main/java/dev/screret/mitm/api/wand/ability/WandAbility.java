package dev.screret.mitm.api.wand.ability;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import dev.screret.mitm.api.capability.ability.CapabilityWandAbility;
import dev.screret.mitm.api.registry.MITMRegistries;

import java.util.function.Function;

public class WandAbility {
    public static final String ABILITIES_KEY = "wand_abilities", MAIN_ABILITY_KEY = "main_ability", CROUCH_ABILITY_KEY = "crouch_ability", POWERED_UP_KEY = "is_powered_up";
    public static final MapCodec<WandAbility> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("use_duration").forGetter(self -> self.useDuration),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("cooldown_duration").forGetter(self -> self.cooldownDuration),
            Codec.FLOAT.fieldOf("damage_per_hit").forGetter(self -> self.damagePerHit),
            Codec.BOOL.fieldOf("apply_enchants").forGetter(self -> self.applyEnchants),
            ParticleTypes.CODEC.fieldOf("particle").forGetter(self -> self.particle),
            Codec.INT.fieldOf("color").forGetter(self -> self.color)
    ).apply(instance, WandAbility::new));
    public static final Codec<WandAbility> DIRECT_CODEC = MITMRegistries.WAND_ABILITIES.byNameCodec().dispatchStable(Function.identity(), WandAbility::codec);

    private final int useDuration, cooldownDuration;
    private final float damagePerHit;
    private final boolean applyEnchants;
    protected final ParticleOptions particle;

    private final int color;

    public WandAbility(int useDuration, int cooldownDuration, float damagePerHit, boolean applyEnchants, ParticleOptions particle, int color) {
        this.useDuration = useDuration;
        this.cooldownDuration = cooldownDuration;
        this.damagePerHit = damagePerHit;
        this.applyEnchants = applyEnchants;
        this.particle = particle;
        this.color = color;
    }

    public Codec<? extends WandAbility> codec() {
        return CODEC.codec();
    }

    public static <W extends WandAbility> Products.P6<RecordCodecBuilder.Mu<W>, Integer, Integer, Float, Boolean, ParticleOptions, Integer> codecStart(RecordCodecBuilder.Instance<W> instance) {
        return instance.group(
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("use_duration").forGetter(WandAbility::getUseDuration),
                ExtraCodecs.NON_NEGATIVE_INT.fieldOf("cooldown_duration").forGetter(WandAbility::getCooldownDuration),
                Codec.FLOAT.fieldOf("damage_per_hit").forGetter(WandAbility::getBaseDamagePerHit),
                Codec.BOOL.fieldOf("apply_enchants").forGetter(WandAbility::isApplyEnchants),
                ParticleTypes.CODEC.fieldOf("particle").forGetter(WandAbility::getParticle),
                Codec.INT.fieldOf("color").forGetter(WandAbility::getColor)
        );
    }

    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, WandAbilityInstance.WrappedVec3 currentPosition, int timeCharged) {
        return InteractionResultHolder.fail(stack);
    }

    public int getUseDuration() {
        return this.useDuration;
    }

    public boolean isHoldable() {
        return false;
    }

    public boolean isApplyEnchants() {
        return applyEnchants;
    }

    public boolean isChargeable() {
        return false;
    }

    public int getCooldownDuration() {
        return this.cooldownDuration;
    }

    public float getBaseDamagePerHit() {
        return damagePerHit;
    }

    public float getDamagePerHit(ItemStack stack) {
        return applyEnchants ? damagePerHit + (damagePerHit / 5) * stack.getEnchantmentLevel(Enchantments.POWER_ARROWS) : damagePerHit;
    }

    public boolean getPoweredUpMultiplier(ItemStack stack) {
        return stack.getCapability(CapabilityWandAbility.WAND_ABILITY) != null && stack.getCapability(CapabilityWandAbility.WAND_ABILITY).getPoweredUp();
    }

    public ResourceLocation getKey() {
        return MITMRegistries.WAND_ABILITIES.getKey(this);
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public String toString() {
        return getKey().toString();
    }

    public ParticleOptions getParticle() {
        return particle;
    }

    public WandAbilityInstance getBasicInstance() {
        return new WandAbilityInstance(this);
    }
}

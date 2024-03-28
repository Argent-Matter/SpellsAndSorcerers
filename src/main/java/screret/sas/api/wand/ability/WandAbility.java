package screret.sas.api.wand.ability;

import com.mojang.serialization.Codec;
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
import screret.sas.api.capability.ability.ICapabilityWandAbility;

public class WandAbility implements IWandAbility {
    public static final  String ABILITIES_KEY = "wand_abilities", MAIN_ABILITY_KEY = "main_ability", CROUCH_ABILITY_KEY = "crouch_ability", POWERED_UP_KEY = "is_powered_up";
    public static final Codec<WandAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("use_duration").forGetter(self -> self.useDuration),
            ExtraCodecs.NON_NEGATIVE_INT.fieldOf("cooldown_duration").forGetter(self -> self.cooldownDuration),
            Codec.FLOAT.fieldOf("damage_per_hit").forGetter(self -> self.damagePerHit),
            Codec.BOOL.fieldOf("apply_enchants").forGetter(self -> self.applyEnchants),
            ParticleTypes.CODEC.fieldOf("particle").forGetter(self -> self.particle),
            Codec.INT.fieldOf("color").forGetter(self -> self.color)
    ).apply(instance, WandAbility::new));

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

    @Override
    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, WandAbilityInstance.WrappedVec3 currentPosition, int timeCharged) {
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public int getUseDuration() {
        return this.useDuration;
    }

    @Override
    public boolean isHoldable() {
        return false;
    }

    public boolean isChargeable() {
        return false;
    }

    @Override
    public int getCooldownDuration() {
        return this.cooldownDuration;
    }

    @Override
    public float getBaseDamagePerHit() {
        return damagePerHit;
    }

    @Override
    public float getDamagePerHit(ItemStack stack) {
        return applyEnchants ? damagePerHit + (damagePerHit / 5) * stack.getEnchantmentLevel(Enchantments.POWER_ARROWS) : damagePerHit;
    }

    public boolean getPoweredUpMultiplier(ItemStack stack) {
        return stack.getCapability(ICapabilityWandAbility.WAND_ABILITY) != null && stack.getCapability(ICapabilityWandAbility.WAND_ABILITY).getPoweredUp();
    }

    @Override
    public ResourceLocation getKey() {
        return WandAbilityRegistry.WAND_ABILITIES_BUILTIN.getKey(this);
    }

    @Override
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

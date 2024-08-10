package screret.sas.api.wand.ability;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import screret.sas.api.capability.ability.WandAbilityProvider;
import screret.sas.enchantment.ModEnchantments;

public class WandAbility implements IWandAbility {

    public static final String BASIC_ABILITY_KEY = "basic_ability", CROUCH_ABILITY_KEY = "crouch_ability", POWERED_UP_KEY = "is_powered_up";

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
    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, WandAbilityInstance.Vec3Wrapped currentPosition, int timeCharged) {
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
        return applyEnchants ? damagePerHit + (damagePerHit / 5) * stack.getEnchantmentLevel(ModEnchantments.POWER.get()) : damagePerHit;
    }

    public boolean getPoweredUpMultiplier(ItemStack stack) {
        return stack.getCapability(WandAbilityProvider.WAND_ABILITY).isPresent() && stack.getCapability(WandAbilityProvider.WAND_ABILITY).resolve().get().getPoweredUp();
    }

    @Override
    public ResourceLocation getKey() {
        return WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get().getKey(this);
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

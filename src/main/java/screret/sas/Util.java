package screret.sas;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import screret.sas.ability.ModWandAbilities;
import screret.sas.api.capability.ability.WandAbilityProvider;
import screret.sas.api.wand.ability.WandAbility;
import screret.sas.api.wand.ability.WandAbilityInstance;
import screret.sas.api.wand.ability.WandAbilityRegistry;
import screret.sas.item.ModItems;
import screret.sas.item.item.WandCoreItem;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class Util {

    public static final Map<ResourceLocation, ItemStack> customWands = Maps.newHashMap();
    public static final Map<ResourceLocation, ItemStack> customWandCores = Maps.newHashMap();

    public static void addItems(){
        addWand(new WandAbilityInstance(ModWandAbilities.SHOOT_RAY.get(), new WandAbilityInstance(ModWandAbilities.DAMAGE.get())), null);
        addWand(new WandAbilityInstance(ModWandAbilities.SHOOT_HOLD_DOWN.get(), new WandAbilityInstance(ModWandAbilities.HEAL.get())), new WandAbilityInstance(ModWandAbilities.HEAL_SELF.get()));
        addWand(new WandAbilityInstance(ModWandAbilities.SHOOT_ANGRY_RAY.get(), new WandAbilityInstance(ModWandAbilities.EXPLODE.get())), null);

        addWand(ModWandAbilities.SMALL_FIREBALL.get(), null);
        addWand(ModWandAbilities.LARGE_FIREBALL.get(), null);
        addWand(new WandAbilityInstance(ModWandAbilities.SHOOT_LIGHTNING.get(), new WandAbilityInstance(ModWandAbilities.LIGHTNING.get())), null);

        WandAbilityRegistry.WAND_ABILITIES_BUILTIN.holders().forEach(ability -> {
            addWandCore(ability.value());
        });
    }

    public static ItemStack createWand(WandAbility main, @Nullable WandAbility crouch){
        var tag = new CompoundTag();
        var ability = new WandAbilityInstance(main);
        tag.put(WandAbility.BASIC_ABILITY_KEY, ability.serializeNBT());
        if(crouch != null) {
            var ability1 = new WandAbilityInstance(crouch);
            tag.put(WandAbility.CROUCH_ABILITY_KEY, ability1.serializeNBT());
        }
        return new ItemStack(ModItems.WAND.get(), 1, tag);
    }

    public static ItemStack addWand(WandAbility main, @Nullable WandAbility crouch){
        return customWands.put(main.getKey(), createWand(main, crouch));
    }

    public static ItemStack createWand(Item item, WandAbilityInstance main, @Nullable WandAbilityInstance crouch){
        var tag = new CompoundTag();
        tag.put(WandAbility.BASIC_ABILITY_KEY, main.serializeNBT());
        if(crouch != null) {
            tag.put(WandAbility.CROUCH_ABILITY_KEY, crouch.serializeNBT());
        }
        return new ItemStack(item, 1, tag);
    }

    public static ItemStack addWand(WandAbilityInstance main, @Nullable WandAbilityInstance crouch){
        var childestAbility = main;
        while (childestAbility.getChildren() != null && !childestAbility.getChildren().isEmpty()){
            childestAbility = childestAbility.getChildren().get(0);
        }
        return customWands.put(childestAbility.getId(), createWand(ModItems.WAND.get(), main, crouch));
    }

    public static ItemStack addWandCore(WandAbility ability){
        var coreStack = new ItemStack(ModItems.WAND_CORE.get(), 1);
        var tag = new CompoundTag();
        tag.putString(WandCoreItem.ABILITY_KEY, ability.toString());
        coreStack.setTag(tag);
        return customWandCores.put(ability.getKey(), coreStack);
    }

    public static WandAbility getAbilityFromJson(JsonObject json, String key){
        return WandAbilityRegistry.WAND_ABILITIES_BUILTIN.get(new ResourceLocation(GsonHelper.getAsString(json, key)));
    }

    public static double randomInRange(RandomSource randomSource, double min, double max) {
        return (randomSource.nextDouble() * (max - min)) + min;
    }

    public static Optional<WandAbilityInstance> getMainAbilityFromStack(ItemStack stack){
        if(stack.getCapability(WandAbilityProvider.WAND_ABILITY) != null) {
            return Optional.of(stack.getCapability(WandAbilityProvider.WAND_ABILITY).getAbility());
        }
        return Optional.empty();
    }

    public static ResourceLocation id(String path){
        return new ResourceLocation(SpellsAndSorcerers.MODID, path);
    }

    public static BlockHitResult getHitResult(Level level, LivingEntity entity, ClipContext.Fluid fluidInteractionMode, double distance) {
        var entityPosStuff = getEntityPos(entity, distance);
        return level.clip(new ClipContext(entity.getEyePosition(), entityPosStuff.to, ClipContext.Block.OUTLINE, fluidInteractionMode, entity));
    }

    public static EntityHitResult getHitResult(Level level, LivingEntity entity, Predicate<Entity> filter, double distance) {
        var entityPosStuff = getEntityPos(entity, distance);
        return ProjectileUtil.getEntityHitResult(entity, entity.getEyePosition(), entityPosStuff.to, AABB.ofSize(entityPosStuff.from, distance, distance, distance), filter, distance);
    }

    public static EntityPosStuff getEntityPos(LivingEntity entity, double distance){
        EntityPosStuff stuff = new EntityPosStuff();
        var xRot = entity.getXRot();
        var yRot = entity.getYRot();
        stuff.from = entity.position();
        float cosY = Mth.cos(-yRot * Mth.DEG_TO_RAD - Mth.PI);
        float sinY = Mth.sin(-yRot * Mth.DEG_TO_RAD - Mth.PI);
        float cosX = -Mth.cos(-xRot * Mth.DEG_TO_RAD);
        float sinX = Mth.sin(-xRot * Mth.DEG_TO_RAD);
        float sinCos = sinY * cosX;
        float cosCos = cosY * cosX;
        stuff.to = entity.getEyePosition().add((double)sinCos * distance, (double)sinX * distance, (double)cosCos * distance);

        return stuff;
    }

    public static void spawnParticlesInLine(Level level, Vec3 start, Vec3 end, ParticleOptions particle, int pointsPerLine, Vec3 randomDeviation, boolean alwaysRender){
        double d = start.distanceTo(end) / pointsPerLine;
        for (int i = 0; i < pointsPerLine; i++) {
            Vec3 pos = new Vec3(start.x, start.y, start.z);
            Vec3 direction = end.subtract(start).normalize();
            Vec3 v = direction.multiply(i * d, i * d, i * d);

            pos = pos.add(v);
            if(level.isClientSide){
                level.addParticle(particle, alwaysRender, pos.x, pos.y, pos.z, randomDeviation.x, randomDeviation.y, randomDeviation.z);
                continue;
            }
            ((ServerLevel)level).sendParticles(particle, pos.x, pos.y, pos.z, 1, randomDeviation.x, randomDeviation.y, randomDeviation.z, 0);
        }
    }

    public static class EntityPosStuff {
        public Vec3 from, to;
    }


}

package dev.screret.motm.api.ability;

import dev.screret.motm.api.registry.MOTMRegistries;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;

import java.util.*;
import java.util.function.Function;

import javax.annotation.ParametersAreNonnullByDefault;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WandAbilityInstance {

    // spotless:off
    public static final Codec<WandAbilityInstance> CODEC = Codec.recursive("WandAbilityInstance", wrapped -> {
        Codec<WandAbilityInstance> childCodec = Codec.either(wrapped, WandAbility.CODEC.xmap(WandAbilityInstance::new, WandAbilityInstance::getAbility))
                .xmap(either -> either.map(Function.identity(), Function.identity()),
                        inst -> !inst.getChildren().isEmpty() ? Either.left(inst) : Either.right(inst));

        return RecordCodecBuilder.create(instance -> instance.group(
                        WandAbility.CODEC.fieldOf("ability").forGetter(WandAbilityInstance::getAbility),
                childCodec.listOf().optionalFieldOf("children", new ArrayList<>()).forGetter(WandAbilityInstance::getChildren)
        ).apply(instance, WandAbilityInstance::new));
    });
    public static final StreamCodec<RegistryFriendlyByteBuf, WandAbilityInstance> STREAM_CODEC = StreamCodec.recursive(codec ->
            StreamCodec.composite(
                    ByteBufCodecs.registry(MOTMRegistries.WAND_ABILITY_REGISTRY), WandAbilityInstance::getAbility,
                    codec.apply(ByteBufCodecs.list()), WandAbilityInstance::getChildren,
                    WandAbilityInstance::new
            )
    );
    // spotless:on

    @Getter
    private final WandAbility<?> ability;
    @Getter
    @Unmodifiable
    private final List<WandAbilityInstance> children;

    public WandAbilityInstance(WandAbility<?> ability, WandAbilityInstance @Nullable... children) {
        this(ability, children == null ? null : List.of(children));
    }

    public WandAbilityInstance(WandAbility<?> ability, @Nullable List<WandAbilityInstance> children) {
        this.ability = ability;
        this.children = children == null ? Collections.emptyList() : List.copyOf(children);
    }

    public static WandAbilityInstance fromNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        var ops = registries.createSerializationContext(NbtOps.INSTANCE);
        return WandAbilityInstance.CODEC.parse(ops, nbt).getOrThrow(IllegalArgumentException::new);
    }

    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, WrappedVec3 currentPos,
                                                      int timeCharged) {
        var returnValue = InteractionResultHolder.fail(stack);
        if (ability != null) {
            returnValue = ability.execute(level, user, stack, currentPos, timeCharged);
        }
        for (var child : this.children) {
            InteractionResult result = child.execute(level, user, stack, currentPos, timeCharged).getResult();
            if (result == InteractionResult.FAIL) {
                return InteractionResultHolder.fail(stack);
            }
        }

        return returnValue;
    }

    public ResourceLocation getId() {
        return ability.getId();
    }

    public String getDescriptionId() {
        return ability.getDescriptionId();
    }

    public boolean isHoldable() {
        if (children != null) {
            for (WandAbilityInstance ability : getChildren()) {
                if (ability.isHoldable())
                    return true;
            }
        }
        return ability.isHoldable();
    }

    public boolean isChargeable() {
        if (children != null) {
            for (WandAbilityInstance ability : getChildren()) {
                if (ability.isChargeable())
                    return true;
            }
        }
        return ability.isChargeable();
    }

    public int getUseDuration() {
        var total = 0;
        if (children != null) {
            for (WandAbilityInstance ability : getChildren()) {
                if (ability.isHoldable())
                    total += ability.getUseDuration();
            }
        }
        return total + ability.getUseDuration();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof WandAbilityInstance other) {
            return other.getAbility() == this.getAbility() && other.getChildren().equals(this.children);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ability, children);
    }

    public static class WrappedVec3 {

        public WrappedVec3(Vec3 obj) {
            this.real = obj;
        }

        public Vec3 real;
    }
}

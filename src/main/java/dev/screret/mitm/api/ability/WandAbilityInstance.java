package dev.screret.mitm.api.ability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import net.neoforged.neoforge.common.util.INBTSerializable;

import dev.screret.mitm.api.registry.MITMRegistries;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class WandAbilityInstance implements INBTSerializable<CompoundTag> {

    // spotless:off
    public static final Codec<WandAbilityInstance> CODEC = Codec.recursive("WandAbilityInstance", wrapped ->
            RecordCodecBuilder.create(instance -> instance.group(
                    WandAbility.CODEC.fieldOf("ability").forGetter(self -> self.ability),
                    wrapped.listOf().optionalFieldOf("children", new ArrayList<>()).forGetter(self -> self.children)
            ).apply(instance, WandAbilityInstance::new))
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, WandAbilityInstance> STREAM_CODEC = StreamCodec.recursive(codec ->
            StreamCodec.composite(
                    ByteBufCodecs.registry(MITMRegistries.WAND_ABILITY_REGISTRY), WandAbilityInstance::getAbility,
                    codec.apply(ByteBufCodecs.list()), WandAbilityInstance::getChildren,
                    WandAbilityInstance::new
            )
    );
    // spotless:on

    @Getter
    private WandAbility<?> ability;
    @Getter
    private List<WandAbilityInstance> children;

    public WandAbilityInstance(WandAbility<?> ability, WandAbilityInstance @Nullable ... children) {
        this.ability = ability;
        this.children = children == null ? new ArrayList<>() : Arrays.stream(children).collect(Collectors.toList());
    }

    public WandAbilityInstance(WandAbility<?> ability, @Nullable List<WandAbilityInstance> children) {
        this.ability = ability;
        this.children = children == null ? new ArrayList<>() : new ArrayList<>(children);
    }

    public WandAbilityInstance(CompoundTag nbt, HolderLookup.Provider registries) {
        this.deserializeNBT(registries, nbt);
    }

    public InteractionResultHolder<ItemStack> execute(Level level, LivingEntity user, ItemStack stack, WrappedVec3 currentPos, int timeCharged) {
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
        return ability.getKey();
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
    public CompoundTag serializeNBT(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putString("ability", getId().toString());
        ListTag children = new ListTag();
        if (this.children != null) {
            for (WandAbilityInstance child : this.children) {
                children.add(child.serializeNBT(registries));
            }
        }
        tag.put("children", children);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider registries, CompoundTag nbt) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.ability = MITMRegistries.WAND_ABILITIES.get(ResourceLocation.parse(nbt.getString("ability")));
        ListTag children = nbt.getList("children", Tag.TAG_COMPOUND);
        for (int i = 0; i < children.size(); ++i) {
            var child = children.getCompound(i);
            WandAbilityInstance a = new WandAbilityInstance(child, registries);
            this.children.add(a);
        }
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

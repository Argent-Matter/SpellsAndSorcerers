package dev.screret.mitm.common.item.component;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.screret.mitm.api.ability.WandAbilityInstance;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record WandComponent(WandAbilityInstance primary, Optional<WandAbilityInstance> secondary, boolean poweredUp) {

    // spotless:off
    public static final Codec<WandComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WandAbilityInstance.CODEC.fieldOf("primary").forGetter(WandComponent::primary),
            WandAbilityInstance.CODEC.optionalFieldOf("secondary").forGetter(WandComponent::secondary),
            Codec.BOOL.fieldOf("powered_up").forGetter(WandComponent::poweredUp)
    ).apply(instance, WandComponent::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, WandComponent> STREAM_CODEC = StreamCodec.composite(
            WandAbilityInstance.STREAM_CODEC, WandComponent::primary,
            ByteBufCodecs.optional(WandAbilityInstance.STREAM_CODEC), WandComponent::secondary,
            ByteBufCodecs.BOOL, WandComponent::poweredUp,
            WandComponent::new
    );
    // spotless:on

    public @Nullable WandAbilityInstance secondaryOrNull() {
        return secondary.orElse(null);
    }
}

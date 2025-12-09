package dev.screret.motm.common.world.generation;

import dev.screret.motm.data.MOTMHeightProviders;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.ConstantHeight;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.function.ToIntBiFunction;

import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class DeferringHeightProvider extends HeightProvider {

    private static final Map<ResourceLocation, DeferringHeightProvider> REGISTERED_HEIGHT_PROVIDERS = new HashMap<>();

    public static void addValueProvider(ResourceLocation name, DeferringHeightProvider value) {
        if (REGISTERED_HEIGHT_PROVIDERS.containsKey(name)) {
            throw new IllegalArgumentException("Cannot add two height providers with id " + name);
        }
        value.setName(name);
        REGISTERED_HEIGHT_PROVIDERS.put(name, value);
    }

    // spotless:off
    private static final MapCodec<Either<ResourceLocation, HeightProvider>> NAME_OR_WRAPPED_CODEC = Codec.mapEither(
            ResourceLocation.CODEC.fieldOf("name"),
            HeightProvider.CODEC.fieldOf("value")
    );
    public static final MapCodec<DeferringHeightProvider> CODEC = NAME_OR_WRAPPED_CODEC.flatXmap(
            either -> DataResult.success(either.map(REGISTERED_HEIGHT_PROVIDERS::get, anchor -> DeferringHeightProvider.of(null, anchor))),
            provider -> {
                if (provider.wrappedHeightProvider != null) {
                    return DataResult.success(Either.right(provider.wrappedHeightProvider));
                } else if (provider.name != null) {
                    return DataResult.success(Either.left(provider.name));
                } else {
                    return DataResult.error(() -> "Could not save deferring height provider " + provider + ", as it has no wrapped provider or registered name.");
                }
            }
    );
    // spotless:on

    private final ToIntBiFunction<RandomSource, WorldGenerationContext> heightProvider;
    @Nullable
    @Setter(AccessLevel.PRIVATE)
    private ResourceLocation name;

    @Nullable
    @Getter(AccessLevel.PRIVATE)
    @Setter(AccessLevel.PRIVATE)
    private HeightProvider wrappedHeightProvider = null;

    protected DeferringHeightProvider(ToIntBiFunction<RandomSource, WorldGenerationContext> heightProvider) {
        this(null, heightProvider);
    }

    protected DeferringHeightProvider(@Nullable ResourceLocation name,
                                      ToIntBiFunction<RandomSource, WorldGenerationContext> heightProvider) {
        this.name = name;
        this.heightProvider = heightProvider;

        if (this.name != null) {
            addValueProvider(this.name, this);
        }
    }

    public static DeferringHeightProvider absolute(@Nullable ResourceLocation name, IntSupplier heightSupplier) {
        return of(name, (rng, ctx) -> heightSupplier.getAsInt());
    }

    public static DeferringHeightProvider aboveBottom(@Nullable ResourceLocation name, IntSupplier heightSupplier) {
        return of(name, () -> VerticalAnchor.aboveBottom(heightSupplier.getAsInt()));
    }

    public static DeferringHeightProvider belowTop(@Nullable ResourceLocation name, IntSupplier heightSupplier) {
        return of(name, () -> VerticalAnchor.belowTop(heightSupplier.getAsInt()));
    }

    public static DeferringHeightProvider of(@Nullable ResourceLocation name, Supplier<VerticalAnchor> heightSupplier) {
        return ofHeightProvider(name, (rng, ctx) -> heightSupplier.get());
    }

    public static DeferringHeightProvider of(@Nullable ResourceLocation name, VerticalAnchor height) {
        return of(name, ConstantHeight.of(height));
    }

    public static DeferringHeightProvider of(@Nullable ResourceLocation name, HeightProvider heightProvider) {
        DeferringHeightProvider value = of(name, heightProvider::sample);
        value.setWrappedHeightProvider(heightProvider);
        return value;
    }

    public static DeferringHeightProvider ofHeightProvider(@Nullable ResourceLocation name,
                                                           BiFunction<RandomSource, WorldGenerationContext, VerticalAnchor> heightProvider) {
        return of(name, (rng, ctx) -> heightProvider.apply(rng, ctx).resolveY(ctx));
    }

    public static DeferringHeightProvider of(@Nullable ResourceLocation name,
                                             ToIntBiFunction<RandomSource, WorldGenerationContext> heightProvider) {
        return new DeferringHeightProvider(heightProvider);
    }

    @Override
    public int sample(RandomSource random, WorldGenerationContext context) {
        return heightProvider.applyAsInt(random, context);
    }

    @Override
    public HeightProviderType<?> getType() {
        return MOTMHeightProviders.DEFERRING.get();
    }
}

package dev.screret.motm.api.util.registries;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

/**
 * Special {@link DeferredHolder} for {@link BlockEntityType BlockEntityTypes}.
 *
 * @param <B> The specific {@link BlockEntity} type.
 */
public class DeferredBlockEntity<B extends BlockEntity> extends DeferredHolder<BlockEntityType<?>, BlockEntityType<? extends B>> {

    /**
     * Create a "default" instance of this {@link BlockEntity} via the {@link BlockEntityType}.
     *
     * @return The instance
     */
    public @Nullable B create(BlockPos pos, BlockState state) {
        return get().create(pos, state);
    }

    /**
     * Check that the given {@link BlockEntity} is an instance of this type.
     *
     * @param blockEntity The {@link BlockEntity}
     * @return {@code true} if the type matches, {@code false} otherwise.
     */
    public boolean is(@Nullable BlockEntity blockEntity) {
        return blockEntity != null && blockEntity.getType() == get();
    }

    /**
     * Get an instance of this {@link BlockEntity} from the level.
     *
     * @param level The level to look for the block entity in
     * @param pos   The position of the block entity
     * @return An {@link Optional} containing the block entity, if it exists and matches this type. Otherwise,
     *         {@link Optional#empty()}.
     * @see #getNullable(BlockGetter, BlockPos)
     */
    public Optional<B> get(BlockGetter level, BlockPos pos) {
        return Optional.ofNullable(getNullable(level, pos));
    }

    /**
     * Get an instance of this {@link BlockEntity} from the level.
     *
     * @param level The level to look for the block entity in
     * @param pos   The position of the block entity
     * @return The block entity, if it exists and matches this type. Otherwise, {@code null}.
     * @see #get(BlockGetter, BlockPos)
     */
    @SuppressWarnings("unchecked")
    public @Nullable B getNullable(BlockGetter level, BlockPos pos) {
        BlockEntity be = level.getBlockEntity(pos);
        return is(be) ? (B) be : null;
    }

    /**
     * Creates a new {@link DeferredHolder} targeting the {@link BlockEntityType} with the specified name.
     *
     * @param <B> The type of the target {@link BlockEntity}.
     * @param key The name of the target {@link BlockEntityType}.
     */
    public static <B extends BlockEntity> DeferredBlockEntity<B> createBlockEntity(ResourceLocation key) {
        return createBlockEntity(ResourceKey.create(Registries.BLOCK_ENTITY_TYPE, key));
    }

    /**
     * Creates a new {@link DeferredHolder} targeting the specified {@link BlockEntityType}.
     *
     * @param <B> The type of the target {@link BlockEntity}.
     * @param key The name of the target {@link BlockEntityType}.
     */
    public static <B extends BlockEntity> DeferredBlockEntity<B> createBlockEntity(ResourceKey<BlockEntityType<?>> key) {
        return new DeferredBlockEntity<>(key);
    }

    protected DeferredBlockEntity(ResourceKey<BlockEntityType<?>> key) {
        super(key);
    }
}

package dev.screret.motm.api.registry.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("unused")
public final class DeferredRegisterHelper {

    private DeferredRegisterHelper() {
        throw new RuntimeException();
    }

    /**
     * Factory for a specialized DeferredRegister for {@link BlockEntityType BlockEntityTypes}.
     *
     * @param modid The namespace for all objects registered to this DeferredRegister
     * @see DeferredRegister#createBlocks(String)
     * @see DeferredRegister#createItems(String)
     */
    public static DeferredRegisterHelper.BlockEntities createBlockEntities(String modid) {
        return new DeferredRegisterHelper.BlockEntities(modid);
    }

    /**
     * Specialized DeferredRegister for {@link BlockEntityType BlockEntityTypes} that uses the specialized {@link DeferredBlock}
     * as the return type for {@link #register}.
     */
    public static class BlockEntities extends DeferredRegister<BlockEntityType<?>> {

        protected BlockEntities(String namespace) {
            super(Registries.BLOCK_ENTITY_TYPE, namespace);
        }

        /**
         * Adds a new block to the list of entries to be registered and returns a {@link DeferredHolder} that will be populated
         * with the created block automatically.
         *
         * @param name The new block entity type's name. It will automatically have the {@linkplain #getNamespace() namespace}
         *             prefixed.
         * @param func A factory for the new block entity type. The factory should not cache the created block entity type.
         * @return A {@link DeferredHolder} that will track updates from the registry for this block entity type.
         */
        @SuppressWarnings("unchecked")
        public <T extends BlockEntity> DeferredBlockEntity<T> registerBlockEntity(String name,
                                                                                  Function<ResourceLocation, ? extends BlockEntityType<? extends T>> func) {
            return (DeferredBlockEntity<T>) this.register(name, func);
        }

        /**
         * Adds a new block to the list of entries to be registered and returns a {@link DeferredHolder} that will be populated
         * with the created block automatically.
         *
         * @param name The new block entity type's name. It will automatically have the {@linkplain #getNamespace() namespace}
         *             prefixed.
         * @param sup  A factory for the new block entity type. The factory should not cache the created block entity type.
         * @return A {@link DeferredHolder} that will track updates from the registry for this block entity type.
         */
        public <T extends BlockEntity> DeferredBlockEntity<T> registerBlockEntity(String name,
                                                                                  Supplier<? extends BlockEntityType<? extends T>> sup) {
            return this.registerBlockEntity(name, key -> sup.get());
        }

        /**
         * Adds a new block to the list of entries to be registered and returns a {@link DeferredHolder} that will be populated
         * with the created block automatically.
         *
         * @param name The new block entity type's name. It will automatically have the {@linkplain #getNamespace() namespace}
         *             prefixed.
         * @param sup  A factory for the new block entity type. The factory should not cache the created block entity type.
         * @return A {@link DeferredHolder} that will track updates from the registry for this block entity type.
         */
        @SuppressWarnings("DataFlowIssue")
        public <T extends BlockEntity> DeferredBlockEntity<T> registerBuilderBlockEntity(String name,
                                                                                         Supplier<? extends BlockEntityType.Builder<? extends T>> sup) {
            return this.registerBlockEntity(name, key -> sup.get().build(null));
        }

        /**
         * Adds a new block to the list of entries to be registered and returns a {@link DeferredHolder} that will be populated
         * with the created block automatically.
         *
         * @param name        The new block entity type's name. It will automatically have the {@linkplain #getNamespace()
         *                    namespace} prefixed.
         * @param supplier    A factory for creating instances of this block entity.
         * @param validBlocks A collection of valid blocks for this block entity type.
         * @return A {@link DeferredHolder} that will track updates from the registry for this block entity type.
         */
        public <T extends BlockEntity> DeferredBlockEntity<T> registerSimpleBlockEntity(String name,
                                                                                        BlockEntityType.BlockEntitySupplier<? extends T> supplier,
                                                                                        Collection<? extends Holder<? extends Block>> validBlocks) {
            return this.registerBuilderBlockEntity(name,
                    () -> BlockEntityType.Builder.of(supplier, validBlocks.stream().map(Holder::value).toArray(Block[]::new)));
        }

        /**
         * Adds a new block to the list of entries to be registered and returns a {@link DeferredHolder} that will be populated
         * with the created block automatically.
         *
         * @param name       The new block entity type's name. It will automatically have the {@linkplain #getNamespace()
         *                   namespace} prefixed.
         * @param supplier   A factory for creating instances of this block entity.
         * @param validBlock The valid block for this block entity.
         * @return A {@link DeferredHolder} that will track updates from the registry for this block entity type.
         */
        public <T extends BlockEntity> DeferredBlockEntity<T> registerSimpleBlockEntity(String name,
                                                                                        BlockEntityType.BlockEntitySupplier<? extends T> supplier,
                                                                                        Holder<? extends Block> validBlock) {
            return this.registerBuilderBlockEntity(name, () -> BlockEntityType.Builder.of(supplier, validBlock.value()));
        }

        @SuppressWarnings("unchecked")
        @Override
        protected <
                I extends BlockEntityType<? extends BlockEntity>> DeferredHolder<BlockEntityType<?>, I> createHolder(ResourceKey<? extends Registry<BlockEntityType<?>>> registryKey,
                                                                                                                     ResourceLocation key) {
            return (DeferredHolder<BlockEntityType<?>, I>) DeferredBlockEntity
                    .createBlockEntity(ResourceKey.create(registryKey, key));
        }
    }
}

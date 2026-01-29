package com.gtceu.syncsystem.transformers;

import com.gtceu.syncsystem.TypeDeclaration;
import com.gtceu.syncsystem.transformers.collections.ListTransformer;
import com.gtceu.syncsystem.transformers.collections.MapTransformer;
import com.gtceu.syncsystem.transformers.collections.ObjectArrayTransformer;
import com.gtceu.syncsystem.transformers.collections.SetTransformer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

public final class ValueTransformers {

    private static final Map<Class<?>, ValueTransformer<?>> REGISTERED = new Reference2ReferenceOpenHashMap<>();
    private static final Map<Class<?>, Supplier<ValueTransformer<?>>> REGISTERED_SUPPLIERS = new Reference2ReferenceOpenHashMap<>();

    private static final Map<Type, Type> PRIMITIVE_TO_BOXED = Map.of(
            boolean.class, Boolean.class,
            byte.class, Byte.class,
            char.class, Character.class,
            short.class, Short.class,
            int.class, Integer.class,
            long.class, Long.class,
            float.class, Float.class,
            double.class, Double.class,
            void.class, Void.class);

    private static final Map<Type, ValueTransformer<?>> TYPE_CACHE = new Reference2ReferenceOpenHashMap<>();

    /**
     * Gets the {@link ValueTransformer} associated with a specific type.
     */
    public static @Nullable ValueTransformer<?> get(Type type) {
        if (type instanceof Class<?> cls) type = cls.isPrimitive() ? PRIMITIVE_TO_BOXED.get(cls) : cls;
        return TYPE_CACHE.computeIfAbsent(type, ValueTransformers::generateOrGetTransformer);
    }

    private static @Nullable ValueTransformer<?> generateOrGetTransformer(Type type) {
        TypeDeclaration declaration = new TypeDeclaration(type);
        Class<?> clazz = declaration.getClassValue();

        if (clazz != null && REGISTERED.containsKey(clazz)) return REGISTERED.get(clazz);

        if (clazz == null || clazz.isArray()) {
            ValueTransformer<?> componentTx = get(declaration.getArrayComponentType().getRawType());
            if (componentTx != null) return new ObjectArrayTransformer<>(componentTx);
            return null;
        }

        if (clazz.isEnum()) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) clazz;
            return new EnumTransformer<>(enumClass);
        }

        for (var entry : REGISTERED_SUPPLIERS.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) return entry.getValue().get();
        }

        for (var entry : REGISTERED.entrySet()) {
            if (entry.getKey().isAssignableFrom(clazz)) return entry.getValue();
        }

        return null;
    }

    /**
     * Registers a {@link ValueTransformer} for the given class or interface.
     * If registering a type with generic arguments, instead use {@link #registerTransformerProvider}
     * to create a new transformer instance for each set of generic type arguments.
     * 
     * @param type        The class to register this {@link ValueTransformer} for
     * @param transformer The transformer being registered
     */
    public static <T> void registerTransformer(Class<T> type, ValueTransformer<T> transformer) {
        if (REGISTERED.containsKey(type)) {
            throw new IllegalArgumentException("Attempted to register transformer for %s twice".formatted(type));
        }
        REGISTERED.put(type, transformer);
    }

    /**
     * Registers a {@link ValueTransformer} for the given class or interface.
     * If registering a type with generic arguments, instead use {@link #registerTransformerProvider}
     * to create a new transformer instance for each set of generic type arguments.
     *
     * @param type  The class to register this {@link ValueTransformer} for
     * @param codec The codec to register as a transformer
     */
    public static <T> void registerCodecTransformer(Class<T> type, Codec<T> codec) {
        registerTransformer(type, new CodecTransformer<>(codec));
    }

    /**
     * Creates and registers a {@link ValueTransformer} for the given class using predefined NBT parsing functions.
     *
     * @param type     The class to register this {@link ValueTransformer} for
     * @param write    A function that writes the value into a specific tag type
     * @param read     A function that reads the value from a specific tag type
     * @param tagClass The tag type the value is serialized into
     */
    public static <T, S extends Tag> void registerSimpleClassTransformer(Class<T> type, Function<T, S> write, Function<S, T> read,
                                                                         Class<S> tagClass) {
        registerTransformer(type, new SimpleClassTransformer<>(write, read, tagClass));
    }

    /**
     * Creates and registers a {@link ValueTransformer} that simply passes the original value through as is.
     *
     * @param tagClass The class to register this {@link PassthroughTransformer} for
     */
    public static <T extends Tag> void registerPassthrough(Class<T> tagClass) {
        registerTransformer(tagClass, new PassthroughTransformer<>(tagClass));
    }

    /**
     * Registers a supplier that supplies instances of a specific transformer type.
     * The supplier will be called to create new instances of the transformer for each unique set of
     * generic type arguments passed to the given class.
     *
     * @param type The class to register this {@link ValueTransformer} supplier for
     * @param sup  Supplier function
     */
    public static <T> void registerTransformerProvider(Class<T> type, Supplier<ValueTransformer<?>> sup) {
        if (REGISTERED_SUPPLIERS.containsKey(type)) {
            throw new IllegalArgumentException("Attempted to register transformer provider for %s twice".formatted(type));
        }
        REGISTERED_SUPPLIERS.put(type, sup);
    }

    // spotless:off
    static {
        // Primitives
        registerSimpleClassTransformer(Integer.class, IntTag::valueOf, IntTag::getAsInt, IntTag.class);
        registerSimpleClassTransformer(Long.class, LongTag::valueOf, LongTag::getAsLong, LongTag.class);
        registerSimpleClassTransformer(Float.class, FloatTag::valueOf, FloatTag::getAsFloat, FloatTag.class);
        registerSimpleClassTransformer(Double.class, DoubleTag::valueOf, DoubleTag::getAsDouble, DoubleTag.class);
        registerSimpleClassTransformer(Short.class, ShortTag::valueOf, ShortTag::getAsShort, ShortTag.class);
        registerSimpleClassTransformer(Byte.class, ByteTag::valueOf, ByteTag::getAsByte, ByteTag.class);
        registerSimpleClassTransformer(Character.class, (b) -> IntTag.valueOf(b), (t) -> (char) t.getAsInt(), IntTag.class);
        registerSimpleClassTransformer(Boolean.class, ByteTag::valueOf, (b) -> b.getAsByte() != 0, ByteTag.class);

        // Primtive arrays
        registerSimpleClassTransformer(int[].class, IntArrayTag::new, IntArrayTag::getAsIntArray, IntArrayTag.class);
        registerSimpleClassTransformer(long[].class, LongArrayTag::new, LongArrayTag::getAsLongArray, LongArrayTag.class);
        registerSimpleClassTransformer(byte[].class, ByteArrayTag::new, ByteArrayTag::getAsByteArray, ByteArrayTag.class);
        registerPrimitiveArrayTransformer(float.class, Tag.TAG_FLOAT);
        registerPrimitiveArrayTransformer(double.class, Tag.TAG_DOUBLE);
        registerPrimitiveArrayTransformer(short.class, Tag.TAG_SHORT);
        registerPrimitiveArrayTransformer(char.class, Tag.TAG_INT);

        // Java classes and standard minecraft/forge classes

        registerSimpleClassTransformer(String.class, StringTag::valueOf, StringTag::getAsString, StringTag.class);

        registerCodecTransformer(ItemStack.class, ItemStack.OPTIONAL_CODEC);
        registerCodecTransformer(FluidStack.class, FluidStack.OPTIONAL_CODEC);

        registerCodecTransformer(UUID.class, UUIDUtil.LENIENT_CODEC);
        registerCodecTransformer(BlockPos.class, BlockPos.CODEC);
        registerPassthrough(CompoundTag.class);

        registerCodecTransformer(Component.class, ComponentSerialization.CODEC);

        registerTransformerProvider(INBTSerializable.class, NBTSerializableTransformer::new);

        registerTransformerProvider(List.class, ListTransformer::new);
        registerTransformerProvider(Map.class, MapTransformer::new);
        registerTransformerProvider(Set.class, SetTransformer::new);
    }
    // spotless:on

    /**
     * Create a generic primitive array transformer for primitive types that don't have built in array types in NBT.<br>
     * This is private because it should only actually be used for {@code float}, {@code double}, {@code short}, and {@code char}.
     *
     * @param componentType    The component type of the array, e.g. {@code float.class} for a float array.
     * @param componentTagType The tag type to use for the component, e.g. {@link Tag#TAG_FLOAT} for a float array.
     * @param <T>              {@code componentType}
     */
    @SuppressWarnings("unchecked")
    private static <T> void registerPrimitiveArrayTransformer(Class<T> componentType, byte componentTagType) {
        ValueTransformers.registerSimpleClassTransformer((Class<T[]>) componentType.arrayType(), (T[] arr) -> {
            ListTag tag = new ListTag(arr.length);
            for (int i = 0; i < arr.length; i++) {
                // I don't know a better way to do this, so this is what we're using.
                switch (componentTagType) {
                    case Tag.TAG_INT -> {
                        // special case char support
                        if (componentType == char.class || componentType == Character.class) {
                            tag.add(IntTag.valueOf(Array.getChar(arr, i)));
                        } else {
                            tag.add(IntTag.valueOf(Array.getInt(arr, i)));
                        }
                    }
                    case Tag.TAG_LONG -> tag.add(LongTag.valueOf(Array.getLong(arr, i)));
                    case Tag.TAG_FLOAT -> tag.add(FloatTag.valueOf(Array.getFloat(arr, i)));
                    case Tag.TAG_DOUBLE -> tag.add(DoubleTag.valueOf(Array.getDouble(arr, i)));
                    case Tag.TAG_SHORT -> tag.add(ShortTag.valueOf(Array.getShort(arr, i)));
                    case Tag.TAG_BYTE -> tag.add(ByteTag.valueOf(Array.getByte(arr, i)));
                    default -> throw new IllegalArgumentException("%s is not a primitive tag type".formatted(componentTagType));
                }
            }
            return tag;
        }, listTag -> {
            T[] array = (T[]) Array.newInstance(componentType, listTag.size());
            if (listTag.isEmpty() || listTag.getElementType() != componentTagType) {
                return array;
            }
            for (int i = 0; i < listTag.size(); i++) {
                NumericTag tag = (NumericTag) listTag.get(i);
                // I don't know a better way to do this, so this is what we're using.
                switch (componentTagType) {
                    case Tag.TAG_INT -> {
                        // special case char support
                        if (componentType == char.class || componentType == Character.class) {
                            Array.setChar(array, i, (char) tag.getAsInt());
                        } else {
                            Array.setInt(array, i, tag.getAsInt());
                        }
                    }
                    case Tag.TAG_LONG -> Array.setLong(array, i, tag.getAsLong());
                    case Tag.TAG_FLOAT -> Array.setFloat(array, i, tag.getAsFloat());
                    case Tag.TAG_DOUBLE -> Array.setDouble(array, i, tag.getAsDouble());
                    case Tag.TAG_SHORT -> Array.setShort(array, i, tag.getAsShort());
                    case Tag.TAG_BYTE -> Array.setByte(array, i, tag.getAsByte());
                    default -> throw new IllegalArgumentException("%s is not a primitive tag type".formatted(componentTagType));
                }
            }
            return array;
        }, ListTag.class);
    }
}

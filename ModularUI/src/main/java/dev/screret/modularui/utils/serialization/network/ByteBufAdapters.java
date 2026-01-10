package dev.screret.modularui.utils.serialization.network;

import dev.screret.modularui.network.NetworkUtils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.VarInt;
import net.minecraft.network.codec.*;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

import io.netty.buffer.ByteBuf;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.network.codec.ByteBufCodecs.*;

public class ByteBufAdapters {

    // spotless:off
    public static final IByteBufAdapter<RegistryFriendlyByteBuf, ItemStack> ITEM_STACK = makeAdapter(ItemStack.OPTIONAL_STREAM_CODEC, ItemStack::matches);
    public static final IByteBufAdapter<RegistryFriendlyByteBuf, FluidStack> FLUID_STACK = makeAdapter(FluidStack.OPTIONAL_STREAM_CODEC, FluidStack::matches);
    public static final IByteBufAdapter<ByteBuf, CompoundTag> NBT = makeAdapter(ByteBufCodecs.COMPOUND_TAG, null);
    public static final IByteBufAdapter<ByteBuf, String> STRING = makeAdapter(STRING_UTF8, null);
    public static final IByteBufAdapter<ByteBuf, ByteBuf> BYTE_BUF = makeAdapter(NetworkUtils::readByteBuf, NetworkUtils::writeByteBuf, null);
    public static final IByteBufAdapter<ByteBuf, FriendlyByteBuf> FRIENDLY_BYTE_BUF = makeAdapter(NetworkUtils::readFriendlyByteBuf, NetworkUtils::writeByteBuf, null);
    // spotless:on

    public static final IByteBufAdapter<ByteBuf, byte[]> BYTE_ARR = makeAdapter(BYTE_ARRAY, (t1, t2) -> {
        if (t1.length != t2.length) return false;
        for (int i = 0; i < t1.length; i++) {
            if (t1[i] != t2[i]) return false;
        }
        return true;
    });

    public static final IByteBufAdapter<ByteBuf, long[]> LONG_ARR = new IByteBufAdapter<>() {

        @Override
        public long @NotNull [] decode(@NotNull ByteBuf buffer) {
            int length = VarInt.read(buffer);
            long[] array = new long[length];
            for (int i = 0; i < length; i++) {
                array[i] = buffer.readLong();
            }
            return array;
        }

        @Override
        public void encode(@NotNull ByteBuf buffer, long @NotNull [] u) {
            VarInt.write(buffer, u.length);
            for (long i : u) {
                buffer.writeLong(i);
            }
        }

        @Override
        public boolean areEqual(long @NotNull [] t1, long @NotNull [] t2) {
            if (t1.length != t2.length) return false;
            for (int i = 0; i < t1.length; i++) {
                if (t1[i] != t2[i]) return false;
            }
            return true;
        }
    };

    public static final IByteBufAdapter<ByteBuf, BigInteger> BIG_INT = new IByteBufAdapter<>() {

        @Override
        public @NotNull BigInteger decode(@NotNull ByteBuf buffer) {
            return new BigInteger(FriendlyByteBuf.readByteArray(buffer));
        }

        @Override
        public void encode(@NotNull ByteBuf buffer, @NotNull BigInteger u) {
            FriendlyByteBuf.writeByteArray(buffer, u.toByteArray());
        }

        @Override
        public boolean areEqual(@NotNull BigInteger t1, @NotNull BigInteger t2) {
            return t1.equals(t2);
        }
    };

    public static final IByteBufAdapter<ByteBuf, BigDecimal> BIG_DECIMAL = new IByteBufAdapter<>() {

        @Override
        public @NotNull BigDecimal decode(@NotNull ByteBuf buffer) {
            return new BigDecimal(BIG_INT.decode(buffer), VarInt.read(buffer));
        }

        @Override
        public void encode(@NotNull ByteBuf buffer, @NotNull BigDecimal u) {
            BIG_INT.encode(buffer, u.unscaledValue());
            VarInt.write(buffer, u.scale());
        }

        @Override
        public boolean areEqual(@NotNull BigDecimal t1, @NotNull BigDecimal t2) {
            return t1.equals(t2);
        }
    };

    public static <B, V> IByteBufAdapter<B, V> makeAdapter(@NotNull StreamDecoder<B, V> decoder,
                                                           @NotNull StreamEncoder<B, V> encoder,
                                                           @Nullable IEquals<V> comparator) {
        final IEquals<V> tester = comparator != null ? comparator : IEquals.defaultTester();
        return new IByteBufAdapter<>() {

            @Override
            public @NotNull V decode(@NotNull B buffer) {
                return decoder.decode(buffer);
            }

            @Override
            public void encode(@NotNull B buffer, @NotNull V u) {
                encoder.encode(buffer, u);
            }

            @Override
            public boolean areEqual(@NotNull V v1, @NotNull V v2) {
                return tester.areEqual(v1, v2);
            }
        };
    }

    public static <B, V> IByteBufAdapter<B, V> makeAdapter(@NotNull StreamCodec<B, V> codec,
                                                           @Nullable IEquals<V> comparator) {
        return makeAdapter(codec, codec, comparator);
    }

    public static <B, V> IByteBufAdapter<B, V> makeMemberAdapter(@NotNull StreamDecoder<B, V> decoder,
                                                                 @NotNull StreamMemberEncoder<B, V> memberEncoder,
                                                                 @Nullable IEquals<V> comparator) {
        return makeAdapter(decoder, (buffer, value) -> memberEncoder.encode(value, buffer), comparator);
    }
}

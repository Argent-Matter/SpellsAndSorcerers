package dev.screret.mui.utils.serialization.network;

import net.minecraft.network.codec.StreamCodec;

import org.jetbrains.annotations.NotNull;

public interface IByteBufAdapter<B, V> extends StreamCodec<B, V>, IEquals<V> {

    @Override
    @NotNull
    V decode(@NotNull B buffer);

    @Override
    void encode(@NotNull B buffer, @NotNull V u);

    @Override
    boolean areEqual(@NotNull V v1, @NotNull V v2);
}

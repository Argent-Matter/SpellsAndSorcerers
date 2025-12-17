package dev.screret.motm.common.item.component;

import dev.screret.motm.common.block.entity.PortStoneBlockEntity.PortRune;

import net.minecraft.network.codec.StreamCodec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;

public record PortStoneRunes(PortRune north, PortRune east, PortRune south, PortRune west) {

    // spotless:off
    public static final Codec<PortStoneRunes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PortRune.CODEC.fieldOf("north").forGetter(PortStoneRunes::north),
            PortRune.CODEC.fieldOf("east").forGetter(PortStoneRunes::east),
            PortRune.CODEC.fieldOf("south").forGetter(PortStoneRunes::south),
            PortRune.CODEC.fieldOf("west").forGetter(PortStoneRunes::west)
    ).apply(instance, PortStoneRunes::new));
    public static final StreamCodec<ByteBuf, PortStoneRunes> STREAM_CODEC = StreamCodec.composite(
            PortRune.STREAM_CODEC, PortStoneRunes::north,
            PortRune.STREAM_CODEC, PortStoneRunes::east,
            PortRune.STREAM_CODEC, PortStoneRunes::south,
            PortRune.STREAM_CODEC, PortStoneRunes::west,
            PortStoneRunes::new
    );
    // spotless:on

    public PortStoneRunes(PortRune[] entrances) {
        this(entrances[0], entrances[1], entrances[2], entrances[3]);
    }
}

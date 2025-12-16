package dev.screret.motm.common.network.packets;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.client.memory.ClientMemoryCache;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import io.netty.buffer.ByteBuf;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public record SendStructurePacket(ResourceLocation structureName, Optional<StructureTemplate> structure) implements CustomPacketPayload {

    // spotless:off
    public static final ResourceLocation ID = MOTMUtil.id("send_structure");
    public static final Type<SendStructurePacket> TYPE = new Type<>(ID);

    private static final StreamCodec<ByteBuf, Optional<StructureTemplate>> STRUCTURE_TEMPLATE_CODEC = ByteBufCodecs.optional(ByteBufCodecs.COMPOUND_TAG)
            .map(tag -> tag.map(nbt -> {
                StructureTemplate structure = new StructureTemplate();
                structure.load(BuiltInRegistries.BLOCK.asLookup(), tag.get());
                return structure;
            }), structure -> structure.map(value -> value.save(new CompoundTag())));
    public static final StreamCodec<ByteBuf, SendStructurePacket> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, SendStructurePacket::structureName,
            STRUCTURE_TEMPLATE_CODEC, SendStructurePacket::structure,
            SendStructurePacket::new
    );
    // spotless:on

    public void execute(IPayloadContext context) {
        Player player = context.player();
        // on client

        if (this.structure.isPresent()) {
            CompletableFuture<StructureTemplate> future = ClientMemoryCache.MEMORY_STRUCTURE_CACHE.getIfPresent(this.structureName);
            if (future != null) {
                future.complete(this.structure.get());
            } else {
                // if it doesn't exist (anymore), set a new value
                ClientMemoryCache.MEMORY_STRUCTURE_CACHE.put(this.structureName, CompletableFuture.completedFuture(this.structure.get()));
            }
        } else {
            player.sendSystemMessage(Component.translatableEscape("message.motm.structure_load_error", this.structureName));
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

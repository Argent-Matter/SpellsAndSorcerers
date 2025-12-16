package dev.screret.motm.common.network.packets;

import dev.screret.motm.MOTMUtil;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

public record RequestStructurePacket(ResourceLocation structureName) implements CustomPacketPayload {

    // spotless:off
    public static final ResourceLocation ID = MOTMUtil.id("request_structure");
    public static final Type<RequestStructurePacket> TYPE = new Type<>(ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestStructurePacket> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC, RequestStructurePacket::structureName,
            RequestStructurePacket::new
    );
    // spotless:on

    public void execute(IPayloadContext context) {
        // on server
        Optional<StructureTemplate> structure = Optional.ofNullable(context.player().getServer())
                .flatMap(server -> server.getStructureManager().get(this.structureName));
        context.reply(new SendStructurePacket(this.structureName, structure));
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

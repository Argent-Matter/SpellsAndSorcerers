package dev.screret.motm.client.memory;

import dev.screret.motm.common.network.packets.RequestStructurePacket;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.neoforged.neoforge.network.PacketDistributor;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public final class ClientMemoryCache {

    public static final LoadingCache<ResourceLocation, CompletableFuture<StructureTemplate>> MEMORY_STRUCTURE_CACHE = CacheBuilder.newBuilder()
            .softValues()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(CacheLoader.from(structure -> {
                Preconditions.checkNotNull(structure, "Structure name must not be null");
                PacketDistributor.sendToServer(new RequestStructurePacket(structure));
                // return a CompletableFuture that doesn't have a value, it'll be filled later in SendStructurePacket#execute
                return new CompletableFuture<>();
            }));

    private ClientMemoryCache() {}
}

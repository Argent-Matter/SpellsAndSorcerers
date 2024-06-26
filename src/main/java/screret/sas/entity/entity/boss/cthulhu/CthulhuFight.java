package screret.sas.entity.entity.boss.cthulhu;

import com.google.common.collect.*;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.EndFeatures;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.*;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TheEndPortalBlockEntity;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockPredicate;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.end.DragonRespawnAnimation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;

public class CthulhuFight {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_TICKS_BEFORE_DRAGON_RESPAWN = 1200;
    private static final int TIME_BETWEEN_CRYSTAL_SCANS = 100;
    private static final int TIME_BETWEEN_PLAYER_SCANS = 20;
    private static final int ARENA_SIZE_CHUNKS = 8;
    public static final int ARENA_TICKET_LEVEL = 9;
    private static final int GATEWAY_COUNT = 20;
    private static final int GATEWAY_DISTANCE = 96;
    public static final int DRAGON_SPAWN_Y = 128;
    private static final Predicate<Entity> VALID_PLAYER = EntitySelector.ENTITY_STILL_ALIVE.and(EntitySelector.withinDistance(0.0D, 128.0D, 0.0D, 192.0D));
    private final ServerBossEvent dragonEvent = (ServerBossEvent) (new ServerBossEvent(Component.translatable("entity.sas.cthulhu"), BossEvent.BossBarColor.PINK, BossEvent.BossBarOverlay.PROGRESS)).setPlayBossMusic(true).setCreateWorldFog(true);
    private final ServerLevel level;
    private final ObjectArrayList<Integer> gateways = new ObjectArrayList<>();
    private final BlockPattern exitPortalPattern;
    private int ticksSinceDragonSeen;
    private int crystalsAlive;
    private int ticksSinceCrystalsScanned;
    private int ticksSinceLastPlayerScan;
    private boolean dragonKilled;
    private boolean previouslyKilled;
    @Nullable
    private UUID dragonUUID;
    private boolean needsStateScanning = true;
    @Nullable
    private BlockPos portalLocation;
    private int respawnTime;
    @Nullable
    private List<EndCrystal> respawnCrystals;

    public CthulhuFight(ServerLevel pLevel, long pSeed, CompoundTag pTag) {
        this.level = pLevel;
        if (pTag.contains("NeedsStateScanning")) {
            this.needsStateScanning = pTag.getBoolean("NeedsStateScanning");
        }

        if (pTag.contains("DragonKilled", 99)) {
            if (pTag.hasUUID("Dragon")) {
                this.dragonUUID = pTag.getUUID("Dragon");
            }

            this.dragonKilled = pTag.getBoolean("DragonKilled");
            this.previouslyKilled = pTag.getBoolean("PreviouslyKilled");
            this.needsStateScanning = !pTag.getBoolean("LegacyScanPerformed"); // Forge: fix MC-105080

            if (pTag.contains("ExitPortalLocation", 10)) {
                this.portalLocation = NbtUtils.readBlockPos(pTag.getCompound("ExitPortalLocation"));
            }
        } else {
            this.dragonKilled = true;
            this.previouslyKilled = true;
        }

        if (pTag.contains("Gateways", 9)) {
            ListTag listtag = pTag.getList("Gateways", 3);

            for (int i = 0; i < listtag.size(); ++i) {
                this.gateways.add(listtag.getInt(i));
            }
        } else {
            this.gateways.addAll(ContiguousSet.create(Range.closedOpen(0, 20), DiscreteDomain.integers()));
            Util.shuffle(this.gateways, RandomSource.create(pSeed));
        }

        this.exitPortalPattern = BlockPatternBuilder.start()
                .aisle(
                        "       ",
                        "       ",
                        "       ",
                        "   #   ",
                        "       ",
                        "       ",
                        "       ")
                .aisle(
                        "       ",
                        "       ",
                        "       ",
                        "   #   ",
                        "       ",
                        "       ",
                        "       ")
                .aisle(
                        "       ",
                        "       ",
                        "       ",
                        "   #   ",
                        "       ",
                        "       ",
                        "       ")
                .aisle(
                        "  ###  ",
                        " #   # ",
                        "#     #",
                        "#  #  #",
                        "#     #",
                        " #   # ",
                        "  ###  ")
                .aisle(
                        "       ",
                        "  ###  ",
                        " ##### ",
                        " ##### ",
                        " ##### ",
                        "  ###  ",
                        "       ")
                .where('#', BlockInWorld.hasState(BlockPredicate.forBlock(Blocks.BEDROCK))).build();
    }

    public CompoundTag saveData(CompoundTag tag) {
        tag.putBoolean("NeedsStateScanning", this.needsStateScanning);
        if (this.dragonUUID != null) {
            tag.putUUID("Dragon", this.dragonUUID);
        }

        tag.putBoolean("DragonKilled", this.dragonKilled);
        tag.putBoolean("PreviouslyKilled", this.previouslyKilled);
        tag.putBoolean("LegacyScanPerformed", !this.needsStateScanning); // Forge: fix MC-105080
        if (this.portalLocation != null) {
            tag.put("ExitPortalLocation", NbtUtils.writeBlockPos(this.portalLocation));
        }

        ListTag listtag = new ListTag();

        for (int i : this.gateways) {
            listtag.add(IntTag.valueOf(i));
        }

        tag.put("Gateways", listtag);
        return tag;
    }

    public void tick() {
        this.dragonEvent.setVisible(!this.dragonKilled);
        if (++this.ticksSinceLastPlayerScan >= 20) {
            this.updatePlayers();
            this.ticksSinceLastPlayerScan = 0;
        }

        if (!this.dragonEvent.getPlayers().isEmpty()) {
            this.level.getChunkSource().addRegionTicket(TicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
            boolean flag = this.isArenaLoaded();
            if (this.needsStateScanning && flag) {
                this.scanState();
                this.needsStateScanning = false;
            }

            if (!this.dragonKilled) {
                if ((this.dragonUUID == null || ++this.ticksSinceDragonSeen >= MAX_TICKS_BEFORE_DRAGON_RESPAWN) && flag) {
                    this.findOrCreateDragon();
                    this.ticksSinceDragonSeen = 0;
                }

                if (++this.ticksSinceCrystalsScanned >= TIME_BETWEEN_CRYSTAL_SCANS && flag) {
                    this.updateCrystalCount();
                    this.ticksSinceCrystalsScanned = 0;
                }
            }
        } else {
            this.level.getChunkSource().removeRegionTicket(TicketType.DRAGON, new ChunkPos(0, 0), 9, Unit.INSTANCE);
        }

    }

    private void scanState() {
        LOGGER.info("Scanning for legacy world dragon fight...");
        boolean flag = this.hasActiveExitPortal();
        if (flag) {
            LOGGER.info("Found that the dragon has been killed in this world already.");
            this.previouslyKilled = true;
        } else {
            LOGGER.info("Found that the dragon has not yet been killed in this world.");
            this.previouslyKilled = false;
            if (this.findExitPortal() == null) {
                this.spawnExitPortal(false);
            }
        }

        List<? extends EnderDragon> list = this.level.getDragons();
        if (list.isEmpty()) {
            this.dragonKilled = true;
        } else {
            EnderDragon enderdragon = list.get(0);
            this.dragonUUID = enderdragon.getUUID();
            LOGGER.info("Found that there's a dragon still alive ({})", (Object) enderdragon);
            this.dragonKilled = false;
            if (!flag) {
                LOGGER.info("But we didn't have a portal, let's remove it.");
                enderdragon.discard();
                this.dragonUUID = null;
            }
        }

        if (!this.previouslyKilled && this.dragonKilled) {
            this.dragonKilled = false;
        }

    }

    private void findOrCreateDragon() {
        List<? extends EnderDragon> list = this.level.getDragons();
        if (list.isEmpty()) {
            LOGGER.debug("Haven't seen the dragon, respawning it");
            this.createNewDragon();
        } else {
            LOGGER.debug("Haven't seen our dragon, but found another one to use.");
            this.dragonUUID = list.get(0).getUUID();
        }

    }

    private boolean hasActiveExitPortal() {
        for (int i = -8; i <= 8; ++i) {
            for (int j = -8; j <= 8; ++j) {
                LevelChunk levelchunk = this.level.getChunk(i, j);

                for (BlockEntity blockentity : levelchunk.getBlockEntities().values()) {
                    if (blockentity instanceof TheEndPortalBlockEntity) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Nullable
    private BlockPattern.BlockPatternMatch findExitPortal() {
        for (int i = -8; i <= 8; ++i) {
            for (int j = -8; j <= 8; ++j) {
                LevelChunk levelchunk = this.level.getChunk(i, j);

                for (BlockEntity blockentity : levelchunk.getBlockEntities().values()) {
                    if (blockentity instanceof TheEndPortalBlockEntity) {
                        BlockPattern.BlockPatternMatch blockpattern$blockpatternmatch = this.exitPortalPattern.find(this.level, blockentity.getBlockPos());
                        if (blockpattern$blockpatternmatch != null) {
                            BlockPos blockpos = blockpattern$blockpatternmatch.getBlock(3, 3, 3).getPos();
                            if (this.portalLocation == null) {
                                this.portalLocation = blockpos;
                            }

                            return blockpattern$blockpatternmatch;
                        }
                    }
                }
            }
        }

        int k = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION).getY();

        for (int l = k; l >= this.level.getMinBuildHeight(); --l) {
            BlockPattern.BlockPatternMatch blockpattern$blockpatternmatch1 = this.exitPortalPattern.find(this.level, new BlockPos(EndPodiumFeature.END_PODIUM_LOCATION.getX(), l, EndPodiumFeature.END_PODIUM_LOCATION.getZ()));
            if (blockpattern$blockpatternmatch1 != null) {
                if (this.portalLocation == null) {
                    this.portalLocation = blockpattern$blockpatternmatch1.getBlock(3, 3, 3).getPos();
                }

                return blockpattern$blockpatternmatch1;
            }
        }

        return null;
    }

    private boolean isArenaLoaded() {
        for (int i = -8; i <= 8; ++i) {
            for (int j = 8; j <= 8; ++j) {
                ChunkAccess chunkaccess = this.level.getChunk(i, j, ChunkStatus.FULL, false);
                if (!(chunkaccess instanceof LevelChunk)) {
                    return false;
                }

                FullChunkStatus chunkholder$fullchunkstatus = ((LevelChunk) chunkaccess).getFullStatus();
                if (!chunkholder$fullchunkstatus.isOrAfter(FullChunkStatus.ENTITY_TICKING)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void updatePlayers() {
        Set<ServerPlayer> set = Sets.newHashSet();

        for (ServerPlayer serverplayer : this.level.getPlayers(VALID_PLAYER)) {
            this.dragonEvent.addPlayer(serverplayer);
            set.add(serverplayer);
        }

        Set<ServerPlayer> set1 = Sets.newHashSet(this.dragonEvent.getPlayers());
        set1.removeAll(set);

        for (ServerPlayer serverplayer1 : set1) {
            this.dragonEvent.removePlayer(serverplayer1);
        }

    }

    private void updateCrystalCount() {
        this.ticksSinceCrystalsScanned = 0;
        this.crystalsAlive = 0;

        for (SpikeFeature.EndSpike spikefeature$endspike : SpikeFeature.getSpikesForLevel(this.level)) {
            this.crystalsAlive += this.level.getEntitiesOfClass(EndCrystal.class, spikefeature$endspike.getTopBoundingBox()).size();
        }

        LOGGER.debug("Found {} end crystals still alive", (int) this.crystalsAlive);
    }

    public void setDragonKilled(EnderDragon pDragon) {
        if (pDragon.getUUID().equals(this.dragonUUID)) {
            this.dragonEvent.setProgress(0.0F);
            this.dragonEvent.setVisible(false);
            this.spawnExitPortal(true);
            this.spawnNewGateway();
            if (!this.previouslyKilled) {
                this.level.setBlockAndUpdate(this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION), Blocks.DRAGON_EGG.defaultBlockState());
            }

            this.previouslyKilled = true;
            this.dragonKilled = true;
        }

    }

    private void spawnNewGateway() {
        if (!this.gateways.isEmpty()) {
            int i = this.gateways.remove(this.gateways.size() - 1);
            int j = Mth.floor(GATEWAY_DISTANCE * Math.cos(2.0D * (-Math.PI + 0.15707963267948966D * (double) i)));
            int k = Mth.floor(GATEWAY_DISTANCE * Math.sin(2.0D * (-Math.PI + 0.1570796326794896E6D * (double) i)));
            this.spawnNewGateway(new BlockPos(j, 75, k));
        }
    }

    private void spawnNewGateway(BlockPos pPos) {
        this.level.levelEvent(3000, pPos, 0);
        ConfiguredFeature<?, ?> gateway = ServerLifecycleHooks.getCurrentServer().registryAccess().registryOrThrow(Registries.CONFIGURED_FEATURE).get(EndFeatures.END_GATEWAY_DELAYED);
        gateway.place(this.level, this.level.getChunkSource().getGenerator(), RandomSource.create(), pPos);
    }

    private void spawnExitPortal(boolean pActive) {
        EndPodiumFeature endpodiumfeature = new EndPodiumFeature(pActive);
        if (this.portalLocation == null) {
            for (this.portalLocation = this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION).below(); this.level.getBlockState(this.portalLocation).is(Blocks.BEDROCK) && this.portalLocation.getY() > this.level.getSeaLevel(); this.portalLocation = this.portalLocation.below()) {
            }
        }

        endpodiumfeature.place(FeatureConfiguration.NONE, this.level, this.level.getChunkSource().getGenerator(), RandomSource.create(), this.portalLocation);
    }

    private EnderDragon createNewDragon() {
        this.level.getChunkAt(new BlockPos(0, 128, 0));
        EnderDragon enderdragon = EntityType.ENDER_DRAGON.create(this.level);
        enderdragon.getPhaseManager().setPhase(EnderDragonPhase.HOLDING_PATTERN);
        enderdragon.moveTo(0.0D, 128.0D, 0.0D, this.level.random.nextFloat() * 360.0F, 0.0F);
        this.level.addFreshEntity(enderdragon);
        this.dragonUUID = enderdragon.getUUID();
        return enderdragon;
    }

    public void updateDragon(EnderDragon pDragon) {
        if (pDragon.getUUID().equals(this.dragonUUID)) {
            this.dragonEvent.setProgress(pDragon.getHealth() / pDragon.getMaxHealth());
            this.ticksSinceDragonSeen = 0;
            if (pDragon.hasCustomName()) {
                this.dragonEvent.setName(pDragon.getDisplayName());
            }
        }

    }

    public int getCrystalsAlive() {
        return this.crystalsAlive;
    }

    public void onCrystalDestroyed(EndCrystal pCrystal, DamageSource pDmgSrc) {
        this.updateCrystalCount();
        Entity entity = this.level.getEntity(this.dragonUUID);
        if (entity instanceof EnderDragon) {
            ((EnderDragon) entity).onCrystalDestroyed(pCrystal, pCrystal.blockPosition(), pDmgSrc);
        }
    }

    public void resetSpikeCrystals() {
        for (SpikeFeature.EndSpike spikefeature$endspike : SpikeFeature.getSpikesForLevel(this.level)) {
            for (EndCrystal endcrystal : this.level.getEntitiesOfClass(EndCrystal.class, spikefeature$endspike.getTopBoundingBox())) {
                endcrystal.setInvulnerable(false);
                endcrystal.setBeamTarget((BlockPos) null);
            }
        }
    }

    public void addPlayer(ServerPlayer player) {
        this.dragonEvent.addPlayer(player);
    }

    public void removePlayer(ServerPlayer player) {
        this.dragonEvent.removePlayer(player);
    }
}

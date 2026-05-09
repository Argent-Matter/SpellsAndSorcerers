package dev.screret.motm.common.util;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.block.entity.PortStoneBlockEntity;
import dev.screret.motm.common.block.entity.PortStoneBlockEntity.PortRune;
import dev.screret.motm.data.block.entity.MOTMBlockEntities;
import dev.screret.motm.data.block.MOTMPoiTypes;

import net.minecraft.core.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.BlockGetter;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.world.chunk.RegisterTicketControllersEvent;
import net.neoforged.neoforge.common.world.chunk.TicketController;
import net.neoforged.neoforge.common.world.chunk.TicketHelper;

import com.mojang.datafixers.util.Pair;

import java.util.EnumSet;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public class PortStoneHelper {

    // search for matching port runes in a range of 64 chunks
    private static final int POI_SEARCH_RADIUS = 64;
    private static TicketController FORCELOADING_TICKET_CONTROLLER;

    public static final EnumSet<RelativeMovement> NONE_RELATIVE = EnumSet.noneOf(RelativeMovement.class);

    /**
     * Forceload a port stone at {@code pos}.
     * 
     * @param level The {@link ServerLevel} to forceload in.
     * @param pos   The position of the (now loaded) block.
     * @return Whether loading was successful.
     */
    public static boolean forceLoadPortStone(ServerLevel level, BlockPos pos) {
        if (!isValidPortStone(level, pos)) {
            return false;
        }

        int chunkX = SectionPos.blockToSectionCoord(pos.getX());
        int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
        return FORCELOADING_TICKET_CONTROLLER.forceChunk(level, pos, chunkX, chunkZ, true, false);
    }

    /**
     * Un-forceload a port stone at {@code pos}.
     * 
     * @param level The {@link ServerLevel} to forceload in.
     * @param pos   The position of the (no longer loaded) block.
     * @return Whether removal was successful.
     */
    public static boolean unLoadPortStone(ServerLevel level, BlockPos pos) {
        if (!isValidPortStone(level, pos)) {
            return false;
        }

        int chunkX = SectionPos.blockToSectionCoord(pos.getX());
        int chunkZ = SectionPos.blockToSectionCoord(pos.getZ());
        return FORCELOADING_TICKET_CONTROLLER.forceChunk(level, pos, chunkX, chunkZ, false, false);
    }

    /**
     * Finds the nearest portway entrance on the {@link Direction#getOpposite() entranceFace.getOpposite()} face of the block at
     * {@link PortRune#destination() entrance.destination()} that can link to {@code entrance} and, if it isn't generated yet,
     * generates it.
     * 
     * @param currentLevel The {@link ServerLevel} to look for valid entrances/exits in.
     * @param entrancePos  The position that {@code entrance} is at.
     * @param entranceFace The (horizontal) block face that {@code entrance} is on. Note that the exit must be on the opposite
     *                     face.
     * @param entrance     The entrance to find a matching exit for.
     * @return A valid portway entrance, or {@link Optional#empty() Optional.empty()} if none were found.
     */
    public static Optional<PortRune> findDestination(ServerLevel currentLevel, BlockPos entrancePos, Direction entranceFace,
                                                     PortRune entrance) {
        GlobalPos destination = entrance.destination();
        final ServerLevel destinationLevel = getDestinationLevel(currentLevel.getServer(), destination);
        if (destinationLevel == null) {
            return Optional.empty();
        }
        final Direction destinationFace = entranceFace.getOpposite();

        // noinspection deprecation
        return destinationLevel.getPoiManager().findAllClosestFirstWithType(poi -> poi.is(MOTMPoiTypes.PORT_STONE), p -> true,
                destination.pos(), POI_SEARCH_RADIUS, PoiManager.Occupancy.ANY)
                .map(poi -> destinationLevel.getBlockEntity(poi.getSecond(), MOTMBlockEntities.PORT_STONE.get()))
                .flatMap(Optional::stream)
                // TODO add condition for matching runes once those are added
                .map(portStone -> Pair.of(portStone, portStone.getRuneOnFace(destinationFace).orElse(null)))
                .filter(pair -> {
                    PortRune rune = pair.getSecond();
                    // it has to exist
                    if (rune == null) return false;
                    // it must not also be an 'entrance'
                    if (rune.isExit() == entrance.isExit()) return false;
                    // its destination must be in the entrance's dimension
                    if (rune.destination().dimension() != currentLevel.dimension()) return false;
                    // filter out mismatched destinations that have already been reassigned (or otherwise checked)
                    if (!rune.destination().pos().equals(entrancePos)) return !pair.getFirst().isFaceChecked(destinationFace);
                    return true;
                })
                // `findAllClosestFirstWithType` returns a stream that's sorted by distance
                // so we can just get the first entry and assume it's the closest one.
                .findFirst()
                .map(pair -> {
                    PortRune rune = pair.getSecond();
                    // fix mismatched destinations
                    // already matched destinations won't get this far
                    if (!rune.destination().pos().equals(entrancePos)) {
                        PortStoneBlockEntity portStone = pair.getFirst();
                        rune = rune.withDestinationPos(entrancePos);
                        portStone.setRuneOnFace(destinationFace, rune);
                    }
                    return rune;
                });
    }

    @SuppressWarnings("ConstantValue")
    public static @Nullable ServerLevel getDestinationLevel(MinecraftServer server, GlobalPos pos) {
        if (server == null) {
            MagicOfTheMind.LOGGER.error("Cannot track portways on the client");
            return null;
        }
        ServerLevel level = server.getLevel(pos.dimension());
        if (level == null) {
            MagicOfTheMind.LOGGER.error("Cannot track portways in nonexistent (unloaded?) dimension '{}'",
                    pos.dimension().location());
            return null;
        }
        return level;
    }

    private static boolean isValidPortStone(BlockGetter level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof PortStoneBlockEntity)) {
            MagicOfTheMind.LOGGER.error("Only port stones may be forceloaded with PortStoneHelper!");
            return false;
        }
        return true;
    }

    @SubscribeEvent
    public static void registerTicketController(RegisterTicketControllersEvent event) {
        FORCELOADING_TICKET_CONTROLLER = new TicketController(MOTMUtil.id("port_stones"), PortStoneHelper::validateLoadedChunks);

        event.register(FORCELOADING_TICKET_CONTROLLER);
    }

    private static void validateLoadedChunks(ServerLevel level, TicketHelper ticketHelper) {
        MagicOfTheMind.LOGGER.debug("Validating chunk tickets for level {}", level.dimension().location());

        ticketHelper.getBlockTickets().forEach((pos, chunks) -> {
            MagicOfTheMind.LOGGER.debug("Validating {} chunkloading tickets for {}", chunks.ticking().size(), pos);

            // clean up all invalid tickets (ones with no port stone POI at them)
            if (!level.getPoiManager().existsAtPosition(MOTMPoiTypes.PORT_STONE.getKey(), pos)) {
                ticketHelper.removeAllTickets(pos);
                MagicOfTheMind.LOGGER.info("Cleaned up {} invalid chunkloading tickets {} in dimension {}",
                        chunks.nonTicking().size() + chunks.ticking().size(), pos, level.dimension().location());
            }
        });
    }
}

package dev.screret.motm.api.util;

import dev.screret.motm.core.mixin.vanilla.StructureTemplateAccessor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Clearable;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BitSetDiscreteVoxelShape;
import net.minecraft.world.phys.shapes.DiscreteVoxelShape;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate.*;

public class StructureUtil {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Direction[] ALL_DIRECTIONS_EXCEPT_DOWN = { Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH,
            Direction.WEST };

    public static boolean placeInWorld(StructureTemplate structure, Level level, BlockPos offset, BlockPos pos,
                                       StructurePlaceSettings settings, RandomSource random, int flags) {
        StructureTemplateAccessor accessor = (StructureTemplateAccessor) structure;

        if (accessor.motm$getPalettes().isEmpty()) {
            return false;
        }
        Vec3i size = accessor.motm$getSize();
        if (size.getX() < 1 || size.getY() < 1 || size.getZ() < 1) {
            return false;
        }
        List<StructureTemplate.StructureBlockInfo> blockInfos = settings.getRandomPalette(accessor.motm$getPalettes(), offset)
                .blocks();
        if (blockInfos.isEmpty() && (settings.isIgnoreEntities() || accessor.motm$getEntityInfoList().isEmpty())) {
            return false;
        }

        BoundingBox bounds = settings.getBoundingBox();
        List<BlockPos> waterloggedBlocks = new ArrayList<>(settings.shouldApplyWaterlogging() ? blockInfos.size() : 0);
        List<BlockPos> fluidBlocks = new ArrayList<>(settings.shouldApplyWaterlogging() ? blockInfos.size() : 0);
        List<Pair<BlockPos, CompoundTag>> allBlocks = new ArrayList<>(blockInfos.size());
        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE, minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, maxZ = Integer.MIN_VALUE;

        for (StructureTemplate.StructureBlockInfo blockInfo : processBlockInfos(level, offset, pos, settings, blockInfos,
                structure)) {
            BlockPos blockpos = blockInfo.pos();
            if (bounds != null && !bounds.isInside(blockpos)) {
                continue;
            }
            FluidState fluidState = settings.shouldApplyWaterlogging() ? level.getFluidState(blockpos) : null;
            BlockState blockState = blockInfo.state().mirror(settings.getMirror()).rotate(settings.getRotation());
            if (blockInfo.nbt() != null) {
                BlockEntity blockentity = level.getBlockEntity(blockpos);
                Clearable.tryClear(blockentity);
                level.setBlock(blockpos, Blocks.BARRIER.defaultBlockState(), Block.UPDATE_INVISIBLE | Block.UPDATE_KNOWN_SHAPE);
            }

            if (!level.setBlock(blockpos, blockState, flags)) {
                continue;
            }
            minX = Math.min(minX, blockpos.getX());
            minY = Math.min(minY, blockpos.getY());
            minZ = Math.min(minZ, blockpos.getZ());
            maxX = Math.max(maxX, blockpos.getX());
            maxY = Math.max(maxY, blockpos.getY());
            maxZ = Math.max(maxZ, blockpos.getZ());

            allBlocks.add(Pair.of(blockpos, blockInfo.nbt()));
            if (blockInfo.nbt() != null) {
                BlockEntity blockEntity = level.getBlockEntity(blockpos);
                if (blockEntity != null) {
                    if (blockEntity instanceof RandomizableContainer) {
                        blockInfo.nbt().putLong("LootTableSeed", random.nextLong());
                    }

                    blockEntity.loadWithComponents(blockInfo.nbt(), level.registryAccess());
                }
            }

            if (fluidState != null) {
                if (blockState.getFluidState().isSource()) {
                    fluidBlocks.add(blockpos);
                } else if (blockState.getBlock() instanceof LiquidBlockContainer waterloggableBlock) {
                    waterloggableBlock.placeLiquid(level, blockpos, blockState, fluidState);
                    if (!fluidState.isSource()) {
                        waterloggedBlocks.add(blockpos);
                    }
                }
            }
        }

        boolean hasWaterloggedBlocks = true;
        while (hasWaterloggedBlocks && !waterloggedBlocks.isEmpty()) {
            hasWaterloggedBlocks = false;
            Iterator<BlockPos> iterator = waterloggedBlocks.iterator();

            while (iterator.hasNext()) {
                BlockPos curPos = iterator.next();
                FluidState fluidState = level.getFluidState(curPos);

                for (Direction side : ALL_DIRECTIONS_EXCEPT_DOWN) {
                    if (fluidState.isSource()) break;

                    BlockPos relative = curPos.relative(side);
                    FluidState relativeFluid = level.getFluidState(relative);
                    if (relativeFluid.isSource() && !fluidBlocks.contains(relative)) {
                        fluidState = relativeFluid;
                    }
                }

                if (fluidState.isSource()) {
                    BlockState blockState = level.getBlockState(curPos);
                    if (blockState.getBlock() instanceof LiquidBlockContainer waterloggableBlock) {
                        waterloggableBlock.placeLiquid(level, curPos, blockState, fluidState);
                        hasWaterloggedBlocks = true;
                        iterator.remove();
                    }
                }
            }
        }

        if (minX <= maxX) {
            if (!settings.getKnownShape()) {
                DiscreteVoxelShape shape = new BitSetDiscreteVoxelShape(maxX - minX + 1, maxY - minY + 1, maxZ - minZ + 1);

                for (Pair<BlockPos, CompoundTag> entry : allBlocks) {
                    BlockPos curPos = entry.getFirst();
                    shape.fill(curPos.getX() - minX, curPos.getY() - minY, curPos.getZ() - minZ);
                }
                updateShapeAtEdge(level, flags, shape, minX, minY, minZ);
            }
            for (Pair<BlockPos, CompoundTag> entry : allBlocks) {
                BlockPos curPos = entry.getFirst();
                if (!settings.getKnownShape()) {
                    BlockState blockstate2 = level.getBlockState(curPos);
                    BlockState blockstate3 = Block.updateFromNeighbourShapes(blockstate2, level, curPos);
                    if (blockstate2 != blockstate3) {
                        level.setBlock(curPos, blockstate3, flags & -2 | 16);
                    }

                    level.blockUpdated(curPos, blockstate3.getBlock());
                }

                if (entry.getSecond() != null) {
                    BlockEntity blockEntity = level.getBlockEntity(curPos);
                    if (blockEntity != null) {
                        blockEntity.setChanged();
                    }
                }
            }
        }

        if (!settings.isIgnoreEntities()) {
            addEntitiesToWorld(structure, level, offset, settings);
        }

        return true;
    }

    public static List<StructureTemplate.StructureBlockInfo> processBlockInfos(LevelReader level, BlockPos offset, BlockPos pos,
                                                                               StructurePlaceSettings settings,
                                                                               List<StructureTemplate.StructureBlockInfo> blockInfos,
                                                                               @Nullable StructureTemplate template) {
        List<StructureTemplate.StructureBlockInfo> originalBlockInfos = new ArrayList<>();
        List<StructureTemplate.StructureBlockInfo> processedBlockInfos = new ArrayList<>();

        for (StructureTemplate.StructureBlockInfo blockInfo : blockInfos) {
            BlockPos relative = calculateRelativePosition(settings, blockInfo.pos()).offset(offset);
            StructureTemplate.StructureBlockInfo processed = new StructureTemplate.StructureBlockInfo(relative,
                    blockInfo.state(), blockInfo.nbt() != null ? blockInfo.nbt().copy() : null);

            for (StructureProcessor processor : settings.getProcessors()) {
                processed = processor.process(level, offset, pos, blockInfo, processed, settings, template);
                if (processed == null) break;
            }

            if (processed != null) {
                processedBlockInfos.add(processed);
                originalBlockInfos.add(blockInfo);
            }
        }

        if (level instanceof ServerLevelAccessor serverLevel) {
            for (StructureProcessor structureprocessor : settings.getProcessors()) {
                processedBlockInfos = structureprocessor.finalizeProcessing(serverLevel, offset, pos,
                        originalBlockInfos, processedBlockInfos, settings);
            }
        }

        return processedBlockInfos;
    }

    public static void addEntitiesToWorld(StructureTemplate template, Level level, BlockPos blockPos,
                                          StructurePlaceSettings settings) {
        for (StructureTemplate.StructureEntityInfo entityInfo : processEntityInfos(template, level, blockPos, settings,
                ((StructureTemplateAccessor) template).motm$getEntityInfoList())) {
            BlockPos entityBlockPos = entityInfo.blockPos; // FORGE: Position will have already been transformed by
                                                           // processEntityInfos
            if (settings.getBoundingBox() == null || settings.getBoundingBox().isInside(entityBlockPos)) {
                CompoundTag nbt = entityInfo.nbt.copy();
                Vec3 pos = entityInfo.pos; // FORGE: Position will have already been transformed by processEntityInfos
                nbt.put("Pos", Vec3.CODEC.encodeStart(NbtOps.INSTANCE, pos).getOrThrow());
                nbt.remove("UUID");
                createEntityIgnoreException(level, nbt).ifPresent(entity -> {
                    float rotation = entity.rotate(settings.getRotation());
                    rotation += entity.mirror(settings.getMirror()) - entity.getYRot();
                    entity.moveTo(pos.x, pos.y, pos.z, rotation, entity.getXRot());
                    if (settings.shouldFinalizeEntities() && level instanceof ServerLevelAccessor serverLevel &&
                            entity instanceof Mob mob) {
                        mob.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(BlockPos.containing(pos)),
                                MobSpawnType.STRUCTURE, null);
                    }

                    addFreshEntityWithPassengers(level, entity);
                });
            }
        }
    }

    public static List<StructureTemplate.StructureEntityInfo> processEntityInfos(StructureTemplate template,
                                                                                 LevelAccessor level, BlockPos blockPos,
                                                                                 StructurePlaceSettings settings,
                                                                                 List<StructureTemplate.StructureEntityInfo> entityInfos) {
        List<StructureTemplate.StructureEntityInfo> processedEntityInfos = Lists.newArrayList();
        for (StructureTemplate.StructureEntityInfo entityInfo : entityInfos) {
            Vec3 pos = transformedVec3d(settings, entityInfo.pos).add(Vec3.atLowerCornerOf(blockPos));
            BlockPos relative = calculateRelativePosition(settings, entityInfo.blockPos).offset(blockPos);
            StructureTemplate.StructureEntityInfo info = new StructureTemplate.StructureEntityInfo(pos, relative, entityInfo.nbt);
            for (StructureProcessor proc : settings.getProcessors()) {
                info = proc.processEntity(level, blockPos, entityInfo, info, settings, template);
                // noinspection ConstantValue
                if (info == null) break;
            }
            // noinspection ConstantValue
            if (info != null) processedEntityInfos.add(info);
        }
        return processedEntityInfos;
    }

    public static Optional<Entity> createEntityIgnoreException(Level level, CompoundTag tag) {
        try {
            return EntityType.create(tag, level);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    public static void addFreshEntityWithPassengers(LevelAccessor level, Entity entity) {
        entity.getSelfAndPassengers().forEach(level::addFreshEntity);
    }
}

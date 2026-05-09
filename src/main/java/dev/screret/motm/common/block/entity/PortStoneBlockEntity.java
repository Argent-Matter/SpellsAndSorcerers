package dev.screret.motm.common.block.entity;

import dev.screret.motm.MagicOfTheMind;
import dev.screret.motm.common.item.component.PortStoneRunes;
import dev.screret.motm.common.util.MathUtil;
import dev.screret.motm.common.util.PortStoneHelper;
import dev.screret.motm.data.block.entity.MOTMBlockEntities;
import dev.screret.motm.data.item.MOTMDataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.DimensionTransition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.With;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class PortStoneBlockEntity extends BlockEntity {

    @Getter
    protected @Nullable PortRune north, east, south, west;
    @Getter
    @Setter(AccessLevel.PROTECTED)
    protected boolean northChecked, eastChecked, southChecked, westChecked;

    public PortStoneBlockEntity(BlockPos pos, BlockState blockState) {
        super(MOTMBlockEntities.PORT_STONE.get(), pos, blockState);
    }

    public Optional<PortRune> getRuneOnFace(Direction face) {
        return switch (face) {
            case NORTH -> Optional.ofNullable(north);
            case EAST -> Optional.ofNullable(east);
            case SOUTH -> Optional.ofNullable(south);
            case WEST -> Optional.ofNullable(west);
            default -> Optional.empty();
        };
    }

    public boolean isFaceChecked(Direction face) {
        return switch (face) {
            case NORTH -> this.northChecked;
            case EAST -> this.eastChecked;
            case SOUTH -> this.southChecked;
            case WEST -> this.westChecked;
            default -> {
                MagicOfTheMind.LOGGER.error("Runes cannot be placed on the {} face of a port stone", face.getName());
                yield false;
            }
        };
    }

    public void setRuneOnFace(Direction face, @Nullable PortRune rune) {
        switch (face) {
            case NORTH -> {
                this.north = rune;
                this.northChecked = rune != null;
            }
            case EAST -> {
                this.east = rune;
                this.eastChecked = rune != null;
            }
            case SOUTH -> {
                this.south = rune;
                this.southChecked = rune != null;
            }
            case WEST -> {
                this.west = rune;
                this.westChecked = rune != null;
            }
            default -> MagicOfTheMind.LOGGER.error("Runes cannot be placed on the {} face of a port stone", face.getName());
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(MOTMDataComponents.PORT_STONE_RUNES, new PortStoneRunes(this.north, this.east, this.south, this.west));
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        PortStoneRunes runes = componentInput.get(MOTMDataComponents.PORT_STONE_RUNES);
        if (runes != null) {
            setRuneOnFace(Direction.NORTH, runes.north());
            setRuneOnFace(Direction.EAST, runes.east());
            setRuneOnFace(Direction.SOUTH, runes.south());
            setRuneOnFace(Direction.WEST, runes.west());
        }
    }

    public Optional<PortRune> getDestination(Direction face) {
        if (!(this.getLevel() instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }
        return getRuneOnFace(face).flatMap(rune -> PortStoneHelper.findDestination(serverLevel, this.getBlockPos(), face, rune));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        {
            CompoundTag runesTag = new CompoundTag();
            if (this.north != null) runesTag.put("north", this.north.save(this.northChecked));
            if (this.east != null) runesTag.put("east", this.east.save(this.eastChecked));
            if (this.south != null) runesTag.put("south", this.south.save(this.southChecked));
            if (this.west != null) runesTag.put("west", this.west.save(this.westChecked));
            tag.put("runes", runesTag);
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        {
            CompoundTag runesTag = tag.getCompound("runes");
            this.north = PortRune.load(runesTag.getCompound("north"), this::setNorthChecked);
            this.east = PortRune.load(runesTag.getCompound("east"), this::setEastChecked);
            this.south = PortRune.load(runesTag.getCompound("south"), this::setSouthChecked);
            this.west = PortRune.load(runesTag.getCompound("west"), this::setWestChecked);
        }
    }

    public record PortRune(@With GlobalPos destination, boolean isExit) {

        // spotless:off
        public static final Codec<PortRune> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                GlobalPos.CODEC.fieldOf("destination").forGetter(PortRune::destination),
                Codec.BOOL.fieldOf("is_exit").forGetter(PortRune::isExit)
        ).apply(instance, PortRune::new));
        public static final StreamCodec<ByteBuf, PortRune> STREAM_CODEC = StreamCodec.composite(
                GlobalPos.STREAM_CODEC, PortRune::destination,
                ByteBufCodecs.BOOL, PortRune::isExit,
                PortRune::new
        );
        // spotless:off

        public PortRune(ResourceKey<Level> dimension, BlockPos pos, boolean isExit) {
            this(GlobalPos.of(dimension, pos), isExit);
        }

        public PortRune withDestinationPos(BlockPos pos) {
            return withDestination(GlobalPos.of(this.destination().dimension(), pos));
        }

        public @Nullable DimensionTransition makeTeleport(Entity entity, Direction entryFace, ServerLevel level) {
            ServerLevel destinationLevel = PortStoneHelper.getDestinationLevel(level.getServer(), this.destination);
            if (destinationLevel == null) {
                return null;
            }
            return new DimensionTransition(destinationLevel,
                    this.destination.pos().relative(entryFace.getOpposite()).getBottomCenter(),
                    MathUtil.reflectVec3(entity.getDeltaMovement(), entryFace.step()),
                    entity.getYRot(), entity.getXRot(),
                    DimensionTransition.DO_NOTHING);
        }

        public Tag save(boolean checked) {
            CompoundTag prefix = new CompoundTag();
            prefix.putBoolean("checked", checked);
            return PortRune.CODEC.encode(this, NbtOps.INSTANCE, prefix).getOrThrow();
        }

        public static @Nullable PortRune load(@Nullable CompoundTag tag, BooleanConsumer checkedSetter) {
            if (tag == null) return null;
            PortRune result = PortRune.CODEC.parse(NbtOps.INSTANCE, tag).result().orElse(null);
            checkedSetter.accept(result != null && tag.getBoolean("checked"));
            return result;
        }
    }
}

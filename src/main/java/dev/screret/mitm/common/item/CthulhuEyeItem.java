package dev.screret.mitm.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

import dev.screret.mitm.config.MITMConfig;
import dev.screret.mitm.common.data.EyeConversionManager;

public class CthulhuEyeItem extends Item {
    public CthulhuEyeItem() {
        super(new Properties());
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!MITMConfig.Server.enableQthulhuEyeConversion.get()) {
            return super.useOn(context);
        }
        Level level = context.getLevel();
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }

        Player player = context.getPlayer();
        BlockPos blockpos = context.getClickedPos();
        if (!level.mayInteract(player, blockpos)) {
            return InteractionResult.PASS;
        }
        for (var conversionEntry : EyeConversionManager.INSTANCE.getAllConversions()) {
            BlockState originalState = level.getBlockState(blockpos);
            if (!conversionEntry.getValue().test(originalState)) {
                continue;
            }
            copyProperties(level, blockpos, conversionEntry.getKey().defaultBlockState(), originalState);
            RandomSource random = level.random;
            Vec3 pos = new Vec3(blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D);
            serverLevel.sendParticles(ParticleTypes.END_ROD,
                    pos.x,
                    pos.y,
                    pos.z,
                    32,
                    random.nextDouble(),
                    random.nextDouble(),
                    random.nextDouble(),
                    0.5D
            );
            serverLevel.sendParticles(ParticleTypes.TOTEM_OF_UNDYING,
                    pos.x,
                    pos.y,
                    pos.z,
                    32,
                    random.nextDouble(),
                    random.nextDouble(),
                    random.nextDouble(),
                    0.5D
            );
            return InteractionResult.CONSUME;
        }

        return super.useOn(context);
    }

    private static void copyProperties(Level level, BlockPos pos, BlockState toPlace, BlockState originalState) {
        var newState = toPlace;
        for (Property<?> property : originalState.getProperties()) {
            newState = copyProperty(originalState, newState, property);
        }
        level.setBlockAndUpdate(pos, newState);
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState from, BlockState to, Property<T> property) {
        return to.setValue(property, from.getValue(property));
    }
}

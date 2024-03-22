package screret.sas.item.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import screret.sas.SpellsAndSorcerers;
import screret.sas.config.SASConfig;
import screret.sas.resource.EyeConversionManager;

public class CthulhuEyeItem extends Item {
    public CthulhuEyeItem() {
        super(new Properties().tab(SpellsAndSorcerers.SAS_TAB));
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if(SASConfig.Server.enableQthulhuEyeConversion.get()){
            var player = pContext.getPlayer();
            var level = pContext.getLevel();
            BlockPos blockpos = pContext.getClickedPos();
            if (!level.mayInteract(player, blockpos)) {
                return InteractionResult.PASS;
            }

            for (var block : EyeConversionManager.INSTANCE.getAllConversions()){
                var originalState = level.getBlockState(blockpos);
                if(block.getValue().test(originalState)) {
                    copyProperties(level, blockpos, block.getKey().defaultBlockState(), originalState);
                    if(!level.isClientSide){
                        var serverLevel = (ServerLevel)level;
                        var random = level.random;
                        var pos = new Vec3(blockpos.getX() + 0.5D, blockpos.getY() + 0.5D, blockpos.getZ() + 0.5D);
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
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }

        }
        return super.useOn(pContext);
    }

    private static void copyProperties(Level level, BlockPos pos, BlockState toPlace, BlockState originalState){
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

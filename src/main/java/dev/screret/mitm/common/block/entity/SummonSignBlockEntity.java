package dev.screret.mitm.common.block.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import dev.screret.mitm.data.MITMTags;
import dev.screret.mitm.common.block.SummonSignBlock;
import dev.screret.mitm.data.MITMBlockEntities;
import dev.screret.mitm.data.MITMEntityTypes;
import dev.screret.mitm.common.entity.BossWizardEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SummonSignBlockEntity extends BlockEntity implements GeoBlockEntity {

    private static final int TICKS_TO_SPAWN = 100;
    public static final VoxelShape INSIDE = Block.box(-1D, 0.0D, -1D, 17.0D, 16.0D, 17.0D);
    public static final RawAnimation SUMMON = RawAnimation.begin().thenLoop("summon_sign.summon");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int ticksToSpawn = -1;
    private boolean hasSpawned = false;

    public SummonSignBlockEntity(BlockPos pos, BlockState blockState) {
        super(MITMBlockEntities.SUMMON_SIGN.get(), pos, blockState);
    }

    public static Set<ItemEntity> getItemsAt(Level level, SummonSignBlockEntity blockEntity) {
        return INSIDE.toAabbs().stream()
                .flatMap((bounds) -> level.getEntitiesOfClass(ItemEntity.class, bounds.move(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ()), EntitySelector.ENTITY_STILL_ALIVE).stream())
                .collect(Collectors.toSet());
    }

    public static boolean testForTag(Set<HolderSet.Named<Item>> tag, ItemStack stack, RequiredCounter counter) {
        if (stack != null) {
            for (var item : tag) {
                if (stack.is(item)) {
                    tag.remove(item);
                    return true;
                }
                counter.count++;
            }
        }
        return false;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(
            new AnimationController<>(this, 10, state -> state.setAndContinue(this.ticksToSpawn > 0 ? SUMMON : DefaultAnimations.IDLE))
        );
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private static class RequiredCounter {
        int count = 0;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SummonSignBlockEntity blockEntity) {
        if (pos.getY() >= level.getMinBuildHeight() && level.getDifficulty() != Difficulty.PEACEFUL) {
            var itemEntities = getItemsAt(level, blockEntity);
            if (!blockEntity.hasSpawned) {
                Stream<ItemStack> items = itemEntities.stream().map(ItemEntity::getItem);
                var requiredItems = BuiltInRegistries.ITEM.getTag(MITMTags.Items.BOSS_SUMMON_ITEMS);
                Set<HolderSet.Named<Item>> requiredSet = requiredItems.stream().collect(Collectors.toSet());
                var counter = new RequiredCounter();
                if (!items.allMatch(item -> testForTag(requiredSet, item, counter))) {
                    level.setBlockAndUpdate(pos, state.setValue(SummonSignBlock.TRIGGERED, false));
                    return;
                }
                if (itemEntities.size() < requiredSet.size()) {
                    level.setBlockAndUpdate(pos, state.setValue(SummonSignBlock.TRIGGERED, false));
                    return;
                }

                if (blockEntity.ticksToSpawn < 0) {
                    blockEntity.ticksToSpawn = TICKS_TO_SPAWN;
                    blockEntity.setChanged();
                    level.setBlockAndUpdate(pos, state.setValue(SummonSignBlock.TRIGGERED, true));
                    return;
                } else if (blockEntity.ticksToSpawn > 0) {
                    --blockEntity.ticksToSpawn;
                    blockEntity.setChanged();
                    return;
                }

                for (var itemEntity : itemEntities) {
                    itemEntity.getItem().shrink(1);
                }

                BossWizardEntity boss = MITMEntityTypes.BOSS_WIZARD.get().create(level);
                boss.setSpawningPosition(pos);
                boss.moveTo(pos.getX() + 0.5f, pos.getY() + 1.55D, pos.getZ() + 0.5f, 0.0F, 0.0F);
                boss.makeInvulnerable();
                for (ServerPlayer serverplayer : level.getEntitiesOfClass(ServerPlayer.class, boss.getBoundingBox().inflate(50.0D))) {
                    CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer, boss);
                }
                level.addFreshEntity(boss);
                blockEntity.hasSpawned = true;
                blockEntity.setChanged();
            }


            //pLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 11);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putInt("TimeToSpawn", this.ticksToSpawn);
        tag.putBoolean("HasSpawned", this.hasSpawned);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("TimeToSpawn")) {
            this.ticksToSpawn = tag.getInt("TimeToSpawn");
        }
        if (tag.contains("HasSpawned")) {
            this.hasSpawned = tag.getBoolean("HasSpawned");
        }
    }
}

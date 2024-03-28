package screret.sas.blockentity.blockentity;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
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
import screret.sas.ModTags;
import screret.sas.block.block.SummonSignBlock;
import screret.sas.blockentity.ModBlockEntities;
import screret.sas.entity.ModEntities;
import screret.sas.entity.entity.BossWizardEntity;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.constant.DefaultAnimations;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SummonSignBlockEntity extends BlockEntity implements GeoBlockEntity {
    private static final int REQUIRED_ITEMS_COUNT = 4;
    private static final int TICKS_TO_SPAWN = 100;
    public static final VoxelShape INSIDE = Block.box(-1D, 0.0D, -1D, 17.0D, 16.0D, 17.0D);
    public static final RawAnimation SUMMON = RawAnimation.begin().thenLoop("summon_sign.summon");

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private int ticksToSpawn = -1;
    private boolean hasSpawned = false;

    public SummonSignBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.SUMMON_SIGN.get(), pPos, pBlockState);
    }

    public static Set<ItemEntity> getItemsAt(Level pLevel, SummonSignBlockEntity blockEntity) {
        return INSIDE.toAabbs().stream()
                .flatMap((bounds) -> pLevel.getEntitiesOfClass(ItemEntity.class, bounds.move(blockEntity.getBlockPos().getX(), blockEntity.getBlockPos().getY(), blockEntity.getBlockPos().getZ()), EntitySelector.ENTITY_STILL_ALIVE).stream())
                .collect(Collectors.toSet());
    }

    public static boolean testForTag(Set<HolderSet.Named<Item>> tag, ItemStack pStack, RequiredCounter counter) {
        if (pStack != null) {
            for (var item : tag) {
                if (pStack.is(item)) {
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

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, SummonSignBlockEntity pBlockEntity) {
        if (pPos.getY() >= pLevel.getMinBuildHeight() && pLevel.getDifficulty() != Difficulty.PEACEFUL) {
            var itemEntities = getItemsAt(pLevel, pBlockEntity);
            if (!pBlockEntity.hasSpawned) {
                Stream<ItemStack> items = itemEntities.stream().map(ItemEntity::getItem);
                var requiredItems = BuiltInRegistries.ITEM.getTag(ModTags.Items.BOSS_SUMMON_ITEMS);
                Set<HolderSet.Named<Item>> requiredSet = requiredItems.stream().collect(Collectors.toSet());
                var counter = new RequiredCounter();
                if (!items.allMatch(item -> testForTag(requiredSet, item, counter))) {
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(SummonSignBlock.TRIGGERED, false));
                    return;
                }
                if (itemEntities.size() < requiredSet.size()) {
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(SummonSignBlock.TRIGGERED, false));
                    return;
                }

                if (pBlockEntity.ticksToSpawn < 0) {
                    pBlockEntity.ticksToSpawn = TICKS_TO_SPAWN;
                    pBlockEntity.setChanged();
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(SummonSignBlock.TRIGGERED, true));
                    return;
                } else if (pBlockEntity.ticksToSpawn > 0) {
                    --pBlockEntity.ticksToSpawn;
                    pBlockEntity.setChanged();
                    return;
                }

                for (var itemEntity : itemEntities) {
                    itemEntity.getItem().shrink(1);
                }

                BossWizardEntity boss = ModEntities.BOSS_WIZARD.get().create(pLevel);
                boss.setSpawningPosition(pPos);
                boss.moveTo(pPos.getX() + 0.5f, pPos.getY() + 1.55D, pPos.getZ() + 0.5f, 0.0F, 0.0F);
                boss.makeInvulnerable();
                for (ServerPlayer serverplayer : pLevel.getEntitiesOfClass(ServerPlayer.class, boss.getBoundingBox().inflate(50.0D))) {
                    CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer, boss);
                }
                pLevel.addFreshEntity(boss);
                pBlockEntity.hasSpawned = true;
                pBlockEntity.setChanged();
            }


            //pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 11);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("TimeToSpawn", this.ticksToSpawn);
        pTag.putBoolean("HasSpawned", this.hasSpawned);
    }

    @Override
    public void load(CompoundTag pTag) {
        if (pTag.contains("TimeToSpawn"))
            this.ticksToSpawn = pTag.getInt("TimeToSpawn");
        if (pTag.contains("HasSpawned"))
            this.hasSpawned = pTag.getBoolean("HasSpawned");
    }
}

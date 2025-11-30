package dev.screret.motm.common.menu.container;

import dev.screret.motm.common.block.entity.PotionDistilleryBlockEntity;
import dev.screret.motm.common.menu.slot.DistilleryFuelSlot;
import dev.screret.motm.common.menu.slot.DistilleryResultSlot;
import dev.screret.motm.data.MOTMContainers;
import dev.screret.motm.data.MOTMRecipeTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;

import org.jetbrains.annotations.Nullable;

public class PotionDistilleryMenu extends AbstractContainerMenu {

    private static final int RESULT_SLOT_START = 2, RESULT_SLOT_END = 4;
    private static final int INPUT_SLOT = 1, FUEL_SLOT = 0, INV_SLOT_START = 5, INV_SLOT_END = 32, USE_ROW_SLOT_START = 32,
            USE_ROW_SLOT_END = 41;
    public static final int PROGRESS_BAR_Y_SIZE = 24, FUEL_PROGRESS_BAR_X_SIZE = 18;

    @Nullable
    private final PotionDistilleryBlockEntity blockEntity;
    private final ContainerData data;
    private final Level level;

    public PotionDistilleryMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null);
    }

    public PotionDistilleryMenu(int containerId, Inventory playerInventory, PotionDistilleryBlockEntity blockEntity) {
        super(MOTMContainers.POTION_DISTILLERY.get(), containerId);
        this.blockEntity = blockEntity;

        if (this.blockEntity != null) {
            checkContainerSize(this.blockEntity.getInventory(), 5);
            checkContainerDataCount(blockEntity.getDataAccess(), 4);

            this.data = blockEntity.getDataAccess();
            this.level = playerInventory.player.level();

            IItemHandler items = this.blockEntity.getInventory();
            this.addSlot(new DistilleryFuelSlot(this, items, 0, 17, 17));
            this.addSlot(new SlotItemHandler(items, 1, 79, 17));

            this.addSlot(new DistilleryResultSlot(playerInventory.player, items, 2, 56, 51));
            this.addSlot(new DistilleryResultSlot(playerInventory.player, items, 3, 79, 58));
            this.addSlot(new DistilleryResultSlot(playerInventory.player, items, 4, 102, 51));

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
                }
            }

            for (int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
            }

            this.addDataSlots(this.data);
        } else {
            this.data = null;
            this.level = null;
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int startSlot) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(startSlot);
        if (slot != null && slot.hasItem()) {
            ItemStack item = slot.getItem();
            copy = item.copy();
            if (startSlot >= RESULT_SLOT_START && startSlot <= RESULT_SLOT_END) {
                for (int index = RESULT_SLOT_START; index <= RESULT_SLOT_END; ++index) {
                    if (!this.moveItemStackTo(item, index, USE_ROW_SLOT_END, true)) {
                        return ItemStack.EMPTY;
                    }
                }

                slot.onQuickCraft(item, copy);
            } else if (startSlot != FUEL_SLOT && startSlot != INPUT_SLOT) {
                if (this.canSmelt(item)) {
                    if (!this.moveItemStackTo(item, INPUT_SLOT, FUEL_SLOT, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (PotionDistilleryBlockEntity.isFuel(item)) {
                    if (!this.moveItemStackTo(item, FUEL_SLOT, RESULT_SLOT_START, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (startSlot >= INV_SLOT_START && startSlot < INV_SLOT_END) {
                    if (!this.moveItemStackTo(item, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else
                    if (startSlot >= INV_SLOT_END && startSlot < USE_ROW_SLOT_END && !this.moveItemStackTo(item, 3, 30, false)) {
                        return ItemStack.EMPTY;
                    }
            } else if (!this.moveItemStackTo(item, RESULT_SLOT_END, USE_ROW_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (item.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (item.getCount() == copy.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, item);
        }

        return copy;
    }

    protected boolean canSmelt(ItemStack stack) {
        return this.level.getRecipeManager()
                .getRecipeFor(MOTMRecipeTypes.POTION_DISTILLING_RECIPE.get(), new SingleRecipeInput(stack), this.level)
                .isPresent();
    }

    public boolean isLit() {
        return this.data.get(PotionDistilleryBlockEntity.DATA_LIT_TIME) > 0;
    }

    public int getBurnProgress() {
        int progress = this.data.get(PotionDistilleryBlockEntity.DATA_PROGRESS);
        int total = this.data.get(PotionDistilleryBlockEntity.DATA_TOTAL_TIME);
        return total != 0 && progress != 0 ? progress * PROGRESS_BAR_Y_SIZE / total : 0;
    }

    public int getLitProgress() {
        int litDuration = this.data.get(PotionDistilleryBlockEntity.DATA_LIT_DURATION);
        if (litDuration == 0) {
            litDuration = PotionDistilleryBlockEntity.DEFAULT_PROCESS_TIME;
        }

        return this.data.get(PotionDistilleryBlockEntity.DATA_LIT_TIME) * FUEL_PROGRESS_BAR_X_SIZE / litDuration;
    }

    @Override
    public boolean stillValid(Player player) {
        if (blockEntity == null) {
            return false;
        }
        BlockPos pos = blockEntity.getBlockPos();
        return player.distanceToSqr(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D) > 8 * 8;
    }

    protected static void checkContainerSize(IItemHandler itemHandler, int minSize) {
        if (itemHandler.getSlots() < minSize) {
            throw new IllegalArgumentException(
                    "Container size " + itemHandler.getSlots() + " is smaller than expected: " + minSize);
        }
    }
}

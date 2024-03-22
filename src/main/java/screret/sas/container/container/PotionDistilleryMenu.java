package screret.sas.container.container;

import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;
import screret.sas.blockentity.blockentity.PotionDistilleryBE;
import screret.sas.container.ModContainers;
import screret.sas.container.slot.DistilleryFuelSlot;
import screret.sas.container.slot.DistilleryResultSlot;
import screret.sas.recipe.ModRecipes;

public class PotionDistilleryMenu extends AbstractContainerMenu {
    private static final int RESULT_SLOT_START = 2, RESULT_SLOT_END = 4;
    private static final int INPUT_SLOT = 1, FUEL_SLOT = 0, INV_SLOT_START = 5, INV_SLOT_END = 32, USE_ROW_SLOT_START = 32, USE_ROW_SLOT_END = 41;
    public static final int PROGRESS_BAR_Y_SIZE = 24, FUEL_PROGRESS_BAR_X_SIZE = 18;

    private final Player player;
    @Nullable private final PotionDistilleryBE blockEntity;
    private final ItemStackHandler items;
    private final ContainerData data;
    private final Level level;

    public PotionDistilleryMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, null);
    }

    public PotionDistilleryMenu(int pContainerId, Inventory pPlayerInventory, PotionDistilleryBE blockEntity) {
        super(ModContainers.POTION_DISTILLERY.get(), pContainerId);
        this.player = pPlayerInventory.player;
        this.blockEntity = blockEntity;

        if(this.blockEntity != null){
            checkContainerSize(this.blockEntity.getInventoryWrapper(), 5);
            checkContainerDataCount(blockEntity.getDataAccess(), 4);
            this.items = this.blockEntity.getInventory();
            this.data = blockEntity.getDataAccess();
            this.level = pPlayerInventory.player.level;
            this.addSlot(new DistilleryFuelSlot(this, this.items, 0, 17, 17));
            this.addSlot(new SlotItemHandler(items, 1, 79, 17));

            this.addSlot(new DistilleryResultSlot(pPlayerInventory.player, this.items, 2, 56, 51));
            this.addSlot(new DistilleryResultSlot(pPlayerInventory.player, this.items, 3, 79, 58));
            this.addSlot(new DistilleryResultSlot(pPlayerInventory.player, this.items, 4, 102, 51));

            for(int i = 0; i < 3; ++i) {
                for(int j = 0; j < 9; ++j) {
                    this.addSlot(new Slot(pPlayerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
                }
            }

            for(int k = 0; k < 9; ++k) {
                this.addSlot(new Slot(pPlayerInventory, k, 8 + k * 18, 142));
            }

            this.addDataSlots(this.data);
        } else {
            this.items = null;
            this.data = null;
            this.level = null;
        }
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack copy = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack item = slot.getItem();
            copy = item.copy();
            if (pIndex >= RESULT_SLOT_START && pIndex <= RESULT_SLOT_END) {
                for (int index = RESULT_SLOT_START; index <= RESULT_SLOT_END; ++index){
                    if (!this.moveItemStackTo(item, index, USE_ROW_SLOT_END, true)) {
                        return ItemStack.EMPTY;
                    }
                }

                slot.onQuickCraft(item, copy);
            } else if (pIndex != FUEL_SLOT && pIndex != INPUT_SLOT) {
                if (this.canSmelt(item)) {
                    if (!this.moveItemStackTo(item, INPUT_SLOT, FUEL_SLOT, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.isFuel(item)) {
                    if (!this.moveItemStackTo(item, FUEL_SLOT, RESULT_SLOT_START, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex >= INV_SLOT_START && pIndex < INV_SLOT_END) {
                    if (!this.moveItemStackTo(item, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (pIndex >= INV_SLOT_END && pIndex < USE_ROW_SLOT_END && !this.moveItemStackTo(item, 3, 30, false)) {
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

            slot.onTake(pPlayer, item);
        }

        return copy;
    }

    protected boolean canSmelt(ItemStack pStack) {
        return this.level.getRecipeManager().getRecipeFor(ModRecipes.POTION_DISTILLING_RECIPE.get(), new SimpleContainer(pStack), this.level).isPresent();
    }

    public boolean isFuel(ItemStack pStack) {
        return CommonHooks.getBurnTime(pStack, ModRecipes.POTION_DISTILLING_RECIPE.get()) > 0;
    }

    public boolean isLit() {
        return this.data.get(PotionDistilleryBE.DATA_LIT_TIME) > 0;
    }

    public int getBurnProgress() {
        int progress = this.data.get(PotionDistilleryBE.DATA_PROGRESS);
        int total = this.data.get(PotionDistilleryBE.DATA_TOTAL_TIME);
        return total != 0 && progress != 0 ? progress * PROGRESS_BAR_Y_SIZE / total : 0;
    }

    public int getLitProgress() {
        int litDuration = this.data.get(PotionDistilleryBE.DATA_LIT_DURATION);
        if (litDuration == 0) {
            litDuration = PotionDistilleryBE.DEFAULT_PROCESS_TIME;
        }

        return this.data.get(PotionDistilleryBE.DATA_LIT_TIME) * FUEL_PROGRESS_BAR_X_SIZE / litDuration;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        if(blockEntity == null) {
            return false;
        }
        BlockPos pos = blockEntity.getBlockPos();
        return pPlayer.distanceToSqr(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D) > 8 * 8;
    }
}

package screret.sas.container.container;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.SlotItemHandler;
import screret.sas.block.ModBlocks;
import screret.sas.container.ModContainers;
import screret.sas.container.stackhandler.CraftOutputItemHandler;
import screret.sas.container.stackhandler.CraftResultStackHandler;
import screret.sas.recipe.ModRecipes;
import screret.sas.recipe.recipe.WandRecipe;

import java.util.Optional;

public class WandTableMenu extends AbstractContainerMenu {
    public static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 1, CRAFT_SLOT_END = 7, INV_SLOT_START = 7, INV_SLOT_END = 34, USE_ROW_SLOT_START = 34, USE_ROW_SLOT_END = 43;
    private static final int INPUT_X_SIZE = 3, INPUT_Y_SIZE = 2;

    private final CraftingContainer inputSlots = new CraftingContainer(this, 3,2){
        @Override
        public void setChanged(){
            super.setChanged();
            WandTableMenu.this.slotsChanged(this);
        }
    };

    private final CraftResultStackHandler resultSlot = new CraftResultStackHandler(1);

    private final ContainerLevelAccess access;
    private final Player player;

    public WandTableMenu(int pContainerId, Inventory pPlayerInventory) {
        this(pContainerId, pPlayerInventory, ContainerLevelAccess.NULL);
    }

    public WandTableMenu(int pContainerId, Inventory pPlayerInventory, ContainerLevelAccess pAccess) {
        super(ModContainers.WAND_TABLE.get(), pContainerId);
        this.access = pAccess;
        this.player = pPlayerInventory.player;
        this.addSlot(new CraftOutputItemHandler(pPlayerInventory.player, this.inputSlots, this.resultSlot, 0, 124, 35));

        for(int y = 0; y < INPUT_Y_SIZE; ++y) {
            for(int x = 0; x < INPUT_X_SIZE; ++x) {
                this.addSlot(new Slot(this.inputSlots, x + y * INPUT_X_SIZE, 30 + x * 18, 26 + y * 18));
            }
        }

        for(int k = 0; k < 3; ++k) {
            for(int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(pPlayerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for(int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(pPlayerInventory, l, 8 + l * 18, 142));
        }

    }

    protected static void slotChangedCraftingGrid(AbstractContainerMenu pMenu, Level pLevel, Player pPlayer, CraftingContainer pContainer, CraftResultStackHandler pResult) {
        if (!pLevel.isClientSide) {
            ServerPlayer serverplayer = (ServerPlayer)pPlayer;
            ItemStack result = ItemStack.EMPTY;
            Optional<WandRecipe> optional = pLevel.getServer().getRecipeManager().getRecipeFor(ModRecipes.WAND_RECIPE.get(), pContainer, pLevel);
            if (optional.isPresent()) {
                WandRecipe recipe = optional.get();
                if (pResult.setRecipeUsed(pLevel, serverplayer, recipe)) {
                    result = recipe.assemble(pContainer);
                }
            }

            pResult.setStackInSlot(RESULT_SLOT, result);
            pMenu.setRemoteSlot(RESULT_SLOT, result);
            serverplayer.connection.send(new ClientboundContainerSetSlotPacket(pMenu.containerId, pMenu.incrementStateId(), 0, result));
        }
    }

    @Override
    public void slotsChanged(Container container) {
        this.access.execute((level, pos) -> {
            slotChangedCraftingGrid(this, level, this.player, this.inputSlots, this.resultSlot);
        });
    }


    public boolean recipeMatches(Recipe<? super CraftingContainer> pRecipe) {
        return pRecipe.matches(this.inputSlots, this.player.level);
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        this.access.execute((p_39371_, p_39372_) -> {
            this.clearContainer(pPlayer, this.inputSlots);
        });
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack stackCopy = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            stackCopy = stack.copy();
            if (pIndex == RESULT_SLOT) {
                if (!this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(stack, stackCopy);
            } else if (pIndex >= CRAFT_SLOT_END && pIndex < USE_ROW_SLOT_END) {
                if (!this.moveItemStackTo(stack, CRAFT_SLOT_START, CRAFT_SLOT_END, false)) {
                    if (pIndex < INV_SLOT_END) {
                        if (!this.moveItemStackTo(stack, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_START, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == stackCopy.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(pPlayer, stack);
            if (pIndex == RESULT_SLOT) {
                pPlayer.drop(stack, false);
            }
        }

        return stackCopy;
    }


    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(access, pPlayer, ModBlocks.WAND_TABLE.get());
    }

    public boolean canTakeItemForPickAll(ItemStack pStack, Slot pSlot) {
        if(pSlot instanceof SlotItemHandler slot){
            return slot.getItemHandler() != this.resultSlot && super.canTakeItemForPickAll(pStack, pSlot);
        }
        return super.canTakeItemForPickAll(pStack, pSlot);
    }
}

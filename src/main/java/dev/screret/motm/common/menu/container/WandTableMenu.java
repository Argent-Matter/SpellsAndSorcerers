package dev.screret.motm.common.menu.container;

import dev.screret.motm.common.menu.stackhandler.CraftOutputItemHandler;
import dev.screret.motm.common.menu.stackhandler.CraftResultStackHandler;
import dev.screret.motm.common.recipe.wand.WandRecipe;
import dev.screret.motm.data.MOTMBlocks;
import dev.screret.motm.data.MOTMContainers;
import dev.screret.motm.data.MOTMRecipeTypes;

import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.SlotItemHandler;

import java.util.Optional;

public class WandTableMenu extends AbstractContainerMenu {

    public static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 1, CRAFT_SLOT_END = 7, INV_SLOT_START = 7, INV_SLOT_END = 34,
            USE_ROW_SLOT_START = 34, USE_ROW_SLOT_END = 43;
    private static final int INPUT_X_SIZE = 3, INPUT_Y_SIZE = 2;

    private final CraftingContainer inputSlots = new TransientCraftingContainer(this, 3, 2) {

        @Override
        public void setChanged() {
            super.setChanged();
            WandTableMenu.this.slotsChanged(this);
        }
    };

    private final CraftResultStackHandler resultSlot = new CraftResultStackHandler(1);

    private final ContainerLevelAccess access;
    private final Player player;

    public WandTableMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public WandTableMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(MOTMContainers.WAND_TABLE.get(), containerId);
        this.access = access;
        this.player = playerInventory.player;
        this.addSlot(new CraftOutputItemHandler(playerInventory.player, this.inputSlots, this.resultSlot, 0, 124, 35));

        for (int y = 0; y < INPUT_Y_SIZE; ++y) {
            for (int x = 0; x < INPUT_X_SIZE; ++x) {
                this.addSlot(new Slot(this.inputSlots, x + y * INPUT_X_SIZE, 30 + x * 18, 26 + y * 18));
            }
        }

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(playerInventory, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(playerInventory, l, 8 + l * 18, 142));
        }
    }

    protected static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player,
                                                  CraftingContainer inputItemHandler, CraftResultStackHandler outputItemHandler) {
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        CraftingInput.Positioned positionedCraftInput = inputItemHandler.asPositionedCraftInput();
        CraftingInput craftInput = positionedCraftInput.input();

        ItemStack result = ItemStack.EMPTY;
        Optional<RecipeHolder<WandRecipe>> maybeRecipe = level.getServer().getRecipeManager()
                .getRecipeFor(MOTMRecipeTypes.WAND_RECIPE.get(), craftInput, level);
        if (maybeRecipe.isPresent()) {
            RecipeHolder<WandRecipe> recipe = maybeRecipe.get();
            if (outputItemHandler.setRecipeUsed(level, serverPlayer, recipe)) {
                result = recipe.value().assemble(craftInput, level.registryAccess());
            }
        }

        outputItemHandler.setStackInSlot(RESULT_SLOT, result);
        menu.setRemoteSlot(RESULT_SLOT, result);
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, result));
    }

    @Override
    public void slotsChanged(Container container) {
        this.access.execute((level, pos) -> {
            slotChangedCraftingGrid(this, level, this.player, this.inputSlots, this.resultSlot);
        });
    }

    public boolean recipeMatches(Recipe<? super CraftingInput> recipe) {
        return recipe.matches(this.inputSlots.asCraftInput(), this.player.level());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((p_39371_, p_39372_) -> {
            this.clearContainer(player, this.inputSlots);
        });
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stackCopy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return stackCopy;
        }
        ItemStack stack = slot.getItem();
        stackCopy = stack.copy();
        if (index == RESULT_SLOT) {
            if (!this.moveItemStackTo(stack, INV_SLOT_START, USE_ROW_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }

            slot.onQuickCraft(stack, stackCopy);
        } else if (index >= CRAFT_SLOT_END && index < USE_ROW_SLOT_END) {
            if (!this.moveItemStackTo(stack, CRAFT_SLOT_START, CRAFT_SLOT_END, false)) {
                if (index < INV_SLOT_END) {
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
        slot.onTake(player, stack);
        if (index == RESULT_SLOT) {
            player.drop(stack, false);
        }
        return stackCopy;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, MOTMBlocks.WAND_TABLE.get());
    }

    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        if (slot instanceof SlotItemHandler slotItemHandler) {
            return slotItemHandler.getItemHandler() != this.resultSlot && super.canTakeItemForPickAll(stack, slot);
        }
        return super.canTakeItemForPickAll(stack, slot);
    }
}

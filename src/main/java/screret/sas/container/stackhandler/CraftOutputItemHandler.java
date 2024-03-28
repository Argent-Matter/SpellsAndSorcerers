package screret.sas.container.stackhandler;

import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import screret.sas.recipe.ModRecipeTypes;

public class CraftOutputItemHandler extends SlotItemHandler {
    private final CraftingContainer craftSlots;
    private int removeCount;
    private final Player player;

    public CraftOutputItemHandler(Player player, CraftingContainer craftSlots, IItemHandler inventory, int index, int xPosition, int yPosition) {
        super(inventory, index, xPosition, yPosition);
        this.player = player;
        this.craftSlots = craftSlots;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return hasItem();
    }

    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.removeCount += Math.min(amount, this.getItem().getCount());
        }

        return super.remove(amount);
    }

    @Override
    public void onSwapCraft(int count) {
        this.removeCount += count;
    }

    @Override
    protected void onQuickCraft(ItemStack stack, int count) {
        this.removeCount += count;
        this.checkTakeAchievements(stack);
    }

    @Override
    public void onQuickCraft(ItemStack oldStackIn, ItemStack newStackIn) {
        int i = newStackIn.getCount() - oldStackIn.getCount();
        if (i > 0) {
            this.onQuickCraft(newStackIn, i);
        }

    }

    @Override
    protected void checkTakeAchievements(ItemStack stack) {
        if (this.removeCount > 0) {
            stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
            net.neoforged.neoforge.event.EventHooks.firePlayerCraftingEvent(this.player, stack, this.craftSlots);
        }

        if (this.container instanceof RecipeCraftingHolder holder) {
            holder.awardUsedRecipes(this.player, this.craftSlots.getItems());
        }

        this.removeCount = 0;
    }

    @Override
    public void onTake(Player pPlayer, ItemStack pStack) {
        this.checkTakeAchievements(pStack);
        net.neoforged.neoforge.common.CommonHooks.setCraftingPlayer(pPlayer);
        NonNullList<ItemStack> ingredients = pPlayer.level().getRecipeManager().getRemainingItemsFor(ModRecipeTypes.WAND_RECIPE.get(), this.craftSlots, pPlayer.level());
        net.neoforged.neoforge.common.CommonHooks.setCraftingPlayer(null);
        for (int i = 0; i < ingredients.size(); ++i) {
            ItemStack craftSlotItem = this.craftSlots.getItem(i);
            ItemStack ingredient = ingredients.get(i);
            if (!craftSlotItem.isEmpty()) {
                this.craftSlots.removeItem(i, 1);
                craftSlotItem = this.craftSlots.getItem(i);
            }

            if (!ingredient.isEmpty()) {
                if (craftSlotItem.isEmpty()) {
                    this.craftSlots.setItem(i, ingredient);
                } else if (ItemStack.isSameItemSameTags(craftSlotItem, ingredient)) {
                    ingredient.grow(craftSlotItem.getCount());
                    this.craftSlots.setItem(i, ingredient);
                } else if (!this.player.getInventory().add(ingredient)) {
                    this.player.drop(ingredient, false);
                }
            }
        }

    }
}

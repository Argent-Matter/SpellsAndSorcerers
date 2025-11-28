package dev.screret.mitm.common.block.entity;

import com.google.common.collect.Lists;
import dev.screret.mitm.data.MITMBlockEntities;
import dev.screret.mitm.data.MITMRecipeTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import dev.screret.mitm.data.MITMBlocks;
import dev.screret.mitm.common.block.PotionDistilleryBlock;
import dev.screret.mitm.common.menu.container.PotionDistilleryMenu;
import dev.screret.mitm.common.item.handler.WrappedHandler;
import dev.screret.mitm.common.recipe.PotionDistillingRecipe;
import lombok.Getter;

import org.jetbrains.annotations.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PotionDistilleryBlockEntity extends BlockEntity implements MenuProvider {

    public static final int DEFAULT_PROCESS_TIME = 400;
    public static final int DATA_LIT_TIME = 0, DATA_LIT_DURATION = 1, DATA_PROGRESS = 2, DATA_TOTAL_TIME = 3;
    private static final int SLOT_EXTRACT_MIN = 2, SLOT_EXTRACT_MAX = 4, SLOT_FUEL = 0, SLOT_INPUT = 1;

    private final ItemStackHandler items = new ItemStackHandler(5);
    private final RecipeWrapper itemsWrapped = new RecipeWrapper(items);
    private int litTime;
    private int litDuration;
    private int cookingProgress;
    private int cookingTotalTime;

    @Getter
    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int id) {
            return switch (id) {
                case 0 -> PotionDistilleryBlockEntity.this.litTime;
                case 1 -> PotionDistilleryBlockEntity.this.litDuration;
                case 2 -> PotionDistilleryBlockEntity.this.cookingProgress;
                case 3 -> PotionDistilleryBlockEntity.this.cookingTotalTime;
                default -> 0;
            };
        }

        public void set(int id, int value) {
            switch (id) {
                case 0:
                    PotionDistilleryBlockEntity.this.litTime = value;
                    break;
                case 1:
                    PotionDistilleryBlockEntity.this.litDuration = value;
                    break;
                case 2:
                    PotionDistilleryBlockEntity.this.cookingProgress = value;
                    break;
                case 3:
                    PotionDistilleryBlockEntity.this.cookingTotalTime = value;
            }

        }

        public int getCount() {
            return 4;
        }
    };
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();

    public PotionDistilleryBlockEntity(BlockPos pos, BlockState blockState) {
        super(MITMBlockEntities.POTION_DISTILLERY.get(), pos, blockState);
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        items.deserializeNBT(registries, tag.getCompound("Items"));

        this.litTime = tag.getInt("BurnTime");
        this.cookingProgress = tag.getInt("CookTime");
        this.cookingTotalTime = tag.getInt("CookTimeTotal");
        this.litDuration = this.getBurnDuration(this.items.getStackInSlot(1));
        CompoundTag compoundtag = tag.getCompound("RecipesUsed");

        for (String recipe : compoundtag.getAllKeys()) {
            this.recipesUsed.put(ResourceLocation.parse(recipe), compoundtag.getInt(recipe));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("Items", this.items.serializeNBT(registries));

        tag.putInt("BurnTime", this.litTime);
        tag.putInt("CookTime", this.cookingProgress);
        tag.putInt("CookTimeTotal", this.cookingTotalTime);
        CompoundTag compoundtag = new CompoundTag();
        this.recipesUsed.forEach((id, amount) -> {
            compoundtag.putInt(id.toString(), amount);
        });
        tag.put("RecipesUsed", compoundtag);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PotionDistilleryBlockEntity blockEntity) {
        boolean isLit = blockEntity.isLit();
        boolean flag1 = false;
        if (blockEntity.isLit()) {
            --blockEntity.litTime;
        }

        ItemStack fuel = blockEntity.items.getStackInSlot(SLOT_FUEL);
        boolean hasItem = !blockEntity.items.getStackInSlot(SLOT_INPUT).isEmpty();
        boolean hasFuel = !fuel.isEmpty();
        if (blockEntity.isLit() || hasItem && hasFuel) {
            RecipeHolder<PotionDistillingRecipe> holder = null;
            if (hasFuel) {
                holder = level.getRecipeManager()
                        .getRecipeFor(MITMRecipeTypes.POTION_DISTILLING_RECIPE.get(), blockEntity.itemsWrapped, level)
                        .orElse(null);
            }
            if (holder == null) return;
            PotionDistillingRecipe recipe = holder.value();

            int i = blockEntity.items.getSlotLimit(SLOT_INPUT);
            if (!blockEntity.isLit() && blockEntity.canBurn(recipe, blockEntity.items, i)) {
                blockEntity.litTime = blockEntity.getBurnDuration(fuel);
                blockEntity.litDuration = blockEntity.litTime;
                if (blockEntity.isLit()) {
                    flag1 = true;
                    if (fuel.hasCraftingRemainingItem())
                        blockEntity.items.setStackInSlot(SLOT_INPUT, fuel.getCraftingRemainingItem());
                    else {
                        fuel.shrink(1);
                        if (fuel.isEmpty()) {
                            blockEntity.items.setStackInSlot(SLOT_INPUT, fuel.getCraftingRemainingItem());
                        }
                    }
                }
            }

            if (blockEntity.isLit() && blockEntity.canBurn(recipe, blockEntity.items, i)) {
                ++blockEntity.cookingProgress;
                if (blockEntity.cookingProgress == blockEntity.cookingTotalTime) {
                    blockEntity.cookingProgress = 0;
                    blockEntity.cookingTotalTime = getTotalCookTime(level, blockEntity);
                    if (blockEntity.burn(recipe, blockEntity.items, i)) {
                        blockEntity.setRecipeUsed(holder);
                    }

                    flag1 = true;
                }
            } else {
                blockEntity.cookingProgress = 0;
            }
        } else if (!blockEntity.isLit() && blockEntity.cookingProgress > 0) {
            blockEntity.cookingProgress = Mth.clamp(blockEntity.cookingProgress - 2, 0, blockEntity.cookingTotalTime);
        }

        if (isLit != blockEntity.isLit()) {
            flag1 = true;
            state = state.setValue(AbstractFurnaceBlock.LIT, blockEntity.isLit());
            level.setBlock(pos, state, 3);
        }

        if (flag1) {
            setChanged(level, pos, state);
        }

    }

    private boolean canBurn(@Nullable PotionDistillingRecipe recipe, IItemHandler stacks, int stackSize) {
        if (!stacks.getStackInSlot(0).isEmpty() && recipe != null) {
            ItemStack itemstack = recipe.assemble(this.itemsWrapped, this.level.registryAccess());
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack result = stacks.getStackInSlot(SLOT_EXTRACT_MIN);
                if (result.isEmpty()) {
                    return true;
                } else if (result.getItem() != itemstack.getItem()) {
                    return false;
                } else if (result.getCount() + itemstack.getCount() <= stackSize && result.getCount() + itemstack.getCount() <= result.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
                    return true;
                } else {
                    return result.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
                }
            }
        } else {
            return false;
        }
    }

    private boolean burn(@Nullable PotionDistillingRecipe recipe, IItemHandlerModifiable stacks, int stackSize) {
        if (recipe != null && this.canBurn(recipe, stacks, stackSize)) {
            ItemStack fuel = stacks.getStackInSlot(SLOT_FUEL);
            ItemStack ingredient = recipe.assemble(this.itemsWrapped, this.level.registryAccess());
            ItemStack result = stacks.getStackInSlot(SLOT_EXTRACT_MIN);
            if (result.isEmpty()) {
                stacks.setStackInSlot(2, ingredient.copy());
            } else if (result.is(ingredient.getItem())) {
                result.grow(ingredient.getCount());
            }

            fuel.shrink(1);
            return true;
        } else {
            return false;
        }
    }

    private static int getTotalCookTime(Level level, PotionDistilleryBlockEntity blockEntity) {
        return level.getRecipeManager().getRecipeFor(MITMRecipeTypes.POTION_DISTILLING_RECIPE.get(), blockEntity.getInventoryWrapper(), level)
                .map(RecipeHolder::value)
                .map(PotionDistillingRecipe::getProcessingTime)
                .orElse(DEFAULT_PROCESS_TIME);
    }

    public static boolean isFuel(ItemStack stack) {
        return getBurnDuration(stack) > 0;
    }

    protected static int getBurnDuration(ItemStack stack) {
        return stack.getBurnTime(MITMRecipeTypes.POTION_DISTILLING_RECIPE.get());
    }

    public void setRecipeUsed(@Nullable RecipeHolder<PotionDistillingRecipe> recipe) {
        if (recipe != null) {
            ResourceLocation resourcelocation = recipe.id();
            this.recipesUsed.addTo(resourcelocation, 1);
        }
    }

    public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 popVec) {
        List<RecipeHolder<?>> usedRecipes = Lists.newArrayList();

        for (Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> {
                usedRecipes.add(recipe);
                createExperience(level, popVec, entry.getIntValue(), ((PotionDistillingRecipe) recipe.value()).getExperience());
            });
        }

        return usedRecipes;
    }

    private static void createExperience(ServerLevel level, Vec3 popVec, int recipeIndex, float experience) {
        int amount = Mth.floor(recipeIndex * experience);
        float fraction = Mth.frac(recipeIndex * experience);
        if (fraction != 0.0F && Math.random() < fraction) {
            ++amount;
        }

        ExperienceOrb.award(level, popVec, amount);
    }

    public IItemHandler getInventory() {
        return this.items;
    }

    public RecipeWrapper getInventoryWrapper() {
        return this.itemsWrapped;
    }

    private final Map<Direction, WrappedHandler> directionWrappedHandlerMap = Map.of(
                    Direction.DOWN, new WrappedHandler(items, (i) -> i >= SLOT_EXTRACT_MIN, (i, s) -> false),
                    Direction.NORTH, new WrappedHandler(items, (index) -> index == SLOT_INPUT, (index, stack) -> items.isItemValid(SLOT_INPUT, stack)),
                    Direction.SOUTH, new WrappedHandler(items, (i) -> i >= SLOT_EXTRACT_MIN, (i, s) -> false),
                    Direction.EAST, new WrappedHandler(items, (i) -> i == SLOT_INPUT, (index, stack) -> items.isItemValid(SLOT_INPUT, stack)),
                    Direction.WEST, new WrappedHandler(items, (index) -> index == SLOT_FUEL || index == SLOT_INPUT, (index, stack) -> items.isItemValid(SLOT_FUEL, stack) || items.isItemValid(SLOT_INPUT, stack))
            );

    public IItemHandler getItemHandler(Direction side) {
        if (directionWrappedHandlerMap.containsKey(side)) {
            Direction localDir = this.getBlockState().getValue(PotionDistilleryBlock.FACING);

            if (side == Direction.UP || side == Direction.DOWN) {
                return directionWrappedHandlerMap.get(side);
            }

            return switch (localDir) {
                default ->
                        directionWrappedHandlerMap.get(side.getOpposite());
                case EAST ->
                        directionWrappedHandlerMap.get(side.getClockWise());
                case SOUTH ->
                        directionWrappedHandlerMap.get(side);
                case WEST ->
                        directionWrappedHandlerMap.get(side.getCounterClockWise());
            };
        }
        return null;
    }

    @Override
    public Component getDisplayName() {
        return MITMBlocks.POTION_DISTILLERY.get().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new PotionDistilleryMenu(containerId, playerInventory, this);
    }
}

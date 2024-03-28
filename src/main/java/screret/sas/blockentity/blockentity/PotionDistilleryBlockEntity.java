package screret.sas.blockentity.blockentity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.LockCode;
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
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import screret.sas.block.ModBlocks;
import screret.sas.block.block.PotionDistilleryBlock;
import screret.sas.blockentity.ModBlockEntities;
import screret.sas.container.container.PotionDistilleryMenu;
import screret.sas.item.handler.WrappedHandler;
import screret.sas.recipe.ModRecipeTypes;
import screret.sas.recipe.recipe.PotionDistillingRecipe;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class PotionDistilleryBlockEntity extends BlockEntity implements MenuProvider {
    public static final int DEFAULT_PROCESS_TIME = 400;
    public static final int DATA_LIT_TIME = 0, DATA_LIT_DURATION = 1, DATA_PROGRESS = 2, DATA_TOTAL_TIME = 3;
    private static final int SLOT_EXTRACT_MIN = 2, SLOT_EXTRACT_MAX = 4, SLOT_FUEL = 0, SLOT_INPUT = 1;

    private LockCode lockKey = LockCode.NO_LOCK;
    private Component name;
    ItemStackHandler items = new ItemStackHandler(5);
    RecipeWrapper itemsWrapped = new RecipeWrapper(items);
    int litTime;
    int litDuration;
    int cookingProgress;
    int cookingTotalTime;
    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int p_58431_) {
            switch (p_58431_) {
                case 0:
                    return PotionDistilleryBlockEntity.this.litTime;
                case 1:
                    return PotionDistilleryBlockEntity.this.litDuration;
                case 2:
                    return PotionDistilleryBlockEntity.this.cookingProgress;
                case 3:
                    return PotionDistilleryBlockEntity.this.cookingTotalTime;
                default:
                    return 0;
            }
        }

        public void set(int p_58433_, int p_58434_) {
            switch (p_58433_) {
                case 0:
                    PotionDistilleryBlockEntity.this.litTime = p_58434_;
                    break;
                case 1:
                    PotionDistilleryBlockEntity.this.litDuration = p_58434_;
                    break;
                case 2:
                    PotionDistilleryBlockEntity.this.cookingProgress = p_58434_;
                    break;
                case 3:
                    PotionDistilleryBlockEntity.this.cookingTotalTime = p_58434_;
            }

        }

        public int getCount() {
            return 4;
        }
    };
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();

    public PotionDistilleryBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.POTION_DISTILLERY.get(), pPos, pBlockState);
    }

    private boolean isLit() {
        return this.litTime > 0;
    }

    public void load(CompoundTag pTag) {
        super.load(pTag);

        this.lockKey = LockCode.fromTag(pTag);
        if (pTag.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(pTag.getString("CustomName"));
        }

        items.deserializeNBT(pTag.getCompound("Items"));

        this.litTime = pTag.getInt("BurnTime");
        this.cookingProgress = pTag.getInt("CookTime");
        this.cookingTotalTime = pTag.getInt("CookTimeTotal");
        this.litDuration = this.getBurnDuration(this.items.getStackInSlot(1));
        CompoundTag compoundtag = pTag.getCompound("RecipesUsed");

        for (String recipe : compoundtag.getAllKeys()) {
            this.recipesUsed.put(new ResourceLocation(recipe), compoundtag.getInt(recipe));
        }

    }

    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);

        this.lockKey = LockCode.fromTag(pTag);
        if (pTag.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(pTag.getString("CustomName"));
        }

        pTag.put("Items", this.items.serializeNBT());

        pTag.putInt("BurnTime", this.litTime);
        pTag.putInt("CookTime", this.cookingProgress);
        pTag.putInt("CookTimeTotal", this.cookingTotalTime);
        CompoundTag compoundtag = new CompoundTag();
        this.recipesUsed.forEach((id, amount) -> {
            compoundtag.putInt(id.toString(), amount);
        });
        pTag.put("RecipesUsed", compoundtag);
    }

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, PotionDistilleryBlockEntity pBlockEntity) {
        boolean isLit = pBlockEntity.isLit();
        boolean flag1 = false;
        if (pBlockEntity.isLit()) {
            --pBlockEntity.litTime;
        }

        ItemStack fuel = pBlockEntity.items.getStackInSlot(SLOT_FUEL);
        boolean hasItem = !pBlockEntity.items.getStackInSlot(SLOT_INPUT).isEmpty();
        boolean hasFuel = !fuel.isEmpty();
        if (pBlockEntity.isLit() || hasItem && hasFuel) {
            RecipeHolder<PotionDistillingRecipe> holder = null;
            if (hasFuel) {
                holder = pLevel.getRecipeManager().getRecipeFor(ModRecipeTypes.POTION_DISTILLING_RECIPE.get(), pBlockEntity.itemsWrapped, pLevel).orElse(null);
            }
            if (holder == null) return;
            PotionDistillingRecipe recipe = holder.value();

            int i = pBlockEntity.items.getSlotLimit(SLOT_INPUT);
            if (!pBlockEntity.isLit() && pBlockEntity.canBurn(recipe, pBlockEntity.items, i)) {
                pBlockEntity.litTime = pBlockEntity.getBurnDuration(fuel);
                pBlockEntity.litDuration = pBlockEntity.litTime;
                if (pBlockEntity.isLit()) {
                    flag1 = true;
                    if (fuel.hasCraftingRemainingItem())
                        pBlockEntity.items.setStackInSlot(SLOT_INPUT, fuel.getCraftingRemainingItem());
                    else {
                        fuel.shrink(1);
                        if (fuel.isEmpty()) {
                            pBlockEntity.items.setStackInSlot(SLOT_INPUT, fuel.getCraftingRemainingItem());
                        }
                    }
                }
            }

            if (pBlockEntity.isLit() && pBlockEntity.canBurn(recipe, pBlockEntity.items, i)) {
                ++pBlockEntity.cookingProgress;
                if (pBlockEntity.cookingProgress == pBlockEntity.cookingTotalTime) {
                    pBlockEntity.cookingProgress = 0;
                    pBlockEntity.cookingTotalTime = getTotalCookTime(pLevel, pBlockEntity);
                    if (pBlockEntity.burn(recipe, pBlockEntity.items, i)) {
                        pBlockEntity.setRecipeUsed(holder);
                    }

                    flag1 = true;
                }
            } else {
                pBlockEntity.cookingProgress = 0;
            }
        } else if (!pBlockEntity.isLit() && pBlockEntity.cookingProgress > 0) {
            pBlockEntity.cookingProgress = Mth.clamp(pBlockEntity.cookingProgress - 2, 0, pBlockEntity.cookingTotalTime);
        }

        if (isLit != pBlockEntity.isLit()) {
            flag1 = true;
            pState = pState.setValue(AbstractFurnaceBlock.LIT, pBlockEntity.isLit());
            pLevel.setBlock(pPos, pState, 3);
        }

        if (flag1) {
            setChanged(pLevel, pPos, pState);
        }

    }

    private boolean canBurn(@Nullable PotionDistillingRecipe pRecipe, IItemHandler pStacks, int pStackSize) {
        if (!pStacks.getStackInSlot(0).isEmpty() && pRecipe != null) {
            ItemStack itemstack = pRecipe.assemble(this.itemsWrapped, this.level.registryAccess());
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack result = pStacks.getStackInSlot(SLOT_EXTRACT_MIN);
                if (result.isEmpty()) {
                    return true;
                } else if (result.getItem() != itemstack.getItem()) {
                    return false;
                } else if (result.getCount() + itemstack.getCount() <= pStackSize && result.getCount() + itemstack.getCount() <= result.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
                    return true;
                } else {
                    return result.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
                }
            }
        } else {
            return false;
        }
    }

    private boolean burn(@Nullable PotionDistillingRecipe pRecipe, IItemHandlerModifiable pStacks, int pStackSize) {
        if (pRecipe != null && this.canBurn(pRecipe, pStacks, pStackSize)) {
            ItemStack fuel = pStacks.getStackInSlot(SLOT_FUEL);
            ItemStack ingredient = pRecipe.assemble(this.itemsWrapped, this.level.registryAccess());
            ItemStack result = pStacks.getStackInSlot(SLOT_EXTRACT_MIN);
            if (result.isEmpty()) {
                pStacks.setStackInSlot(2, ingredient.copy());
            } else if (result.is(ingredient.getItem())) {
                result.grow(ingredient.getCount());
            }

            fuel.shrink(1);
            return true;
        } else {
            return false;
        }
    }

    private static int getTotalCookTime(Level pLevel, PotionDistilleryBlockEntity pBlockEntity) {
        return pLevel.getRecipeManager().getRecipeFor(ModRecipeTypes.POTION_DISTILLING_RECIPE.get(), pBlockEntity.itemsWrapped, pLevel).map(RecipeHolder::value).map(PotionDistillingRecipe::getProcessingTime).orElse(DEFAULT_PROCESS_TIME);
    }

    public static boolean isFuel(ItemStack pStack) {
        return CommonHooks.getBurnTime(pStack, ModRecipeTypes.POTION_DISTILLING_RECIPE.get()) > 0;
    }

    protected int getBurnDuration(ItemStack pFuel) {
        if (pFuel.isEmpty()) {
            return 0;
        } else {
            return CommonHooks.getBurnTime(pFuel, ModRecipeTypes.POTION_DISTILLING_RECIPE.get());
        }
    }

    public void setRecipeUsed(@Nullable RecipeHolder<PotionDistillingRecipe> pRecipe) {
        if (pRecipe != null) {
            ResourceLocation resourcelocation = pRecipe.id();
            this.recipesUsed.addTo(resourcelocation, 1);
        }

    }

    public List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel pLevel, Vec3 pPopVec) {
        List<RecipeHolder<?>> usedRecipes = Lists.newArrayList();

        for (Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
            pLevel.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> {
                usedRecipes.add(recipe);
                createExperience(pLevel, pPopVec, entry.getIntValue(), ((PotionDistillingRecipe) recipe.value()).getExperience());
            });
        }

        return usedRecipes;
    }

    private static void createExperience(ServerLevel pLevel, Vec3 pPopVec, int pRecipeIndex, float pExperience) {
        int amount = Mth.floor(pRecipeIndex * pExperience);
        float fraction = Mth.frac(pRecipeIndex * pExperience);
        if (fraction != 0.0F && Math.random() < fraction) {
            ++amount;
        }

        ExperienceOrb.award(pLevel, pPopVec, amount);
    }

    public ItemStackHandler getInventory() {
        return this.items;
    }

    public RecipeWrapper getInventoryWrapper() {
        return this.itemsWrapped;
    }

    public ContainerData getDataAccess() {
        return this.dataAccess;
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
        return ModBlocks.POTION_DISTILLERY.get().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new PotionDistilleryMenu(pContainerId, pPlayerInventory, this);
    }
}

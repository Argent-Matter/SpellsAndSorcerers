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
import net.minecraft.world.Container;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import screret.sas.block.ModBlocks;
import screret.sas.block.block.PotionDistilleryBlock;
import screret.sas.blockentity.ModBlockEntities;
import screret.sas.container.ModContainers;
import screret.sas.container.container.PotionDistilleryMenu;
import screret.sas.item.handler.WrappedHandler;
import screret.sas.recipe.ModRecipes;
import screret.sas.recipe.recipe.PotionDistillingRecipe;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class PotionDistilleryBE extends BlockEntity implements MenuProvider {
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
                    return PotionDistilleryBE.this.litTime;
                case 1:
                    return PotionDistilleryBE.this.litDuration;
                case 2:
                    return PotionDistilleryBE.this.cookingProgress;
                case 3:
                    return PotionDistilleryBE.this.cookingTotalTime;
                default:
                    return 0;
            }
        }

        public void set(int p_58433_, int p_58434_) {
            switch (p_58433_) {
                case 0:
                    PotionDistilleryBE.this.litTime = p_58434_;
                    break;
                case 1:
                    PotionDistilleryBE.this.litDuration = p_58434_;
                    break;
                case 2:
                    PotionDistilleryBE.this.cookingProgress = p_58434_;
                    break;
                case 3:
                    PotionDistilleryBE.this.cookingTotalTime = p_58434_;
            }

        }

        public int getCount() {
            return 4;
        }
    };
    private final Object2IntOpenHashMap<ResourceLocation> recipesUsed = new Object2IntOpenHashMap<>();

    public PotionDistilleryBE(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.POTION_DISTILLERY_BE.get(), pPos, pBlockState);
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

        for(String recipe : compoundtag.getAllKeys()) {
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

    public static void serverTick(Level pLevel, BlockPos pPos, BlockState pState, PotionDistilleryBE pBlockEntity) {
        boolean isLit = pBlockEntity.isLit();
        boolean flag1 = false;
        if (pBlockEntity.isLit()) {
            --pBlockEntity.litTime;
        }

        ItemStack fuel = pBlockEntity.items.getStackInSlot(SLOT_FUEL);
        boolean hasItem = !pBlockEntity.items.getStackInSlot(SLOT_INPUT).isEmpty();
        boolean hasFuel = !fuel.isEmpty();
        if (pBlockEntity.isLit() || hasItem && hasFuel) {
            Recipe<?> recipe;
            if (hasFuel) {
                recipe =  pLevel.getRecipeManager().getRecipeFor(ModRecipes.POTION_DISTILLING_RECIPE.get(), pBlockEntity.itemsWrapped, pLevel).orElse(null);
            } else {
                recipe = null;
            }

            int i = pBlockEntity.items.getSlotLimit(SLOT_INPUT);
            if (!pBlockEntity.isLit() && pBlockEntity.canBurn(recipe, pBlockEntity.items, i)) {
                pBlockEntity.litTime = pBlockEntity.getBurnDuration(fuel);
                pBlockEntity.litDuration = pBlockEntity.litTime;
                if (pBlockEntity.isLit()) {
                    flag1 = true;
                    if (fuel.hasCraftingRemainingItem())
                        pBlockEntity.items.setStackInSlot(SLOT_INPUT, fuel.getCraftingRemainingItem());
                    else
                    if (hasFuel) {
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
                        pBlockEntity.setRecipeUsed(recipe);
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

    private boolean canBurn(@Nullable Recipe<?> pRecipe, IItemHandler pStacks, int pStackSize) {
        if (!pStacks.getStackInSlot(0).isEmpty() && pRecipe != null) {
            ItemStack itemstack = ((Recipe<Container>)pRecipe).assemble(this.itemsWrapped);
            if (itemstack.isEmpty()) {
                return false;
            } else {
                ItemStack result = pStacks.getStackInSlot(SLOT_EXTRACT_MIN);
                if (result.isEmpty()) {
                    return true;
                } else if (!result.sameItem(itemstack)) {
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

    private boolean burn(@Nullable Recipe<?> pRecipe, IItemHandlerModifiable pStacks, int pStackSize) {
        if (pRecipe != null && this.canBurn(pRecipe, pStacks, pStackSize)) {
            ItemStack fuel = pStacks.getStackInSlot(SLOT_FUEL);
            ItemStack ingredient = ((Recipe<Container>) pRecipe).assemble(this.itemsWrapped);
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

    private static int getTotalCookTime(Level pLevel, PotionDistilleryBE pBlockEntity) {
        return pLevel.getRecipeManager().getRecipeFor(ModRecipes.POTION_DISTILLING_RECIPE.get(), pBlockEntity.itemsWrapped, pLevel).map(PotionDistillingRecipe::getProcessingTime).orElse(DEFAULT_PROCESS_TIME);
    }

    public static boolean isFuel(ItemStack pStack) {
        return CommonHooks.getBurnTime(pStack, ModRecipes.POTION_DISTILLING_RECIPE.get()) > 0;
    }

    protected int getBurnDuration(ItemStack pFuel) {
        if (pFuel.isEmpty()) {
            return 0;
        } else {
            return CommonHooks.getBurnTime(pFuel, ModRecipes.POTION_DISTILLING_RECIPE.get());
        }
    }

    public void setRecipeUsed(@Nullable Recipe<?> pRecipe) {
        if (pRecipe != null) {
            ResourceLocation resourcelocation = pRecipe.getId();
            this.recipesUsed.addTo(resourcelocation, 1);
        }

    }

    public List<Recipe<?>> getRecipesToAwardAndPopExperience(ServerLevel pLevel, Vec3 pPopVec) {
        List<Recipe<?>> usedRecipes = Lists.newArrayList();

        for(Object2IntMap.Entry<ResourceLocation> entry : this.recipesUsed.object2IntEntrySet()) {
            pLevel.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> {
                usedRecipes.add(recipe);
                createExperience(pLevel, pPopVec, entry.getIntValue(), ((PotionDistillingRecipe)recipe).getExperience());
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

    public ItemStackHandler getInventory(){
        return this.items;
    }

    public RecipeWrapper getInventoryWrapper(){
        return this.itemsWrapped;
    }

    public ContainerData getDataAccess(){
        return this.dataAccess;
    }

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private final Map<Direction, LazyOptional<WrappedHandler>> directionWrappedHandlerMap =
            Map.of(Direction.DOWN, LazyOptional.of(() -> new WrappedHandler(items, (i) -> i >= SLOT_EXTRACT_MIN, (i, s) -> false)),
                    Direction.NORTH, LazyOptional.of(() -> new WrappedHandler(items, (index) -> index == SLOT_INPUT, (index, stack) -> items.isItemValid(SLOT_INPUT, stack))),
                    Direction.SOUTH, LazyOptional.of(() -> new WrappedHandler(items, (i) -> i >= SLOT_EXTRACT_MIN, (i, s) -> false)),
                    Direction.EAST, LazyOptional.of(() -> new WrappedHandler(items, (i) -> i == SLOT_INPUT, (index, stack) -> items.isItemValid(SLOT_INPUT, stack))),
                    Direction.WEST, LazyOptional.of(() -> new WrappedHandler(items, (index) -> index == SLOT_FUEL || index == SLOT_INPUT, (index, stack) -> items.isItemValid(SLOT_FUEL, stack) || items.isItemValid(SLOT_INPUT, stack))));

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == Capabilities.ITEM_HANDLER) {
            if (side == null) {
                return lazyItemHandler.cast();
            }

            if (directionWrappedHandlerMap.containsKey(side)) {
                Direction localDir = this.getBlockState().getValue(PotionDistilleryBlock.FACING);

                if (side == Direction.UP || side == Direction.DOWN) {
                    return directionWrappedHandlerMap.get(side).cast();
                }

                return switch (localDir) {
                    default -> directionWrappedHandlerMap.get(side.getOpposite()).cast();
                    case EAST -> directionWrappedHandlerMap.get(side.getClockWise()).cast();
                    case SOUTH -> directionWrappedHandlerMap.get(side).cast();
                    case WEST -> directionWrappedHandlerMap.get(side.getCounterClockWise()).cast();
                };
            }
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
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

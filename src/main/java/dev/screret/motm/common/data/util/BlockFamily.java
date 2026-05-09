package dev.screret.motm.common.data.util;

import net.minecraft.core.Holder;
import net.minecraft.util.StringUtil;
import net.minecraft.world.level.block.Block;

import lombok.Getter;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

public class BlockFamily {

    @Getter
    private final Holder<Block> baseBlock;
    @Getter
    private final Map<BlockFamily.Variant, Holder<Block>> variants = new EnumMap<>(BlockFamily.Variant.class);
    private boolean generateModel = false;
    private boolean generateRecipe = true;
    private @Nullable String recipeGroupPrefix;
    private @Nullable String recipeUnlockedBy;

    BlockFamily(Holder<Block> baseBlock) {
        this.baseBlock = baseBlock;
    }

    public Holder<Block> get(BlockFamily.Variant variant) {
        return this.variants.get(variant);
    }

    // not used, too annoying to get working
    public boolean shouldGenerateModel() {
        return this.generateModel;
    }

    public boolean shouldGenerateRecipe() {
        return this.generateRecipe;
    }

    public Optional<String> getRecipeGroupPrefix() {
        return StringUtil.isBlank(this.recipeGroupPrefix) ? Optional.empty() : Optional.of(this.recipeGroupPrefix);
    }

    public Optional<String> getRecipeUnlockedBy() {
        return StringUtil.isBlank(this.recipeUnlockedBy) ? Optional.empty() : Optional.of(this.recipeUnlockedBy);
    }

    @SuppressWarnings("unused")
    public record Builder(BlockFamily family) {

        public Builder(Holder<Block> baseBlock) {
            this(new BlockFamily(baseBlock));
        }

        public Builder button(Holder<Block> buttonBlock) {
            this.family.variants.put(Variant.BUTTON, buttonBlock);
            return this;
        }

        public Builder chiseled(Holder<Block> chiseledBlock) {
            this.family.variants.put(Variant.CHISELED, chiseledBlock);
            return this;
        }

        public Builder mosaic(Holder<Block> mosaicBlock) {
            this.family.variants.put(Variant.MOSAIC, mosaicBlock);
            return this;
        }

        public Builder cracked(Holder<Block> crackedBlock) {
            this.family.variants.put(Variant.CRACKED, crackedBlock);
            return this;
        }

        public Builder cut(Holder<Block> cutBlock) {
            this.family.variants.put(Variant.CUT, cutBlock);
            return this;
        }

        public Builder door(Holder<Block> doorBlock) {
            this.family.variants.put(Variant.DOOR, doorBlock);
            return this;
        }

        public Builder customFence(Holder<Block> customFenceBlock) {
            this.family.variants.put(Variant.CUSTOM_FENCE, customFenceBlock);
            return this;
        }

        public Builder fence(Holder<Block> fenceBlock) {
            this.family.variants.put(Variant.FENCE, fenceBlock);
            return this;
        }

        public Builder customFenceGate(Holder<Block> customFenceGateBlock) {
            this.family.variants.put(Variant.CUSTOM_FENCE_GATE, customFenceGateBlock);
            return this;
        }

        public Builder fenceGate(Holder<Block> fenceGateBlock) {
            this.family.variants.put(Variant.FENCE_GATE, fenceGateBlock);
            return this;
        }

        public Builder sign(Holder<Block> signBlock, Holder<Block> wallSignBlock) {
            this.family.variants.put(Variant.SIGN, signBlock);
            this.family.variants.put(Variant.WALL_SIGN, wallSignBlock);
            return this;
        }

        public Builder slab(Holder<Block> slabBlock) {
            this.family.variants.put(Variant.SLAB, slabBlock);
            return this;
        }

        public Builder stairs(Holder<Block> stairsBlock) {
            this.family.variants.put(Variant.STAIRS, stairsBlock);
            return this;
        }

        public Builder pressurePlate(Holder<Block> pressurePlateBlock) {
            this.family.variants.put(Variant.PRESSURE_PLATE, pressurePlateBlock);
            return this;
        }

        public Builder polished(Holder<Block> polishedBlock) {
            this.family.variants.put(Variant.POLISHED, polishedBlock);
            return this;
        }

        public Builder trapdoor(Holder<Block> trapdoorBlock) {
            this.family.variants.put(Variant.TRAPDOOR, trapdoorBlock);
            return this;
        }

        public Builder wall(Holder<Block> wallBlock) {
            this.family.variants.put(Variant.WALL, wallBlock);
            return this;
        }

        public Builder dontGenerateModel() {
            this.family.generateModel = false;
            return this;
        }

        public Builder dontGenerateRecipe() {
            this.family.generateRecipe = false;
            return this;
        }

        public Builder recipeGroupPrefix(String recipeGroupPrefix) {
            this.family.recipeGroupPrefix = recipeGroupPrefix;
            return this;
        }

        public Builder recipeUnlockedBy(String recipeUnlockedBy) {
            this.family.recipeUnlockedBy = recipeUnlockedBy;
            return this;
        }

        public BlockFamily build() {
            return this.family;
        }
    }

    public enum Variant {

        BUTTON("button"),
        CHISELED("chiseled"),
        CRACKED("cracked"),
        CUT("cut"),
        DOOR("door"),
        CUSTOM_FENCE("fence"),
        FENCE("fence"),
        CUSTOM_FENCE_GATE("fence_gate"),
        FENCE_GATE("fence_gate"),
        MOSAIC("mosaic"),
        SIGN("sign"),
        SLAB("slab"),
        STAIRS("stairs"),
        PRESSURE_PLATE("pressure_plate"),
        POLISHED("polished"),
        TRAPDOOR("trapdoor"),
        WALL("wall"),
        WALL_SIGN("wall_sign");

        @Getter
        private final String variantName;

        Variant(String variantName) {
            this.variantName = variantName;
        }
    }
}

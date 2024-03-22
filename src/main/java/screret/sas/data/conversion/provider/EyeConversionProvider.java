package screret.sas.data.conversion.provider;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import org.slf4j.Logger;
import screret.sas.Util;
import screret.sas.data.conversion.builder.EyeConversionBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class EyeConversionProvider implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final PackOutput.PathProvider pathProvider;


    public EyeConversionProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, "eye_conversions");
    }

    protected void buildCraftingRecipes(Consumer<EyeConversionBuilder.Result> finished) {
        addConversion(finished, Blocks.GOLD_BLOCK, Tags.Blocks.STORAGE_BLOCKS_IRON);
        addConversion(finished, Blocks.IRON_BLOCK, Tags.Blocks.STORAGE_BLOCKS_GOLD);
        addConversion(finished, Blocks.EMERALD_BLOCK, Tags.Blocks.STORAGE_BLOCKS_DIAMOND);
        addConversion(finished, Blocks.COAL_BLOCK, Tags.Blocks.STORAGE_BLOCKS_RAW_COPPER);
        addConversion(finished, Blocks.RAW_COPPER_BLOCK, Tags.Blocks.STORAGE_BLOCKS_COAL);
        addConversion(finished, Blocks.COBBLED_DEEPSLATE, Tags.Blocks.COBBLESTONE_NORMAL);
        addConversion(finished, Blocks.COBBLESTONE, Tags.Blocks.COBBLESTONE_DEEPSLATE);

        addConversion(finished, Blocks.ZOMBIE_HEAD, Blocks.SKELETON_SKULL);
        addConversion(finished, Blocks.SKELETON_SKULL, Blocks.ZOMBIE_HEAD);
        addConversion(finished, Blocks.ZOMBIE_WALL_HEAD, Blocks.SKELETON_WALL_SKULL);
        addConversion(finished, Blocks.SKELETON_WALL_SKULL, Blocks.ZOMBIE_WALL_HEAD);

    }


    protected void addConversion(Consumer<EyeConversionBuilder.Result> finished, Block result, TagKey<Block> items){
        EyeConversionBuilder.conversion(result)
                .requires(items)
                .save(finished, Util.id(BuiltInRegistries.BLOCK.getKey(result).getPath()));
    }

    protected void addConversion(Consumer<EyeConversionBuilder.Result> finished, Block result, Block item){
        EyeConversionBuilder.conversion(result)
                .requires(item)
                .save(finished, Util.id(BuiltInRegistries.BLOCK.getKey(result).getPath()));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        Set<ResourceLocation> set = Sets.newHashSet();
        List<CompletableFuture<?>> futures = new ArrayList<>();
        buildCraftingRecipes((result) -> {
            if (!set.add(result.getId())) {
                throw new IllegalStateException("Duplicate recipe " + result.getId());
            } else {
                futures.add(DataProvider.saveStable(pOutput, result.serializeRecipe(), this.pathProvider.json(result.getId())));
            }
        });

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

        @Override
    public String getName() {
        return "Eye Conversions";
    }
}

package screret.sas.resource;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import screret.sas.recipe.ingredient.BlockIngredient;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

public class EyeConversionManager extends SimpleJsonResourceReloadListener {
    public static EyeConversionManager INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON_INSTANCE = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient().create();
    private static final String folder = "eye_conversions";

    private Map<Block, BlockIngredient> registeredConversions = ImmutableMap.of();


    public EyeConversionManager() {
        super(GSON_INSTANCE, folder);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceList, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        ImmutableMap.Builder<Block, BlockIngredient> builder = ImmutableMap.builder();

        for (Map.Entry<ResourceLocation, JsonElement> entry : resourceList.entrySet()) {
            ResourceLocation recipeLocation = entry.getKey();

            try {
                Map.Entry<Block, BlockIngredient> recipe = fromJson(recipeLocation, GsonHelper.convertToJsonObject(entry.getValue(), "top element"));
                if (recipe == null) {
                    LOGGER.info("Skipping loading recipe {} as it's serializer returned null", recipeLocation);
                    continue;
                }
                builder.put(recipe);
            } catch (
                    IllegalArgumentException |
                    JsonParseException jsonparseexception) {
                LOGGER.error("Parsing error loading recipe {}", recipeLocation, jsonparseexception);
            }
        }

        this.registeredConversions = builder.build();
        LOGGER.info("Loaded {} recipes", registeredConversions.size());
    }

    public static Map.Entry<Block, BlockIngredient> fromJson(ResourceLocation pRecipeId, JsonObject pJson) {
        Block result = BuiltInRegistries.BLOCK.get(new ResourceLocation(pJson.getAsJsonPrimitive("result").getAsString()));
        BlockIngredient ingredient = BlockIngredient.fromJson(GsonHelper.getAsJsonObject(pJson, "ingredient"));
        if (ingredient == null)
            return null;
        return new AbstractMap.SimpleImmutableEntry<>(result, ingredient);
    }

    /**
     * An immutable collection of the registered eye conversions in layered order.
     */
    public Set<Map.Entry<Block, BlockIngredient>> getAllConversions() {
        return registeredConversions.entrySet();
    }
}

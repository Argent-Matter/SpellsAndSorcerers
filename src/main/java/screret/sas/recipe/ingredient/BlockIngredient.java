package screret.sas.recipe.ingredient;

import com.google.common.collect.Lists;
import com.google.gson.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class BlockIngredient implements Predicate<BlockState> {

    public static final BlockIngredient EMPTY = new BlockIngredient(Stream.empty());
    private final Value[] values;
    @Nullable
    private BlockState[] blocks;

    protected BlockIngredient(Stream<? extends Value> pValues) {
        this.values = pValues.toArray(Value[]::new);
    }

    public BlockState[] getBlocks() {
        this.dissolve();
        return this.blocks;
    }

    private void dissolve() {
        if (this.blocks == null) {
            this.blocks = Arrays.stream(this.values).flatMap((value) -> {
                return value.getBlocks().stream();
            }).distinct().toArray(BlockState[]::new);
        }

    }

    public boolean test(@Nullable BlockState block) {
        if (block != null) {
            this.dissolve();
            if (this.blocks.length == 0) {
                return block.isAir();
            }

            for (var element : this.blocks) {
                if (block.is(element.getBlock()))
                    return true;
            }

        }
        return false;
    }

    public final void toNetwork(FriendlyByteBuf pBuffer) {
        this.dissolve();
        pBuffer.writeCollection(Arrays.stream(this.blocks).map(BlockState::getBlock).map(BuiltInRegistries.BLOCK::getKey).toList(), FriendlyByteBuf::writeResourceLocation);
    }

    public JsonElement toJson() {
        if (this.values.length == 1) {
            return this.values[0].serialize();
        } else {
            JsonArray jsonarray = new JsonArray();

            for (Value value : this.values) {
                jsonarray.add(value.serialize());
            }

            return jsonarray;
        }
    }

    public boolean isEmpty() {
        return this.values.length == 0 && (this.blocks == null || this.blocks.length == 0);
    }

    protected void invalidate() {
        this.blocks = null;
    }

    public BlockIngredientSerializer getSerializer() {
        return BlockIngredientSerializer.INSTANCE;
    }

    public static BlockIngredient fromValues(Stream<? extends Value> pStream) {
        BlockIngredient BlockIngredient = new BlockIngredient(pStream);
        return BlockIngredient.values.length == 0 ? EMPTY : BlockIngredient;
    }

    public static BlockIngredient of() {
        return EMPTY;
    }

    public static BlockIngredient of(Block... pStacks) {
        return of(Arrays.stream(pStacks).map(Block::defaultBlockState));
    }

    public static BlockIngredient of(BlockState... pStacks) {
        return of(Arrays.stream(pStacks));
    }

    public static BlockIngredient of(Stream<BlockState> pStacks) {
        return fromValues(pStacks.filter((p_43944_) -> !p_43944_.isAir()).map(BlockValue::new));
    }

    public static BlockIngredient of(TagKey<Block> pTag) {
        return fromValues(Stream.of(new TagValue(pTag)));
    }

    public static BlockIngredient fromNetwork(FriendlyByteBuf pBuffer) {
        var size = pBuffer.readVarInt();
        if (size == -1)
            return BlockIngredientSerializer.INSTANCE.parse(pBuffer);
        return fromValues(Stream.generate(() -> new BlockValue(pBuffer.readResourceLocation())).limit(size));
    }

    public static BlockIngredient fromJson(@Nullable JsonElement pJson) {
        if (pJson != null && !pJson.isJsonNull()) {
            BlockIngredient ret = BlockIngredientSerializer.INSTANCE.parse(pJson.getAsJsonObject());
            if (ret != null)
                return ret;
            if (pJson.isJsonObject()) {
                return fromValues(Stream.of(valueFromJson(pJson.getAsJsonObject())));
            } else if (pJson.isJsonArray()) {
                JsonArray jsonarray = pJson.getAsJsonArray();
                if (jsonarray.size() == 0) {
                    throw new JsonSyntaxException("Item array cannot be empty, at least one item must be defined");
                } else {
                    return fromValues(StreamSupport.stream(jsonarray.spliterator(), false).map((element) -> {
                        return valueFromJson(GsonHelper.convertToJsonObject(element, "item"));
                    }));
                }
            } else {
                throw new JsonSyntaxException("Expected item to be object or array of objects");
            }
        } else {
            throw new JsonSyntaxException("Item cannot be null");
        }
    }

    public static Value valueFromJson(JsonObject pJson) {
        if (pJson.has("block") && pJson.has("tag")) {
            throw new JsonParseException("An BlockIngredient entry is either a tag or an item, not both");
        } else if (pJson.has("block")) {
            return new BlockValue(blockFromJson(pJson));
        } else if (pJson.has("tag")) {
            ResourceLocation resourcelocation = new ResourceLocation(GsonHelper.getAsString(pJson, "tag"));
            TagKey<Block> key = TagKey.create(Registries.BLOCK, resourcelocation);
            return new TagValue(key);
        } else {
            throw new JsonParseException("An BlockIngredient entry needs either a tag or a block");
        }
    }

    public static Block blockFromJson(JsonObject pItemObject) {
        String s = GsonHelper.getAsString(pItemObject, "block");
        Block block = BuiltInRegistries.BLOCK.getOptional(new ResourceLocation(s)).orElseThrow(() -> new JsonSyntaxException("Unknown item '" + s + "'"));
        if (block == Blocks.AIR) {
            throw new JsonSyntaxException("Invalid item: " + s);
        } else {
            return block;
        }
    }

    public static class BlockValue implements Value {
        private final BlockState block;

        public BlockValue(BlockState block) {
            this.block = block;
        }

        public BlockValue(Block block) {
            this.block = block.defaultBlockState();
        }

        public BlockValue(ResourceLocation location) {
            this.block = BuiltInRegistries.BLOCK.get(location).defaultBlockState();
        }

        public Collection<BlockState> getBlocks() {
            return Collections.singleton(this.block);
        }

        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("block", BuiltInRegistries.BLOCK.getKey(this.block.getBlock()).toString());
            return jsonobject;
        }
    }

    public static class TagValue implements Value {
        private final TagKey<Block> tag;

        public TagValue(TagKey<Block> pTag) {
            this.tag = pTag;
        }

        public Collection<BlockState> getBlocks() {
            List<BlockState> list = Lists.newArrayList();

            for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(this.tag)) {
                list.add(holder.value().defaultBlockState());
            }

            if (list.size() == 0) {
                list.add(Blocks.BARRIER.defaultBlockState());
            }
            return list;
        }

        public JsonObject serialize() {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("tag", this.tag.location().toString());
            return jsonobject;
        }
    }

    public interface Value {
        Collection<BlockState> getBlocks();

        JsonObject serialize();
    }
}

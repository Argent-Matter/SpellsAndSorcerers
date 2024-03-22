package screret.sas.recipe.ingredient;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.ForgeRegistries;
import java.util.stream.Stream;

public class BlockIngredientSerializer
{
    public static final BlockIngredientSerializer INSTANCE  = new BlockIngredientSerializer();

    public BlockIngredient parse(FriendlyByteBuf buffer)
    {
        return BlockIngredient.fromValues(Stream.generate(() -> new BlockIngredient.BlockValue(buffer.<Block>readRegistryId())).limit(buffer.readVarInt()));
    }

    public BlockIngredient parse(JsonObject json)
    {
        return BlockIngredient.fromValues(Stream.of(BlockIngredient.valueFromJson(json)));
    }

    public void write(FriendlyByteBuf buffer, BlockIngredient ingredient)
    {
        BlockState[] blocks = ingredient.getBlocks();
        buffer.writeVarInt(blocks.length);

        for (BlockState block : blocks)
            buffer.writeRegistryId(ForgeRegistries.BLOCKS, block.getBlock());
    }
}

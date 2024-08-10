package screret.sas.block;

import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import screret.sas.SpellsAndSorcerers;
import screret.sas.block.block.PalantirBlock;
import screret.sas.block.block.PotionDistilleryBlock;
import screret.sas.block.block.SummonSignBlock;
import screret.sas.block.block.WandTableBlock;

public class ModBlocks {

    // Create a Deferred Register to hold Blocks which will all be registered under the "sas" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SpellsAndSorcerers.MODID);


    public static final RegistryObject<Block> WAND_TABLE = BLOCKS.register("wand_table", WandTableBlock::new);
    public static final RegistryObject<Block> SUMMON_SIGN = BLOCKS.register("summon_sign", SummonSignBlock::new);
    public static final RegistryObject<Block> PALANTIR = BLOCKS.register("palantir", PalantirBlock::new);
    public static final RegistryObject<Block> POTION_DISTILLERY = BLOCKS.register("potion_distillery", PotionDistilleryBlock::new);


    public static final RegistryObject<Block> SOULSTEEL_BLOCK = BLOCKS.register("soulsteel_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.NETHERITE_BLOCK).strength(5.0F)));
    public static final RegistryObject<Block> GLINT_ORE = BLOCKS.register("glint_ore", () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE).requiresCorrectToolForDrops().strength(3.0F, 9.0F), UniformInt.of(5, 10)));
}

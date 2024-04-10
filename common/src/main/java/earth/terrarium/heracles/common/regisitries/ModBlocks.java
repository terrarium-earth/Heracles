package earth.terrarium.heracles.common.regisitries;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.blocks.BarrierBlock;
import earth.terrarium.heracles.common.blocks.BarrierBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlocks {

    public static final ResourcefulRegistry<BlockEntityType<?>> BLOCK_ENTITIES = ResourcefulRegistries.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Heracles.MOD_ID);
    public static final ResourcefulRegistry<Block> BLOCKS = ResourcefulRegistries.create(BuiltInRegistries.BLOCK, Heracles.MOD_ID);


    public static final RegistryEntry<Block> BARRIER_BLOCK = BLOCKS.register(
        "barrier",
        () -> new BarrierBlock(Block.Properties.copy(Blocks.BARRIER))
    );

    public static final RegistryEntry<BlockEntityType<BarrierBlockEntity>> BARRIER_BLOCK_ENTITY = BLOCK_ENTITIES.register(
        "barrier",
        () -> BlockEntityType.Builder.of(BarrierBlockEntity::new, ModBlocks.BARRIER_BLOCK.get()).build(null)
    );
}

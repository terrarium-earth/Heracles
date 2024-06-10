package earth.terrarium.heracles.common.regisitries;

import com.teamresourceful.resourcefullib.common.registry.RegistryEntry;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistries;
import com.teamresourceful.resourcefullib.common.registry.ResourcefulRegistry;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.blocks.BarrierBlock;
import earth.terrarium.heracles.common.blocks.BarrierBlockEntity;
import earth.terrarium.heracles.common.handlers.CommonConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public class ModBlocks {

    public static final ResourcefulRegistry<BlockEntityType<?>> BLOCK_ENTITIES = ResourcefulRegistries.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Heracles.MOD_ID);
    public static final ResourcefulRegistry<Block> BLOCKS = ResourcefulRegistries.create(BuiltInRegistries.BLOCK, Heracles.MOD_ID);


    public static final @Nullable RegistryEntry<Block> BARRIER_BLOCK = !CommonConfig.registerUtilities ? null : BLOCKS.register(
        "barrier",
        () -> new BarrierBlock(Block.Properties.copy(Blocks.BARRIER))
    );

    public static final @Nullable RegistryEntry<BlockEntityType<BarrierBlockEntity>> BARRIER_BLOCK_ENTITY = BARRIER_BLOCK == null ? null : BLOCK_ENTITIES.register(
        "barrier",
        () -> BlockEntityType.Builder.of(BarrierBlockEntity::new, ModBlocks.BARRIER_BLOCK.get()).build(null)
    );
}

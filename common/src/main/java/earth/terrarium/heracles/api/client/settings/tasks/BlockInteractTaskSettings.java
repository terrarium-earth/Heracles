package earth.terrarium.heracles.api.client.settings.tasks;

import com.mojang.datafixers.util.Either;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import com.teamresourceful.resourcefullib.common.codecs.predicates.properties.BlockStatePredicate;
import earth.terrarium.heracles.api.client.settings.SettingInitializer;
import earth.terrarium.heracles.api.client.settings.base.RegistryValueSetting;
import earth.terrarium.heracles.api.tasks.defaults.BlockInteractTask;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.Optionull;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

public class BlockInteractTaskSettings implements SettingInitializer<BlockInteractTask> {

    public static final BlockInteractTaskSettings INSTANCE = new BlockInteractTaskSettings();

    @Override
    public CreationData create(@Nullable BlockInteractTask object) {
        CreationData settings = new CreationData();
        settings.put("block", RegistryValueSetting.BLOCK, getDefaultBlock(object));
        return settings;
    }

    @Override
    public BlockInteractTask create(String id, @Nullable BlockInteractTask object, Data data) {
        return new BlockInteractTask(
            id,
            data.get("block", RegistryValueSetting.BLOCK).orElse(getDefaultBlock(object)),
            Optionull.mapOrDefault(object, BlockInteractTask::state, BlockStatePredicate.ANY),
            Optionull.mapOrDefault(object, BlockInteractTask::nbt, NbtPredicate.ANY)
        );
    }

    private static RegistryValue<Block> getDefaultBlock(BlockInteractTask object) {
        return Optionull.mapOrDefault(object, BlockInteractTask::block, new RegistryValue<>(Either.left(Blocks.AIR.builtInRegistryHolder())));
    }
}

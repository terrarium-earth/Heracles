package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import com.teamresourceful.resourcefullib.common.codecs.predicates.properties.BlockStatePredicate;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public record BlockInteractionTask(String id, HolderSet<Block> block, BlockStatePredicate state,
                                   NbtPredicate nbt) implements QuestTask<Pair<ServerLevel, BlockPos>, ByteTag, BlockInteractionTask> {

    public static final QuestTaskType<BlockInteractionTask> TYPE = new Type();

    @Override
    public ByteTag test(QuestTaskType<?> type, ByteTag progress, Pair<ServerLevel, BlockPos> input) {
        BlockState blockState = input.getFirst().getBlockState(input.getSecond());
        BlockEntity blockEntity = input.getFirst().getBlockEntity(input.getSecond());
        return storage().of(progress,
            block().contains(blockState.getBlockHolder()) &&
                state().matches(blockState) &&
                nbt().matches(blockEntity == null ? null : blockEntity.saveWithFullMetadata())
        );
    }

    @Override
    public float getProgress(ByteTag progress) {
        return storage().readBoolean(progress) ? 1 : 0;
    }

    @Override
    public BooleanTaskStorage storage() {
        return BooleanTaskStorage.INSTANCE;
    }

    @Override
    public QuestTaskType<BlockInteractionTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<BlockInteractionTask> {
        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "block_interaction");
        }

        @Override
        public Codec<BlockInteractionTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("block").forGetter(BlockInteractionTask::block),
                BlockStatePredicate.CODEC.fieldOf("state").orElse(BlockStatePredicate.ANY).forGetter(BlockInteractionTask::state),
                NbtPredicate.CODEC.fieldOf("nbt").orElse(NbtPredicate.ANY).forGetter(BlockInteractionTask::nbt)
            ).apply(instance, BlockInteractionTask::new));
        }
    }
}

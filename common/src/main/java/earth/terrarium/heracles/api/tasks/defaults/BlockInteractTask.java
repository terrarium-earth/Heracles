package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.predicates.NbtPredicate;
import com.teamresourceful.resourcefullib.common.codecs.predicates.properties.BlockStatePredicate;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.CustomizableQuestElement;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import earth.terrarium.heracles.common.utils.RegistryValue;
import net.minecraft.Optionull;
import net.minecraft.core.BlockSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public record BlockInteractTask(
    String id, String title, QuestIcon<?> icon, RegistryValue<Block> block, BlockStatePredicate state, NbtPredicate nbt
) implements QuestTask<BlockSource, ByteTag, BlockInteractTask>, CustomizableQuestElement {

    public static final QuestTaskType<BlockInteractTask> TYPE = new Type();

    @Override
    public ByteTag test(QuestTaskType<?> type, ByteTag progress, BlockSource input) {
        BlockState blockState = input.getBlockState();
        BlockEntity blockEntity = input.getEntity();
        return storage().of(progress,
            block().is(blockState.getBlockHolder()) &&
                state().matches(blockState) &&
                nbt().matches(Optionull.map(blockEntity, BlockEntity::saveWithFullMetadata))
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
    public QuestTaskType<BlockInteractTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<BlockInteractTask> {
        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "block_interaction");
        }

        @Override
        public Codec<BlockInteractTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.fieldOf("title").orElse("").forGetter(BlockInteractTask::title),
                QuestIcons.CODEC.fieldOf("icon").orElse(new ItemQuestIcon(Items.AIR)).forGetter(BlockInteractTask::icon),
                RegistryValue.codec(Registries.BLOCK).fieldOf("block").forGetter(BlockInteractTask::block),
                BlockStatePredicate.CODEC.fieldOf("state").orElse(BlockStatePredicate.ANY).forGetter(BlockInteractTask::state),
                NbtPredicate.CODEC.fieldOf("nbt").orElse(NbtPredicate.ANY).forGetter(BlockInteractTask::nbt)
            ).apply(instance, BlockInteractTask::new));
        }
    }
}

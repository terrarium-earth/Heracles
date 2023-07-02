package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.PairQuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.IntegerTaskStorage;
import net.minecraft.nbt.NumericTag;
import net.minecraft.resources.ResourceLocation;

public record StatTask(
    String id, ResourceLocation stat, int target
) implements PairQuestTask<ResourceLocation, Integer, NumericTag, StatTask> {

    public static final QuestTaskType<StatTask> TYPE = new Type();

    @Override
    public NumericTag test(QuestTaskType<?> type, NumericTag progress, ResourceLocation stat, Integer amount) {
        if (this.stat.equals(stat)) {
            return storage().set(amount);
        }
        return progress;
    }

    @Override
    public float getProgress(NumericTag progress) {
        return progress.getAsInt() / (float) target;
    }

    @Override
    public IntegerTaskStorage storage() {
        return IntegerTaskStorage.INSTANCE;
    }

    @Override
    public QuestTaskType<StatTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<StatTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "stat");
        }

        @Override
        public Codec<StatTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                ResourceLocation.CODEC.fieldOf("stat").forGetter(StatTask::stat),
                Codec.INT.fieldOf("target").forGetter(StatTask::target)
            ).apply(instance, StatTask::new));
        }
    }
}

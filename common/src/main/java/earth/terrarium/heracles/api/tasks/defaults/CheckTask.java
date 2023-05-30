package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceLocation;

public record CheckTask(String id, String checkId) implements QuestTask<String, ByteTag, CheckTask> {

    public static final QuestTaskType<CheckTask> TYPE = new Type();

    @Override
    public ByteTag test(QuestTaskType<?> type, ByteTag progress, String input) {
        return storage().of(progress, checkId.equals(input));
    }

    @Override
    public float getProgress(ByteTag progress) {
        return storage().readBoolean(progress) ? 1.0F : 0.0F;
    }

    @Override
    public BooleanTaskStorage storage() {
        return BooleanTaskStorage.INSTANCE;
    }

    @Override
    public QuestTaskType<CheckTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<CheckTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "check");
        }

        @Override
        public Codec<CheckTask> codec(String id) {
            return Codec.STRING.xmap(s -> new CheckTask(id, s), CheckTask::checkId);
        }
    }
}

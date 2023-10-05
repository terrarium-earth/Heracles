package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.CustomizableQuestElement;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public record DummyTask(
    String id, String title, QuestIcon<?> icon, String dummyId, String description
) implements QuestTask<String, ByteTag, DummyTask>, CustomizableQuestElement {

    public static final QuestTaskType<DummyTask> TYPE = new Type();

    @Override
    public ByteTag test(QuestTaskType<?> type, ByteTag progress, String input) {
        return storage().of(progress, dummyId.equals(input));
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
    public QuestTaskType<DummyTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<DummyTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "dummy");
        }

        @Override
        public Codec<DummyTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.fieldOf("title").orElse("").forGetter(DummyTask::title),
                QuestIcons.CODEC.fieldOf("icon").orElse(new ItemQuestIcon(Items.AIR)).forGetter(DummyTask::icon),
                Codec.STRING.fieldOf("value").forGetter(DummyTask::dummyId),
                Codec.STRING.fieldOf("description").orElse("").forGetter(DummyTask::title)
            ).apply(instance, DummyTask::new));
        }
    }
}

package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public record DummyTask(
    String id, String dummyId, Item icon, Component title, Component description
) implements QuestTask<String, ByteTag, DummyTask> {

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
                Codec.STRING.fieldOf("value").forGetter(DummyTask::dummyId),
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("icon").orElseGet(() -> Items.BARRIER).forGetter(DummyTask::icon),
                ExtraCodecs.FLAT_COMPONENT.fieldOf("title").orElseGet(Component::empty).forGetter(DummyTask::title),
                ExtraCodecs.FLAT_COMPONENT.fieldOf("description").orElseGet(Component::empty).forGetter(DummyTask::description)
            ).apply(instance, DummyTask::new));
        }
    }
}

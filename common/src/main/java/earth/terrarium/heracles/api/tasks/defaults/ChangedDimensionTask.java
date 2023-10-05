package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.datafixers.util.Pair;
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
import net.minecraft.Optionull;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.ByteTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Optional;

public record ChangedDimensionTask(
    String id, String title, QuestIcon<?> icon, ResourceKey<Level> from, ResourceKey<Level> to
) implements QuestTask<Pair<ResourceKey<Level>, ResourceKey<Level>>, ByteTag, ChangedDimensionTask>, CustomizableQuestElement {

    public static final QuestTaskType<ChangedDimensionTask> TYPE = new Type();

    @Override
    public ByteTag test(QuestTaskType<?> type, ByteTag progress, Pair<ResourceKey<Level>, ResourceKey<Level>> input) {
        final ResourceKey<Level> from = input.getFirst();
        final ResourceKey<Level> to = input.getSecond();

        return storage().of(progress,
            Optionull.mapOrDefault(this.from, value -> value.equals(from), true) &&
                Optionull.mapOrDefault(this.to, value -> value.equals(to), true)
        );
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
    public QuestTaskType<ChangedDimensionTask> type() {
        return TYPE;
    }

    private static class Type implements QuestTaskType<ChangedDimensionTask> {

        @Override
        public ResourceLocation id() {
            return new ResourceLocation(Heracles.MOD_ID, "changed_dimension");
        }

        @Override
        public Codec<ChangedDimensionTask> codec(String id) {
            return RecordCodecBuilder.create(instance -> instance.group(
                RecordCodecBuilder.point(id),
                Codec.STRING.fieldOf("title").orElse("").forGetter(ChangedDimensionTask::title),
                QuestIcons.CODEC.fieldOf("icon").orElse(new ItemQuestIcon(Items.AIR)).forGetter(ChangedDimensionTask::icon),
                ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("from").forGetter(task -> Optional.ofNullable(task.from())),
                ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("to").forGetter(task -> Optional.ofNullable(task.to()))
            ).apply(instance, (i, title, icon, from, to) -> new ChangedDimensionTask(i, title, icon, from.orElse(null), to.orElse(null))));
        }
    }
}

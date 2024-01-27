package earth.terrarium.heracles.api.tasks.defaults;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.CustomizableQuestElement;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.QuestIcons;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.tasks.PairQuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.tasks.storage.defaults.IntegerTaskStorage;
import net.minecraft.nbt.NumericTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.item.Items;

public record StatTask(
    String id, String title, QuestIcon<?> icon, ResourceLocation stat, int target
) implements PairQuestTask<ResourceLocation, Integer, NumericTag, StatTask>, CustomizableQuestElement {

    public static final QuestTaskType<StatTask> TYPE = new Type();

    @Override
    public NumericTag test(QuestTaskType<?> type, NumericTag progress, ResourceLocation stat, Integer amount) {
        if (this.stat.equals(stat)) {
            return storage().max(progress, amount);
        }
        return progress;
    }

    @Override
    public NumericTag init(QuestTaskType<?> type, NumericTag progress, ServerPlayer player) {
        int value = player.getStats().getValue(Stats.CUSTOM, this.stat);
        return value == 0 ? progress : storage().max(progress, value);
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
                Codec.STRING.fieldOf("title").orElse("").forGetter(StatTask::title),
                QuestIcons.CODEC.fieldOf("icon").orElse(new ItemQuestIcon(Items.AIR)).forGetter(StatTask::icon),
                ResourceLocation.CODEC.fieldOf("stat").forGetter(StatTask::stat),
                Codec.INT.fieldOf("target").forGetter(StatTask::target)
            ).apply(instance, StatTask::new));
        }
    }
}

package earth.terrarium.heracles.common.handlers.progress;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.codecs.CodecExtras;
import com.teamresourceful.resourcefullib.common.codecs.maps.DispatchMapCodec;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.tasks.QuestTask;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QuestProgress {

    public static Codec<QuestProgress> codec(Quest quest) {
        return RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("complete").orElse(false).forGetter(QuestProgress::isComplete),
            CodecExtras.set(Codec.STRING).fieldOf("rewards").orElse(new HashSet<>()).forGetter(QuestProgress::claimedRewards),
            DispatchMapCodec.of(Codec.STRING, id -> TaskProgress.codec(quest.tasks().get(id)))
                .orElse(new HashMap<>()).fieldOf("tasks").forGetter(QuestProgress::tasks)
        ).apply(instance, QuestProgress::new));
    }

    private final Map<String, TaskProgress<?>> tasks = new HashMap<>();
    private final Set<String> claimed = new HashSet<>();
    private boolean complete;

    public QuestProgress() {
        this.complete = false;
    }

    public QuestProgress(boolean complete, Set<String> claimed, Map<String, TaskProgress<?>> tasks) {
        this.complete = complete;
        this.claimed.addAll(claimed);
        this.tasks.putAll(tasks);
    }

    public void update(Quest quest) {
        if (complete) return;
        for (QuestTask<?, ?, ?> task : quest.tasks().values()) {
            if (!tasks.containsKey(task.id())) {
                tasks.put(task.id(), new TaskProgress<>(task));
            }
        }
        for (TaskProgress<?> task : tasks.values()) {
            if (!task.isComplete()) {
                return;
            }
        }
        complete = true;
    }

    public boolean isComplete() {
        return complete;
    }

    public void claimReward(String reward) {
        claimed.add(reward);
    }

    public Set<String> claimedRewards() {
        return claimed;
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag> TaskProgress<T> getTask(QuestTask<?, T, ?> task) {
        return (TaskProgress<T>) this.tasks.computeIfAbsent(task.id(), s -> new TaskProgress<>(task));
    }

    public Map<String, TaskProgress<?>> tasks() {
        return this.tasks;
    }
}

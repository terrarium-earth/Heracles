package earth.terrarium.heracles.common.handlers.progress;

import com.teamresourceful.resourcefullib.common.nbt.TagUtils;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.tasks.QuestTask;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.*;

public class QuestProgress {

    private final Map<String, TaskProgress<?>> tasks = new HashMap<>();
    private final Set<String> claimed = new HashSet<>();
    private boolean complete;

    public QuestProgress() {
        this.complete = false;
    }

    public QuestProgress(Quest quest, CompoundTag tag) {
        if (tag == null) return;
        this.complete = tag.getBoolean("complete");
        this.claimed.addAll(TagUtils.mapToCollection(ArrayList::new, tag.getList("rewards", 8), Tag::getAsString));
        var compound = tag.getCompound("tasks");
        for (String taskKey : compound.getAllKeys()) {
            if (!quest.tasks().containsKey(taskKey)) continue;
            CompoundTag task = compound.getCompound(taskKey);
            tasks.put(taskKey, new TaskProgress<>(task.get("progress"), quest.tasks().get(taskKey).storage()::createDefault, task.getBoolean("complete")));
        }
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
        checkComplete();
    }

    public void checkComplete() {
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

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void claimReward(String reward) {
        claimed.add(reward);
    }

    public void reset() {
        for (TaskProgress<?> taskProgress : tasks.values()) {
            taskProgress.reset();
        }
        claimed.clear();
        complete = false;
    }

    public Set<String> claimedRewards() {
        return claimed;
    }

    public boolean canClaim(String reward) {
        return !claimed.contains(reward) && complete;
    }

    @SuppressWarnings("unchecked")
    public <T extends Tag> TaskProgress<T> getTask(QuestTask<?, T, ?> task) {
        return (TaskProgress<T>) this.tasks.computeIfAbsent(task.id(), s -> new TaskProgress<>(task));
    }

    public Map<String, TaskProgress<?>> tasks() {
        return this.tasks;
    }

    public void copyFrom(QuestProgress progress) {
        claimed.clear();
        claimed.addAll(progress.claimed);
        tasks.clear();
        tasks.putAll(progress.tasks);
        complete = progress.complete;
        checkComplete();
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("complete", complete);
        tag.put("rewards", TagUtils.mapToListTag(claimed, StringTag::valueOf));
        CompoundTag tasks = new CompoundTag();
        for (var entry : this.tasks.entrySet()) {
            CompoundTag task = new CompoundTag();
            task.put("progress", entry.getValue().progress());
            task.putBoolean("complete", entry.getValue().isComplete());
            tasks.put(entry.getKey(), task);
        }
        tag.put("tasks", tasks);
        return tag;
    }
}

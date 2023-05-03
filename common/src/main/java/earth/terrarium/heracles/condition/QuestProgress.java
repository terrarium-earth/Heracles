package earth.terrarium.heracles.condition;

import earth.terrarium.heracles.resource.QuestTaskManager;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class QuestProgress {
    private final Set<QuestTask> completedTasks = new HashSet<>();

    public QuestProgress() {}

    public QuestProgress(ListTag tag) {
        for (int i = 0; i < tag.size(); i++) {
            QuestTask task = QuestTaskManager.INSTANCE.getTasks().get(new ResourceLocation(tag.getString(i)));

            if (task != null) {
                completedTasks.add(task);
            }
        }
    }

    public ListTag save() {
        ListTag tag = new ListTag();

        for (QuestTask task : completedTasks) {
            ResourceLocation id = QuestTaskManager.INSTANCE.getTasks().inverse().get(task);

            if (id != null) {
                tag.add(StringTag.valueOf(id.toString()));
            }
        }

        return tag;
    }

    public void completeTask(QuestTask task) {
        completedTasks.add(task);
    }

    public void completeTasks(Collection<QuestTask> tasks) {
        completedTasks.addAll(tasks);
    }

    public boolean isTaskComplete(QuestTask task) {
        return completedTasks.contains(task);
    }

    public Stream<QuestTask> totalProgress() {
        return completedTasks.stream();
    }
}

package earth.terrarium.heracles.common.handlers;

import com.google.common.collect.Maps;
import earth.terrarium.heracles.api.Quest;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.common.team.TeamProvider;
import net.minecraft.Optionull;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record QuestsProgress(Map<String, QuestProgress> progress, CompletableQuests completableQuests) {

    public QuestsProgress(Map<String, QuestProgress> progress) {
        this(progress, new CompletableQuests());
    }

    public <T> void testAndProgressTaskType(ServerPlayer player, T input, Class<? extends QuestTask<T, ?>> taskType) {
        List<String> editedQuests = new ArrayList<>();
        for (String id : this.completableQuests.getQuests(this)) {
            QuestProgress questProgress = getProgress(id);
            Quest quest = QuestHandler.get(id);
            for (QuestTask<?, ?> task : quest.tasks()) {
                if (taskType.isInstance(task.getClass())) {
                    TaskProgress progress = questProgress.getTask(task.id());
                    if (progress.isComplete()) continue;
                    progress.addProgress(taskType.cast(task), input);
                    if (progress.isComplete()) {
                        //SEND NOTIFICATION
                    }
                    editedQuests.add(id);
                }
            }
            questProgress.update(quest);
            this.progress.put(id, questProgress);
        }
        if (editedQuests.isEmpty()) return;
        this.completableQuests.updateCompleteQuests(this);

        TeamProvider.getAllTeams(player)
            .flatMap(List::stream)
            .distinct()
            .forEach(member -> {
                QuestsProgress memberProgress = QuestProgressHandler.getProgress(player.server, member);
                for (String quest : editedQuests) {
                    var currentProgress = memberProgress.progress().get(quest);
                    var questProgress = progress.get(quest);
                    var newTasks = copyTasks(questProgress.tasks());
                    memberProgress.progress.put(quest, new QuestProgress(questProgress.isComplete(), currentProgress != null && currentProgress.isClaimed(), newTasks));
                }
                memberProgress.completableQuests.updateCompleteQuests(memberProgress);
            });
    }

    private Map<String, TaskProgress> copyTasks(Map<String, TaskProgress> tasks) {
        Map<String, TaskProgress> newTasks = Maps.newHashMapWithExpectedSize(tasks.size());
        for (Map.Entry<String, TaskProgress> entry : tasks.entrySet()) {
            newTasks.put(entry.getKey(), entry.getValue().copy());
        }
        return newTasks;
    }

    public boolean isComplete(String id) {
        return Optionull.mapOrDefault(progress.get(id), QuestProgress::isComplete, false);
    }

    public boolean isClaimed(String id) {
        return Optionull.mapOrDefault(progress.get(id), QuestProgress::isClaimed, true);
    }

    public QuestProgress getProgress(String id) {
        return progress.getOrDefault(id, new QuestProgress());
    }
}

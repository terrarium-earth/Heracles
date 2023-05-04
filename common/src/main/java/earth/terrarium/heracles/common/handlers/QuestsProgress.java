package earth.terrarium.heracles.common.handlers;

import com.google.common.collect.Maps;
import earth.terrarium.heracles.api.Quest;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.common.resource.QuestManager;
import earth.terrarium.heracles.common.team.TeamProvider;
import net.minecraft.Optionull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public record QuestsProgress(Map<ResourceLocation, QuestProgress> progress) {

    public List<ResourceLocation> getUncompletedQuests(ServerPlayer player) {
        return List.of(); //TODO implement
    }

    public <T> void testAndProgressTaskType(ServerPlayer player, T input, Class<? extends QuestTask<T, ?>> taskType) {
        List<ResourceLocation> editedQuests = new ArrayList<>();
        List<ResourceLocation> uncompletedQuests = getUncompletedQuests(player);
        for (ResourceLocation id : uncompletedQuests) {
            QuestProgress questProgress = progress.get(id);
            Quest quest = QuestManager.INSTANCE.get(id);
            for (QuestTask<?, ?> task : quest.tasks()) {
                if (task.getClass().equals(taskType)) {
                    TaskProgress progress = questProgress.getTask(task.id());
                    if (progress.isComplete()) continue;
                    progress.addProgress(taskType.cast(task), input);
                    editedQuests.add(id);
                }
            }
            questProgress.update(quest);
        }

        TeamProvider.getAllTeams(player)
            .flatMap(List::stream)
            .distinct()
            .forEach(member -> {
                QuestsProgress memberProgress = QuestProgressHandler.getProgress(player.server, member);
                for (ResourceLocation quest : editedQuests) {
                    var currentProgress = memberProgress.progress().get(quest);
                    var questProgress = progress.get(quest);
                    var newTasks = copyTasks(questProgress.tasks());
                    memberProgress.progress.put(quest, new QuestProgress(questProgress.isComplete(), currentProgress != null && currentProgress.isClaimed(), newTasks));
                }
            });
    }

    private Map<String, TaskProgress> copyTasks(Map<String, TaskProgress> tasks) {
        Map<String, TaskProgress> newTasks = Maps.newHashMapWithExpectedSize(tasks.size());
        for (Map.Entry<String, TaskProgress> entry : tasks.entrySet()) {
            newTasks.put(entry.getKey(), entry.getValue().copy());
        }
        return newTasks;
    }

    public boolean isComplete(ResourceLocation id) {
        return Optionull.mapOrDefault(progress.get(id), QuestProgress::isComplete, false);
    }

    public boolean isClaimed(ResourceLocation id) {
        return Optionull.mapOrDefault(progress.get(id), QuestProgress::isClaimed, true);
    }

    public QuestProgress getProgress(ResourceLocation id) {
        return progress.getOrDefault(id, new QuestProgress());
    }
}

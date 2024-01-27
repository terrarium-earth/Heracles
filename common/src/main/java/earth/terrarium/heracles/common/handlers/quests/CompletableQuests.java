package earth.terrarium.heracles.common.handlers.quests;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.QuestUnlockedPacket;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class CompletableQuests {

    private boolean updated = false;
    private final List<String> quests = new ArrayList<>();

    public List<String> getQuests(QuestsProgress progress) {
        if (!this.updated) {
            this.updateCompleteQuests(progress);
        }
        return this.quests;
    }

    public void updateCompleteQuests(QuestsProgress progress, BiConsumer<String, Quest> onUnlocked) {
        this.updated = true;
        List<String> tempQuests = new ArrayList<>();
        for (var entry : QuestHandler.quests().entrySet()) {
            Quest quest = entry.getValue();
            String id = entry.getKey();
            if (progress.isComplete(id)) continue;
            if (quest.tasks().isEmpty()) continue;
            if (quest.dependencies().isEmpty()) {
                tempQuests.add(id);
                if (!this.quests.contains(id)) {
                    onUnlocked.accept(id, quest);
                }
            } else {
                boolean complete = true;
                for (String dependency : quest.dependencies()) {
                    if (!progress.isComplete(dependency)) {
                        complete = false;
                        break;
                    }
                }
                if (complete) {
                    tempQuests.add(id);
                    if (!this.quests.contains(id)) {
                        onUnlocked.accept(id, quest);
                    }
                }
            }
        }
        this.quests.clear();
        this.quests.addAll(tempQuests);
    }

    public void updateCompleteQuests(QuestsProgress progress) {
        this.updateCompleteQuests(progress, (id, quest) -> {});
    }

    public void updateCompleteQuests(QuestsProgress progress, @Nullable ServerPlayer player) {
        List<UpdatedEntry> updatedQuests = new ArrayList<>();

        this.updateCompleteQuests(progress, (id, quest) -> {
            if (player == null) return;
            if (quest.settings().unlockNotification()) {
                NetworkHandler.CHANNEL.sendToPlayer(new QuestUnlockedPacket(id), player);
            }
            QuestProgress questProgress = progress.getProgress(id);
            if (questProgress.isComplete()) return;
            UpdatedEntry entry = new UpdatedEntry(id, quest, new HashMap<>());
            for (QuestTask<?, ?, ?> value : quest.tasks().values()) {
                Tag newProgress = initTask(value, questProgress, player);
                if (newProgress == null) continue;
                entry.newProgress().put(value.id(), newProgress);
            }
            if (entry.newProgress().isEmpty()) return;
            updatedQuests.add(entry);
        });

        if (player == null) return;
        if (updatedQuests.isEmpty()) return;
        for (UpdatedEntry entry : updatedQuests) {
            QuestProgress questProgress = progress.getProgress(entry.id());
            for (var task : entry.newProgress().entrySet()) {
                progressTask(entry.quest().tasks().get(task.getKey()), task.getValue(), questProgress);
            }
            progress.sendOutQuestChanged(entry.id(), entry.quest(), questProgress, player);
        }
    }

    private static <T extends Tag> void progressTask(QuestTask<?, T, ?> task, Tag newProgress, QuestProgress progress) {
        TaskProgress<T> taskProgress = progress.getTask(task);
        if (taskProgress.isComplete()) return;
        taskProgress.setProgress(ModUtils.cast(newProgress));
        taskProgress.updateComplete(task);
    }

    private static <T extends Tag> T initTask(QuestTask<?, T, ?> task, QuestProgress progress, ServerPlayer player) {
        TaskProgress<T> taskProgress = progress.getTask(task);
        if (taskProgress.isComplete()) return null;
        Tag before = taskProgress.progress().copy();
        T newProgress = task.init(task.type(), taskProgress.progress(), player);
        return task.storage().same(before, newProgress) ? null : newProgress;
    }

    private record UpdatedEntry(String id, Quest quest, Map<String, Tag> newProgress) {}
}

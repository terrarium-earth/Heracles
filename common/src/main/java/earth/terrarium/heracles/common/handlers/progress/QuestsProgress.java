package earth.terrarium.heracles.common.handlers.progress;

import com.google.common.collect.Maps;
import earth.terrarium.heracles.api.events.QuestEvents;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestEntry;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.api.teams.TeamProviders;
import earth.terrarium.heracles.common.handlers.pinned.PinnedQuestHandler;
import earth.terrarium.heracles.common.handlers.quests.CompletableQuests;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.QuestCompletedPacket;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.Optionull;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.*;

public record QuestsProgress(Map<String, QuestProgress> progress, CompletableQuests completableQuests) {

    public QuestsProgress(Map<String, QuestProgress> progress) {
        this(progress, new CompletableQuests());
    }

    public <I, T extends QuestTask<I, ?, T>> void testAndProgressTaskType(ServerPlayer player, I input, QuestTaskType<T> taskType) {
        List<QuestEntry> editedQuests = new ArrayList<>();
        for (String id : this.completableQuests.getQuests(this)) {
            QuestProgress questProgress = getProgress(id);
            Quest quest = QuestHandler.get(id);
            QuestEntry entry = QuestEntry.of(id, quest);
            for (QuestTask<?, ?, ?> task : quest.tasks().values()) {
                if (task.isCompatibleWith(taskType)) {
                    TaskProgress<?> progress = questProgress.getTask(task);
                    Tag before = progress.progress();
                    if (progress.isComplete()) continue;
                    progress.addProgress(taskType, ModUtils.cast(task), input);
                    Tag after = progress.progress();
                    if (task.storage().same(before, after)) continue;
                    editedQuests.add(entry);
                }
            }
            questProgress.update(quest);
            this.progress.put(id, questProgress);
            if (questProgress.isComplete()) {
                sendOutQuestComplete(entry, player);
            }
        }
        if (editedQuests.isEmpty()) return;
        Set<String> updatedQuests = new HashSet<>();
        editedQuests.forEach(pair -> updatedQuests.add(pair.id()));
        PinnedQuestHandler.syncIfChanged(player, updatedQuests);


        this.completableQuests.updateCompleteQuests(this, player);
        syncToTeam(player, editedQuests);
    }

    public <I, T extends QuestTask<I, ?, T>> boolean testAndProgressTask(ServerPlayer player, String id, String task, I input, QuestTaskType<T> taskType) {
        List<String> completableQuests = this.completableQuests.getQuests(this);
        if (!completableQuests.contains(id)) return false;
        QuestProgress questProgress = getProgress(id);
        Quest quest = QuestHandler.get(id);
        if (quest == null) return false;
        QuestTask<?, ?, ?> questTask = quest.tasks().get(task);
        if (questTask == null) return false;
        if (!questTask.isCompatibleWith(taskType)) return false;
        TaskProgress<?> progress = questProgress.getTask(questTask);
        if (progress.isComplete()) return false;
        progress.addProgress(taskType, ModUtils.cast(questTask), input);
        questProgress.update(quest);
        this.progress.put(id, questProgress);

        QuestEntry entry = QuestEntry.of(id, quest);
        if (questProgress.isComplete()) {
            sendOutQuestComplete(entry, player);
        }
        PinnedQuestHandler.syncIfChanged(player, List.of(id));
        this.completableQuests.updateCompleteQuests(this, player);
        syncToTeam(player, List.of(entry));
        return true;
    }

    private void syncToTeam(ServerPlayer player, List<QuestEntry> quests) {
        TeamProviders.getMembers(player)
            .forEach(member -> {
                QuestsProgress memberProgress = QuestProgressHandler.getProgress(player.server, member);
                ServerPlayer serverPlayer = player.server.getPlayerList().getPlayer(member);
                for (var entry : quests) {
                    if (entry.quest().settings().individualProgress()) continue;
                    boolean wasComplete = memberProgress.isComplete(entry.id());
                    var currentProgress = memberProgress.progress().get(entry.id());
                    var questProgress = progress.get(entry.id());
                    var newTasks = copyTasks(questProgress.tasks());
                    memberProgress.progress.put(entry.id(), new QuestProgress(questProgress.isComplete(), Set.copyOf(Optionull.mapOrDefault(currentProgress, QuestProgress::claimedRewards, new HashSet<>())), newTasks));
                    if (serverPlayer != null && (questProgress.isComplete() && !wasComplete)) {
                        sendOutQuestComplete(entry, player);
                    }
                }
                memberProgress.completableQuests.updateCompleteQuests(memberProgress, serverPlayer);
            });
    }

    private static void sendOutQuestComplete(QuestEntry entry, ServerPlayer player) {
        player.level().playSound(null, player.blockPosition(), SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.MASTER, 0.25f, 2f);
        NetworkHandler.CHANNEL.sendToPlayer(new QuestCompletedPacket(entry.id()), player);
        QuestEvents.fireQuestCompleted(entry, player);
    }

    private Map<String, TaskProgress<?>> copyTasks(Map<String, TaskProgress<?>> tasks) {
        Map<String, TaskProgress<?>> newTasks = Maps.newHashMapWithExpectedSize(tasks.size());
        for (Map.Entry<String, TaskProgress<?>> entry : tasks.entrySet()) {
            newTasks.put(entry.getKey(), entry.getValue().copy());
        }
        return newTasks;
    }

    public boolean isComplete(String id) {
        return Optionull.mapOrDefault(progress.get(id), QuestProgress::isComplete, false);
    }

    public boolean isClaimed(String id, Quest quest) {
        QuestProgress progress = this.progress.get(id);
        if (progress == null) return true;
        return progress.claimedRewards().size() >= quest.rewards().size();
    }

    public QuestProgress getProgress(String id) {
        return progress.getOrDefault(id, new QuestProgress());
    }
}

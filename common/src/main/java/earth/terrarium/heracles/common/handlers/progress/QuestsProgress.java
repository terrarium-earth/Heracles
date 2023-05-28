package earth.terrarium.heracles.common.handlers.progress;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import earth.terrarium.heracles.api.quests.Quest;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.*;

public record QuestsProgress(Map<String, QuestProgress> progress, CompletableQuests completableQuests) {

    public QuestsProgress(Map<String, QuestProgress> progress) {
        this(progress, new CompletableQuests());
    }

    public <I, T extends QuestTask<I, ?, T>> void testAndProgressTaskType(ServerPlayer player, I input, QuestTaskType<T> taskType) {
        List<Pair<String, Quest>> editedQuests = new ArrayList<>();
        for (String id : this.completableQuests.getQuests(this)) {
            QuestProgress questProgress = getProgress(id);
            Quest quest = QuestHandler.get(id);
            for (QuestTask<?, ?, ?> task : quest.tasks().values()) {
                if (task.isCompatibleWith(taskType)) {
                    TaskProgress<?> progress = questProgress.getTask(task);
                    if (progress.isComplete()) continue;
                    progress.addProgress(taskType, ModUtils.cast(task), input);
                    editedQuests.add(Pair.of(id, quest));
                }
            }
            questProgress.update(quest);
            this.progress.put(id, questProgress);
            if (questProgress.isComplete()) {
                sendOutQuestComplete(player, quest);
            }
        }
        if (editedQuests.isEmpty()) return;
        Set<String> updatedQuests = new HashSet<>();
        editedQuests.forEach(pair -> updatedQuests.add(pair.getFirst()));
        PinnedQuestHandler.syncIfChanged(player, updatedQuests);


        this.completableQuests.updateCompleteQuests(this);

        TeamProviders.getMembers(player)
            .forEach(member -> {
                QuestsProgress memberProgress = QuestProgressHandler.getProgress(player.server, member);
                for (var quest : editedQuests) {
                    if (quest.getSecond().settings().individualProgress()) continue;
                    var currentProgress = memberProgress.progress().get(quest.getFirst());
                    var questProgress = progress.get(quest.getFirst());
                    var newTasks = copyTasks(questProgress.tasks());
                    memberProgress.progress.put(quest.getFirst(), new QuestProgress(questProgress.isComplete(), Set.copyOf(Optionull.mapOrDefault(currentProgress, QuestProgress::claimedRewards, new HashSet<>())), newTasks));
                    ServerPlayer serverPlayer = player.server.getPlayerList().getPlayer(member);
                    if (serverPlayer != null && questProgress.isComplete()) {
                        Quest questObj = QuestHandler.get(quest.getFirst());
                        sendOutQuestComplete(serverPlayer, questObj);
                    }
                }
                memberProgress.completableQuests.updateCompleteQuests(memberProgress);
            });
    }

    private void sendOutQuestComplete(ServerPlayer player, Quest quest) {
        player.level().playSound(null,
            player.blockPosition(),
            SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
            SoundSource.MASTER, 0.25f, 2f);
        NetworkHandler.CHANNEL.sendToPlayer(new QuestCompletedPacket(quest), player);
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

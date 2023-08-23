package earth.terrarium.heracles.common.handlers.quests;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.QuestUnlockedPacket;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
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
        this.updateCompleteQuests(progress, (id, quest) -> {
            if (quest.settings().unlockNotification() && player != null) {
                NetworkHandler.CHANNEL.sendToPlayer(new QuestUnlockedPacket(id), player);
            }
        });
    }
}

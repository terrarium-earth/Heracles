package earth.terrarium.heracles.common.handlers;

import earth.terrarium.heracles.api.Quest;

import java.util.ArrayList;
import java.util.List;

public class CompletableQuests {

    private boolean updated = false;
    private final List<String> quests = new ArrayList<>();

    public List<String> getQuests(QuestsProgress progress) {
        if (!this.updated) {
            this.updateCompleteQuests(progress);
        }
        return this.quests;
    }

    public void updateCompleteQuests(QuestsProgress progress) {
        this.updated = true;
        this.quests.clear();
        for (var entry : QuestHandler.quests().entrySet()) {
            Quest quest = entry.getValue();
            String id = entry.getKey();
            if (quest.dependencies().isEmpty()) {
                this.quests.add(id);
            } else {
                boolean complete = true;
                for (String dependency : quest.dependencies()) {
                    if (!progress.isComplete(dependency)) {
                        complete = false;
                        break;
                    }
                }
                if (complete) {
                    this.quests.add(id);
                }
            }
        }
    }
}

package earth.terrarium.heracles.api;

import earth.terrarium.heracles.api.quests.QuestIcon;

public interface CustomizableQuestElement {
    String title();
    QuestIcon<?> icon();
}

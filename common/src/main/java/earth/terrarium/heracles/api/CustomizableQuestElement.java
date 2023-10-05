package earth.terrarium.heracles.api;

import earth.terrarium.heracles.api.quests.QuestIcon;
import net.minecraft.network.chat.Component;

public interface CustomizableQuestElement {
    String title();
    QuestIcon<?> icon();
    default Component titleOr(Component overridden) {
        return !title().isEmpty() ? Component.translatable(title()) : overridden;
    }
}

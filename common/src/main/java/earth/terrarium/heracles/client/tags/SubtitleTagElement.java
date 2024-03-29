package earth.terrarium.heracles.client.tags;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestDisplay;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.hermes.api.defaults.TextTagElement;
import net.minecraft.network.chat.CommonComponents;

import java.util.Map;

public class SubtitleTagElement extends TextTagElement {

    public SubtitleTagElement(Map<String, String> parameters) {
        super(parameters);
        String id = parameters.get("id");
        this.component.append(ClientQuests.get(id)
            .map(ClientQuests.QuestEntry::value)
            .map(Quest::display)
            .map(QuestDisplay::subtitle)
            .orElse(CommonComponents.EMPTY));
    }
}

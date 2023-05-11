package earth.terrarium.heracles.client.tags;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestDisplay;
import earth.terrarium.heracles.client.ClientQuests;
import earth.terrarium.hermes.api.defaults.ComponentTagElement;
import net.minecraft.network.chat.CommonComponents;

import java.util.Map;

public class SubtitleTagElement extends ComponentTagElement {

    public SubtitleTagElement(Map<String, String> parameters) {
        super(parameters);
        String id = parameters.get("id");
        this.text = ClientQuests.get(id)
            .map(ClientQuests.QuestEntry::value)
            .map(Quest::display)
            .flatMap(QuestDisplay::subtitle)
            .orElse(CommonComponents.EMPTY);
    }
}

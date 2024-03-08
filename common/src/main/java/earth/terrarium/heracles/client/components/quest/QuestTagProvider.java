package earth.terrarium.heracles.client.components.quest;

import earth.terrarium.heracles.api.client.DescriptionTags;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.tags.SubtitleTagElement;
import earth.terrarium.heracles.client.tags.WidgetTagElement;
import earth.terrarium.hermes.api.DefaultTagProvider;

public class QuestTagProvider extends DefaultTagProvider {

    public QuestTagProvider(Quest quest, String id) {
        super();
        addSerializer("subtitle", SubtitleTagElement::new);
        addSerializer("task", parameters -> WidgetTagElement.ofTask(quest, id, parameters));
        addSerializer("reward", parameters -> WidgetTagElement.ofReward(quest, parameters));
        DescriptionTags.tags().forEach((tag, serializer) -> addSerializer(tag, serializer.create(quest, id)));
    }
}

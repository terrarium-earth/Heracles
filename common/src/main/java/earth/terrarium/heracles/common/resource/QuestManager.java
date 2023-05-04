package earth.terrarium.heracles.common.resource;

import earth.terrarium.heracles.api.Quest;
import net.minecraft.resources.ResourceLocation;

public class QuestManager extends CodecResourceReloadListener<Quest> {
    public static final QuestManager INSTANCE = new QuestManager();

    public QuestManager() {
        super(Quest.CODEC, "quests/quest");
    }

    public Quest get(ResourceLocation id) {
        return getValues().get(id);
    }

    public ResourceLocation getKey(Quest quest) {
        return getValues().inverse().get(quest);
    }
}

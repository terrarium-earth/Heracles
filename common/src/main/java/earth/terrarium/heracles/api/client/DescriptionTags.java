package earth.terrarium.heracles.api.client;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.hermes.api.TagElementSerializer;

import java.util.HashMap;
import java.util.Map;

public class DescriptionTags {

    private static final Map<String, Factory> TAGS = new HashMap<>();

    public static void register(String tag, TagElementSerializer serializer) {
        register(tag, (quest, id) -> serializer);
    }

    public static void register(String tag, Factory factory) {
        if (get(tag) != null) {
            throw new IllegalArgumentException("Tag already registered: " + tag);
        }
        TAGS.put(tag, factory);
    }

    public static Factory get(String tag) {
        return TAGS.get(tag);
    }

    public static Map<String, Factory> tags() {
        return TAGS;
    }

    @FunctionalInterface
    public interface Factory {

        TagElementSerializer create(Quest quest, String id);
    }
}

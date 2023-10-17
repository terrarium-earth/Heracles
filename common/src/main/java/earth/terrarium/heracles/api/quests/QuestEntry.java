package earth.terrarium.heracles.api.quests;

/**
 * A {@link Quest} and associated ID.
 */
public record QuestEntry(String id, Quest quest) {
    public static QuestEntry of(String first, Quest second) {
        return new QuestEntry(first, second);
    }
}

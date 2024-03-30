package earth.terrarium.heracles.api.tasks;

import earth.terrarium.heracles.api.quests.Quest;

import java.util.Collection;

public interface CacheableQuestTaskType<T extends QuestTask<?, ?, T>, C> extends QuestTaskType<T> {

    C cache(Collection<Quest> quests);
}

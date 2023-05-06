package earth.terrarium.heracles.api.rewards.client;

import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.rewards.QuestReward;

public interface QuestRewardWidgetFactory<T extends QuestReward<T>> {

    DisplayWidget create(T task);

    default DisplayWidget createAndCast(QuestReward<?> task) {
        return create(this.cast(task));
    }

    @SuppressWarnings("unchecked")
    default T cast(QuestReward<?> task) {
        return (T) task;
    }
}

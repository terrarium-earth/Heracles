package earth.terrarium.heracles.client.components.lists;

import earth.terrarium.heracles.api.client.DisplayWidget;

public interface ListEntry<T> extends DisplayWidget {

    T value();

    /**
     * if the entry requires a list to be used this will update the list when needed.
     */
    default void setList(QuestList<T> list) {
    }
}

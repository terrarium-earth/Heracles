package earth.terrarium.heracles.api.tasks.storage;

import net.minecraft.nbt.Tag;

import java.util.Objects;

public interface TaskStorage<T, S extends Tag> {

    /**
     * Returns the default progress tag for this task.
     * This is used to initialize the progress of a task.
     *
     * @return The default progress tag.
     */
    S createDefault();

    /**
     * Reads the progress tag and returns the progress value.
     *
     * @param tag The progress tag.
     * @return The progress value.
     */
    T read(S tag);

    /**
     * Compares two progress tags and returns whether they are the same.
     * Default implementation uses {@link Objects#equals(Object, Object)}.
     *
     * @param tag1 The first progress tag.
     * @param tag2 The second progress tag.
     * @return Whether the two progress tags are the same.
     */
    default boolean same(Tag tag1, Tag tag2) {
        return Objects.equals(tag1, tag2);
    }
}

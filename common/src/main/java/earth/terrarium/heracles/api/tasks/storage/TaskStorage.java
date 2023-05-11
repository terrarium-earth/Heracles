package earth.terrarium.heracles.api.tasks.storage;

import net.minecraft.nbt.Tag;

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
}

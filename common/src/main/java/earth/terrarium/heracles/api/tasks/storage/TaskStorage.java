package earth.terrarium.heracles.api.tasks.storage;

import net.minecraft.nbt.Tag;

public interface TaskStorage<T> {

    Tag createDefault();

    T read(Tag tag);
}

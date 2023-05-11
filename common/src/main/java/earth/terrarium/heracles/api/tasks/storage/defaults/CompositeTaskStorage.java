package earth.terrarium.heracles.api.tasks.storage.defaults;

import earth.terrarium.heracles.api.tasks.storage.TaskStorage;
import net.minecraft.nbt.ListTag;

import java.util.List;

public record CompositeTaskStorage(
    List<? extends TaskStorage<?, ?>> taskStorages) implements TaskStorage<ListTag, ListTag> {
    @Override
    public ListTag createDefault() {
        ListTag tags = new ListTag();

        for (var storage : taskStorages()) {
            tags.add(storage.createDefault());
        }

        return tags;
    }

    @Override
    public ListTag read(ListTag tag) {
        return tag;
    }
}

package earth.terrarium.heracles.api.tasks.storage.defaults;

import earth.terrarium.heracles.api.tasks.storage.TaskStorage;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.List;

public record CompositeTaskStorage(List<? extends TaskStorage<?>> taskStorages) implements TaskStorage<ListTag> {
    @Override
    public Tag createDefault() {
        ListTag tags = new ListTag();

        for (var storage : taskStorages()) {
            tags.add(storage.createDefault());
        }

        return tags;
    }

    @Override
    public ListTag read(Tag tag) {
        return (ListTag) tag;
    }
}

package earth.terrarium.heracles.api.tasks.storage.defaults;

import earth.terrarium.heracles.api.tasks.storage.TaskStorage;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.List;

public record CompositeTaskStorage(
    List<? extends TaskStorage<?, ?>> taskStorages) implements TaskStorage<CollectionTag<Tag>, CollectionTag<Tag>> {
    @Override
    public CollectionTag<Tag> createDefault() {
        CollectionTag<Tag> tags = new ListTag();

        for (var storage : taskStorages()) {
            tags.add(storage.createDefault());
        }

        return tags;
    }

    @Override
    public CollectionTag<Tag> read(CollectionTag<Tag> tag) {
        return tag;
    }
}

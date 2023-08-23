package earth.terrarium.heracles.api.tasks.storage.defaults;

import earth.terrarium.heracles.api.tasks.storage.TaskStorage;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.List;

public record CompositeTaskStorage(
    List<? extends TaskStorage<?, ?>> taskStorages
) implements TaskStorage<CollectionTag<Tag>, CollectionTag<Tag>> {
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

    @Override
    public boolean same(Tag tag1, Tag tag2) {
        //noinspection rawtypes
        if (!(tag1 instanceof CollectionTag listTag1) || !(tag2 instanceof CollectionTag listTag2)) {
            return false;
        }
        if (listTag1.size() != listTag2.size()) {
            return false;
        }

        for (int i = 0; i < listTag1.size(); i++) {
            if (!taskStorages.get(i).same((Tag) listTag1.get(i), (Tag) listTag2.get(i))) {
                return false;
            }
        }

        return true;
    }
}

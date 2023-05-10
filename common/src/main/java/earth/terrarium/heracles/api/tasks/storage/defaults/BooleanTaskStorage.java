package earth.terrarium.heracles.api.tasks.storage.defaults;

import earth.terrarium.heracles.api.tasks.storage.TaskStorage;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;

public final class BooleanTaskStorage implements TaskStorage<Boolean> {

    public static final BooleanTaskStorage INSTANCE = new BooleanTaskStorage();

    private BooleanTaskStorage() {
    }

    @Override
    public Tag createDefault() {
        return ByteTag.valueOf(false);
    }

    @Override
    public Boolean read(Tag tag) {
        return readBoolean(tag);
    }

    public boolean readBoolean(Tag tag) {
        return tag instanceof NumericTag numericTag && numericTag.getAsByte() == 1;
    }

    public Tag of(Tag tag, boolean value) {
        return ByteTag.valueOf(readBoolean(tag) || value);
    }

    public Tag of(boolean value) {
        return ByteTag.valueOf(value);
    }
}

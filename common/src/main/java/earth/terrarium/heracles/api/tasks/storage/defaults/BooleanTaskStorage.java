package earth.terrarium.heracles.api.tasks.storage.defaults;

import earth.terrarium.heracles.api.tasks.storage.TaskStorage;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;

public final class BooleanTaskStorage implements TaskStorage<Boolean, ByteTag> {

    public static final BooleanTaskStorage INSTANCE = new BooleanTaskStorage();

    private BooleanTaskStorage() {
    }

    @Override
    public ByteTag createDefault() {
        return ByteTag.valueOf(false);
    }

    @Override
    public Boolean read(ByteTag tag) {
        return readBoolean(tag);
    }

    public boolean readBoolean(Tag tag) {
        return tag instanceof NumericTag numericTag && numericTag.getAsByte() == 1;
    }

    public ByteTag of(Tag tag, boolean value) {
        return ByteTag.valueOf(readBoolean(tag) || value);
    }

    public ByteTag of(boolean value) {
        return ByteTag.valueOf(value);
    }
}

package earth.terrarium.heracles.api.tasks.storage.defaults;

import earth.terrarium.heracles.api.tasks.storage.TaskStorage;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;

public final class IntegerTaskStorage implements TaskStorage<Integer> {

    public static final IntegerTaskStorage INSTANCE = new IntegerTaskStorage();

    private IntegerTaskStorage() {
    }

    @Override
    public Tag createDefault() {
        return IntTag.valueOf(0);
    }

    @Override
    public Integer read(Tag tag) {
        return readInt(tag);
    }

    public int readInt(Tag tag) {
        return tag instanceof NumericTag numericTag ? numericTag.getAsInt() : 0;
    }

    public Tag of(Tag progress, int amount) {
        return IntTag.valueOf(readInt(progress) + amount);
    }
}

package earth.terrarium.heracles.api.tasks.storage.defaults;

import earth.terrarium.heracles.api.tasks.storage.TaskStorage;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;

public final class IntegerTaskStorage implements TaskStorage<Integer, NumericTag> {

    public static final IntegerTaskStorage INSTANCE = new IntegerTaskStorage();

    private IntegerTaskStorage() {
    }

    @Override
    public NumericTag createDefault() {
        return IntTag.valueOf(0);
    }

    @Override
    public Integer read(NumericTag tag) {
        return readInt(tag);
    }

    @Override
    public boolean same(Tag tag1, Tag tag2) {
        return readInt(tag1) == readInt(tag2);
    }

    public int readInt(Tag tag) {
        return tag instanceof NumericTag numericTag ? numericTag.getAsInt() : 0;
    }

    public IntTag add(NumericTag progress, int amount) {
        return IntTag.valueOf(readInt(progress) + amount);
    }

    public IntTag set(int amount) {
        return IntTag.valueOf(amount);
    }
}

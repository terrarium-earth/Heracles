package earth.terrarium.heracles.api.tasks.storage.defaults;

import earth.terrarium.heracles.api.tasks.storage.TaskStorage;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NumericTag;

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

    public int readInt(NumericTag tag) {
        return tag.getAsInt();
    }

    public IntTag add(NumericTag progress, int amount) {
        return IntTag.valueOf(readInt(progress) + amount);
    }

    public IntTag set(int amount) {
        return IntTag.valueOf(amount);
    }
}

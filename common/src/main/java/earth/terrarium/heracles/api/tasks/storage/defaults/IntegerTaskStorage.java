package earth.terrarium.heracles.api.tasks.storage.defaults;

import earth.terrarium.heracles.api.tasks.storage.TaskStorage;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;

public final class IntegerTaskStorage implements TaskStorage<Integer, IntTag> {

    public static final IntegerTaskStorage INSTANCE = new IntegerTaskStorage();

    private IntegerTaskStorage() {
    }

    @Override
    public IntTag createDefault() {
        return IntTag.valueOf(0);
    }

    @Override
    public Integer read(IntTag tag) {
        return readInt(tag);
    }

    public int readInt(IntTag tag) {
        return tag.getAsInt();
    }

    public IntTag of(IntTag progress, int amount) {
        return IntTag.valueOf(readInt(progress) + amount);
    }
}

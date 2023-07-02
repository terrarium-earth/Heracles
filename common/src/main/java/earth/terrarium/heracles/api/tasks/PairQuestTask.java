package earth.terrarium.heracles.api.tasks;

import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.Tag;

public interface PairQuestTask<I1, I2, S extends Tag, T extends PairQuestTask<I1, I2, S, T>> extends QuestTask<Pair<I1, I2>, S, T> {

    @Override
    default S test(QuestTaskType<?> type, S progress, Pair<I1, I2> input) {
        return test(type, progress, input.getFirst(), input.getSecond());
    }

    S test(QuestTaskType<?> type, S progress, I1 input1, I2 input2);
}

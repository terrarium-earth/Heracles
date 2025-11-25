package earth.terrarium.heracles.api.tasks;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public interface QuestTaskType<T extends QuestTask<?, ?, T>> {

    ResourceLocation id();

    MapCodec<T> codec(String id);
}

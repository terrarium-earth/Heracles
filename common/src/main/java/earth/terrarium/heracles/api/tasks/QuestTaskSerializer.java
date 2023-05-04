package earth.terrarium.heracles.api.tasks;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public interface QuestTaskSerializer<T extends QuestTask<?, T>> {

    ResourceLocation id();

    Codec<T> codec();
}

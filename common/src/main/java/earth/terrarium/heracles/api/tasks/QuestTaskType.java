package earth.terrarium.heracles.api.tasks;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public interface QuestTaskType<T extends QuestTask<?, ?, T>> {

    ResourceLocation id();

    Codec<T> codec();
}

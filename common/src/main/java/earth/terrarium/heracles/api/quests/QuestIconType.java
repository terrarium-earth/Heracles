package earth.terrarium.heracles.api.quests;

import com.mojang.serialization.MapCodec;
import net.minecraft.resources.ResourceLocation;

public interface QuestIconType<T extends QuestIcon<T>> {

    ResourceLocation id();

    MapCodec<T> codec();
}

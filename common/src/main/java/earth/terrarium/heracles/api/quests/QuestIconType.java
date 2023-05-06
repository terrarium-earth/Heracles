package earth.terrarium.heracles.api.quests;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

public interface QuestIconType<T extends QuestIcon<T>> {

    ResourceLocation id();

    Codec<T> codec();
}

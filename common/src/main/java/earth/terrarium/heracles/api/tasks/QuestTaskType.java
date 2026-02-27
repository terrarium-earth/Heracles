package earth.terrarium.heracles.api.tasks;

import com.mojang.serialization.Codec;
import earth.terrarium.heracles.api.client.settings.SettingsProvider;
import net.minecraft.resources.ResourceLocation;

public interface QuestTaskType<T extends QuestTask<?, ?, T>> extends SettingsProvider<T> {

    ResourceLocation id();

    Codec<T> codec(String id);
}

package earth.terrarium.heracles.resource;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import earth.terrarium.heracles.Quest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class CodecResourceReloadListener<T> extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Logger LOGGER = LogManager.getLogger();

    private final BiMap<ResourceLocation, T> values = HashBiMap.create();
    private final Codec<T> codec;
    private final String key;

    public CodecResourceReloadListener(Codec<T> codec, String key) {
        super(GSON, key);
        this.codec = codec;
        this.key = key;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        values.clear();

        for (Map.Entry<ResourceLocation, JsonElement> entry : object.entrySet()) {
            ResourceLocation id = new ResourceLocation(entry.getKey().getNamespace(), key + '/' + entry.getKey().getPath());
            DataResult<T> result = codec.parse(JsonOps.INSTANCE, entry.getValue());

            var partialResult = result.get().right();
            if (partialResult.isPresent()) {
                LOGGER.error("Failed to load object " + id + " from codec", new JsonSyntaxException(partialResult.get().message()));
            } else {
                values.put(entry.getKey(), result.get().left().orElseThrow());
            }
        }
    }

    protected BiMap<ResourceLocation, T> getValues() {
        return values;
    }
}

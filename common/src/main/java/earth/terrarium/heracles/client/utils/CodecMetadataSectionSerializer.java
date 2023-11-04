package earth.terrarium.heracles.client.utils;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import org.jetbrains.annotations.NotNull;

public record CodecMetadataSectionSerializer<T>(Codec<T> codec, ResourceLocation id) implements MetadataSectionSerializer<T> {

    @Override
    public @NotNull String getMetadataSectionName() {
        return id.toString();
    }

    @Override
    public @NotNull T fromJson(JsonObject json) {
        DataResult<T> result = codec.parse(JsonOps.INSTANCE, json);
        if (result.error().isPresent()) {
            throw new IllegalStateException("Failed to parse " + id + " metadata section: " + result.error().get());
        }
        if (result.result().isEmpty()) {
            throw new IllegalStateException("Failed to parse " + id + " metadata section: Empty result");
        }
        return result.result().get();
    }
}

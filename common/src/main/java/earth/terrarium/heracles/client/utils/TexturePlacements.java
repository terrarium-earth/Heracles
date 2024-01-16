package earth.terrarium.heracles.client.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.client.utils.CodecMetadataSectionSerializer;
import com.teamresourceful.resourcefullib.common.caches.CacheableFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.Resource;

import java.util.Optional;

public class TexturePlacements {

    private static final CacheableFunction<ResourceLocation, Info> INFO = new CacheableFunction<>(TexturePlacements::find);
    public static final Info NO_OFFSET_24X = new Info(0, 0, 24, 24);

    public static Info get(ResourceLocation location) {
        return INFO.apply(location);
    }

    public static Info getOrDefault(ResourceLocation location, Info defaultInfo) {
        Info info = INFO.apply(location);
        return info == null ? defaultInfo : info;
    }

    private static Info find(ResourceLocation location) {
        return Minecraft.getInstance().getResourceManager()
            .getResource(location)
            .flatMap(resource -> getOrEmpty(resource, Resource::metadata))
            .flatMap(meta -> getOrEmpty(meta, m -> m.getSection(Info.METADATA).orElseThrow()))
            .orElse(null);
    }

    private static <I, O> Optional<O> getOrEmpty(I input, ThrowingFunction<I, O> function) {
        try {
            return Optional.of(function.apply(input));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public record Info(int xOffset, int yOffset, int width, int height) {

        public static final Codec<Info> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("xOffset").forGetter(Info::xOffset),
            Codec.INT.fieldOf("yOffset").forGetter(Info::yOffset),
            Codec.INT.fieldOf("width").forGetter(Info::width),
            Codec.INT.fieldOf("height").forGetter(Info::height)
        ).apply(instance, Info::new));

        public static final MetadataSectionSerializer<Info> METADATA = new CodecMetadataSectionSerializer<>(CODEC, new ResourceLocation("heracles", "texture_placement"));
    }

    public interface ThrowingFunction<I, O> {
        O apply(I input) throws Exception;
    }
}

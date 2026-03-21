package earth.terrarium.olympus.client.images;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ImageProviders {

    private static final List<ImageProvider<?>> PROVIDERS = new ArrayList<>();
    private static long ticks = 0;

    /**
     * Registers a new image provider, which can be used to load images from various sources.
     * @param name the name of the provider, used for identification and caching
     * @param factory the factory that creates the image from the provided key, returning null if the key can't be used to create an image.
     *                A completable future that returns exceptionally is also considered as a failure to create an image.
     * @param hasher the hasher that generates a unique hash for the image based on the key, used for caching purposes and location generation.
     * @param cacheTimeout the duration after which the cached image will be considered stale and removed from the cache. Use null to disable caching.
     * @return the registered image provider, which can be used to retrieve images by their keys.
     * @param <T> the type of the key used to retrieve images from this provider
     */
    public static <T> ImageProvider<T> register(
            @NotNull String name,
            @NotNull ImageProvider.Factory<T> factory,
            @NotNull ImageProvider.Hasher<T> hasher,
            @Nullable Duration cacheTimeout
    ) {
        ImageProvider<T> provider = new ImageProvider<>(name, factory, hasher, cacheTimeout == null ? -1 : cacheTimeout.toMillis());
        PROVIDERS.add(provider);
        return provider;
    }

    @ApiStatus.Internal
    public static void tick() {
        ticks++;
        if (ticks % 100 == 0) { // 5 seconds
            for (ImageProvider<?> provider : PROVIDERS) {
                provider.checkCaches();
            }
        }
    }
}

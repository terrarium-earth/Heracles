package earth.terrarium.heracles.client.data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.theme.Theme;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.Optional;

public class ThemeHandler extends SimplePreparableReloadListener<Optional<Theme>> {
    public static final ThemeHandler INSTANCE = new ThemeHandler();

    private static final ResourceLocation THEME_LOCATION = new ResourceLocation(Heracles.MOD_ID, "theme.json");
    private static final Gson GSON = new Gson();

    @Override
    protected @NotNull Optional<Theme> prepare(ResourceManager manager, ProfilerFiller profiler) {
        return manager.getResource(THEME_LOCATION)
            .flatMap(resource -> {
                try {
                    Reader reader = resource.openAsReader();
                    return Optional.of(GsonHelper.fromJson(GSON, reader, JsonObject.class));
                } catch (Exception e) {
                    Heracles.LOGGER.error("Failed to load theme.", e);
                    return Optional.empty();
                }
            })
            .flatMap(json -> Theme.CODEC.parse(JsonOps.INSTANCE, json)
                .get()
                .ifRight(result -> Heracles.LOGGER.error("Failed to parse theme: {}", result.message()))
                .left()
            );
    }

    @Override
    protected void apply(Optional<Theme> theme, ResourceManager manager, ProfilerFiller profiler) {
        Theme.setInstance(theme.orElse(Theme.DEFAULT));
    }
}

package earth.terrarium.heracles.client.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.utils.ThemeColors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;

import java.lang.reflect.Field;
import java.util.HexFormat;
import java.util.Map;

public class ColorHandler extends SimpleJsonResourceReloadListener {
    public static ColorHandler INSTANCE = new ColorHandler(new Gson());

    public ColorHandler(Gson gson) {
        super(gson, "colors");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsonResources, ResourceManager resourceManager, ProfilerFiller profiler) {
        for (Map.Entry<ResourceLocation, JsonElement> entry : jsonResources.entrySet()) {
            ResourceLocation jsonLocation = entry.getKey();
            JsonElement jsonElement = entry.getValue();
            try {
                if (jsonLocation.getNamespace().equals(Heracles.MOD_ID) && jsonLocation.getPath().equals(Heracles.MOD_ID)) {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    for (Field field : ThemeColors.class.getDeclaredFields()) {
                        String fieldName = field.getName();
                        if (jsonObject.has(fieldName) && jsonObject.get(fieldName).isJsonPrimitive()) {
                            int jsonInt = HexFormat.fromHexDigits(jsonObject.get(fieldName).getAsString().replace("0x", ""));
                            field.setInt(null, jsonInt);
                        }
                    }
                }
            } catch (IllegalAccessException | UnsupportedOperationException | IllegalStateException e) {
                Heracles.LOGGER.error("[Heracles Client] Failed to load theme colors.", e);
            }
        }
    }
}

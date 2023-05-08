package earth.terrarium.heracles.client;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.function.Predicate;

public class ClientUtils {

    public static Collection<ResourceLocation> getTextures(Predicate<ResourceLocation> predicate) {
        var textures = Minecraft.getInstance().getResourceManager()
            .listResources("textures/gui/quest_backgrounds", location -> location.getPath().endsWith(".png"));
        return textures.keySet();
    }
}

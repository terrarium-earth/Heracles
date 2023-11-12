package earth.terrarium.heracles.mixins.fabric;

import com.google.gson.Gson;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ColorHandler;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ColorHandler.class)
public abstract class ColorHandlerMixin extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {
    public ColorHandlerMixin(Gson gson, String string) {
        super(gson, string);
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(Heracles.MOD_ID, "colors");
    }
}

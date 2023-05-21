package earth.terrarium.heracles.client.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.Collection;
import java.util.List;

public class ClientUtils {

    public static Collection<ResourceLocation> getTextures(String path) {
        var textures = Minecraft.getInstance().getResourceManager()
            .listResources("textures/" + path, location -> location.getPath().endsWith(".png"));
        return textures.keySet();
    }

    public static void setTooltip(Component component) {
        if (Minecraft.getInstance().screen != null) {
            Minecraft.getInstance().screen.setTooltipForNextRenderPass(List.of(component.getVisualOrderText()));
        }
    }

    public static void setTooltip(List<Component> component) {
        Screen screen = screen();
        if (screen != null) {
            List<FormattedCharSequence> formattedCharSequences = component.stream()
                .map(c -> Minecraft.getInstance().font.split(c, 10000))
                .flatMap(List::stream)
                .toList();
            screen.setTooltipForNextRenderPass(formattedCharSequences);
        }
    }

    public static void clearTooltip() {
        if (Minecraft.getInstance().screen != null) {
            Minecraft.getInstance().screen.setTooltipForNextRenderPass(List.of());
        }
    }

    public static Screen screen() {
        return Minecraft.getInstance().screen;
    }
}

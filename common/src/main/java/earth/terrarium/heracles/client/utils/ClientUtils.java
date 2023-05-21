package earth.terrarium.heracles.client.utils;

import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
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

    public static MouseClick getMousePos() {
        MouseHandler mouse = Minecraft.getInstance().mouseHandler;
        Window window = Minecraft.getInstance().getWindow();
        double mouseX = mouse.xpos() * (double) window.getGuiScaledWidth() / (double) window.getScreenWidth();
        double mouseY = mouse.ypos() * (double) window.getGuiScaledHeight() / (double) window.getScreenHeight();
        return new MouseClick((int) mouseX, (int) mouseY, -1);
    }
}

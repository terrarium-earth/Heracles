package earth.terrarium.heracles.client.utils;

import com.mojang.blaze3d.platform.Window;
import earth.terrarium.heracles.api.client.WidgetUtils;
import it.unimi.dsi.fastutil.chars.Char2CharMap;
import it.unimi.dsi.fastutil.chars.Char2CharOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ClientUtils {

    public static Collection<ResourceLocation> getTextures(String path) {
        var textures = Minecraft.getInstance().getResourceManager()
            .listResources("textures/" + path, location -> location.getPath().endsWith(".png"));
        return textures.keySet();
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

    private static final Char2CharMap SMALL_NUMBERS = new Char2CharOpenHashMap();

    static {
        SMALL_NUMBERS.put('0', '₀');
        SMALL_NUMBERS.put('1', '₁');
        SMALL_NUMBERS.put('2', '₂');
        SMALL_NUMBERS.put('3', '₃');
        SMALL_NUMBERS.put('4', '₄');
        SMALL_NUMBERS.put('5', '₅');
        SMALL_NUMBERS.put('6', '₆');
        SMALL_NUMBERS.put('7', '₇');
        SMALL_NUMBERS.put('8', '₈');
        SMALL_NUMBERS.put('9', '₉');
    }

    public static String getSmallNumber(int num) {
        String normal = String.valueOf(num);
        StringBuilder builder = new StringBuilder(normal.length());
        for (char c : String.valueOf(num).toCharArray()) {
            builder.append(SMALL_NUMBERS.getOrDefault(c, c));
        }
        return builder.toString();
    }
}

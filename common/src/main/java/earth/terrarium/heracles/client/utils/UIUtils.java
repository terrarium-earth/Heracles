package earth.terrarium.heracles.client.utils;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class UIUtils {

    private static final FormattedText ELLIPSIS = FormattedText.of("...");

    public static void rightAlign(GuiGraphics graphics, Font font, Component text, int x, int y, int color, boolean shadowed) {
        int textWidth = font.width(text);
        int textX = x - textWidth;
        graphics.drawString(font, text, textX, y, color, shadowed);
    }

    public static void blitWithEdge(GuiGraphics graphics, ResourceLocation texture, int x, int y, int width, int height, int size) {
        graphics.blitNineSliced(texture, x, y, width, height, size, 256, 256, 0, 0);
    }

    public static void drawText(GuiGraphics graphics, Font font, List<FormattedCharSequence> lines, int x, int y, int color, boolean shadowed) {
        for (FormattedCharSequence line : lines) {
            graphics.drawString(font, line, x, y, color, shadowed);
            y += font.lineHeight;
        }
    }

    public static List<FormattedCharSequence> splitText(Font font, Component text, int width, int maxLines) {
        int ellipsisWidth = font.width(ELLIPSIS);
        List<FormattedCharSequence> lines = new ArrayList<>();
        List<FormattedText> splitLines = font.getSplitter().splitLines(text, width, Style.EMPTY);

        for (FormattedText line : splitLines) {
            if (lines.size() >= maxLines - 1 && splitLines.size() > maxLines) {
                FormattedText lastLine = font.substrByWidth(line, width - ellipsisWidth);
                lines.add(toCharSequence(FormattedText.composite(lastLine,ELLIPSIS)));
                break;
            } else {
                lines.add(toCharSequence(line));
            }
        }

        return lines;
    }

    private static FormattedCharSequence toCharSequence(FormattedText text) {
        return Language.getInstance().getVisualOrder(text);
    }

    public static int lerp(boolean value, float percent, int one, int two) {
        return value ? Mth.lerpInt(percent, one, two) : one;
    }

    public static void renderScrollingString(GuiGraphics graphics, Component text, int x, int y, int width, int height, int color, boolean shadow) {
        Font font = Minecraft.getInstance().font;
        int fontWidth = font.width(text);
        int textY = (height - 9) / 2 + 1;
        if (fontWidth > width) {
            int overscroll = fontWidth - width;
            double seconds = (double) Util.getMillis() / 1000.0;
            double e = Math.max((double)overscroll * 0.5, 3.0);
            double f = Math.sin((Math.PI / 2) * Math.cos((Math.PI * 2) * seconds / e)) / 2.0 + 0.5;
            double g = Mth.lerp(f, 0.0, overscroll);
            graphics.enableScissor(x, y, width + x, height + y);
            graphics.drawString(font, text, x - (int)g, y + textY, color, shadow);
            graphics.disableScissor();
        } else {
            graphics.drawString(font, text, x, y + textY, color, shadow);
        }
    }

    public static void copyToClipboard(String text) {
        Minecraft.getInstance().keyboardHandler.setClipboard(text);
    }
}

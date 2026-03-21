package earth.terrarium.heracles.client.components.quest.editor;

import earth.terrarium.heracles.client.components.widgets.textbox.editor.TextHighlighter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class MarkdownHighligher implements TextHighlighter {

    public static final MarkdownHighligher INSTANCE = new MarkdownHighligher();
    private static final int IMPORTANT = 0xD4B931;
    private static final int TEXT_COLOR = 0xC9C9D1;
    private static final int ACCENT_COLOR = 0x90A5DB;

    private static final Style IMPORTANT_STYLE = Style.EMPTY.withColor(IMPORTANT);
    private static final Style ACCENT_STYLE = Style.EMPTY.withColor(ACCENT_COLOR);

    @Override
    public Component highlight(String text) {
        if (text.startsWith("> ")) {
            return Component.literal("> ").withStyle(IMPORTANT_STYLE)
                .append(Component.literal(text.substring(2)).withStyle(ACCENT_STYLE));
        } else if (text.startsWith("- ")) {
            return Component.literal("- ").withStyle(IMPORTANT_STYLE)
                .append(Component.literal(text.substring(2)).withStyle(ACCENT_STYLE));
        } else if (text.startsWith("# ")) {
            return Component.literal("# ").withStyle(IMPORTANT_STYLE)
                .append(Component.literal(text.substring(2)).withStyle(ACCENT_STYLE));
        } else if (text.startsWith("## ")) {
            return Component.literal("## ").withStyle(IMPORTANT_STYLE)
                .append(Component.literal(text.substring(3)).withStyle(ACCENT_STYLE));
        } else if (text.equals("---")) {
            return Component.literal("---").withStyle(IMPORTANT_STYLE);
        }
        return Component.literal(text);
    }

    @Override
    public int getTextColor() {
        return TEXT_COLOR;
    }
}

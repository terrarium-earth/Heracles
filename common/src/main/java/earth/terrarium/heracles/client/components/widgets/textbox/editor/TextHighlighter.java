package earth.terrarium.heracles.client.components.widgets.textbox.editor;

import net.minecraft.network.chat.Component;

public interface TextHighlighter {

    Component highlight(String text);

    int getTextColor();

    default int getCursorColor() {
        return getTextColor() | 0xFF000000;
    }
}

package earth.terrarium.heracles.client.components.quest.editor;

import earth.terrarium.heracles.client.components.widgets.textbox.editor.MultiLineTextBox;
import net.minecraft.client.Minecraft;

public class MarkdownTextBox extends MultiLineTextBox {

    public MarkdownTextBox(MarkdownTextBox box, int width, int height) {
        super(box, Minecraft.getInstance().font, width, height, MarkdownHighligher.INSTANCE);
        if (box == null) return;
        this.copy(box);
    }
}

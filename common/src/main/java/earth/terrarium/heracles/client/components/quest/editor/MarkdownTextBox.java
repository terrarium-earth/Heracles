package earth.terrarium.heracles.client.components.quest.editor;

import com.teamresourceful.resourcefullib.client.components.CursorWidget;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import earth.terrarium.heracles.client.components.widgets.textbox.editor.MultiLineEditBox;
import net.minecraft.client.Minecraft;

public class MarkdownTextBox extends MultiLineEditBox implements CursorWidget {

    public MarkdownTextBox(MarkdownTextBox box, int width, int height) {
        super(Minecraft.getInstance().font, 0, 0, width, height, MarkdownHighligher.INSTANCE);
        if (box == null) return;
        this.copy(box);
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return CursorScreen.Cursor.TEXT;
    }
}

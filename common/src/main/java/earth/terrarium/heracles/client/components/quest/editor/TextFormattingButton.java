package earth.terrarium.heracles.client.components.quest.editor;

import com.teamresourceful.resourcefullib.client.components.CursorWidget;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.widgets.WidgetSprites;
import earth.terrarium.heracles.client.components.widgets.textbox.editor.MultilineTextField;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;

public class TextFormattingButton extends Button implements CursorWidget {

    private final WidgetSprites sprites;

    protected TextFormattingButton(String id, Runnable action) {
        super(
            0, 0,
            16, 16,
            Component.translatable("gui.heracles.editor.button." + id),
            b -> action.run(),
            DEFAULT_NARRATION
        );

        this.setTooltip(Tooltip.create(this.getMessage()));

        this.sprites = new WidgetSprites(
            new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/editor/" + id + "/normal.png"),
            new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/editor/" + id + "/hovered.png")
        );
    }

    public static TextFormattingButton of(String id, Runnable action) {
        return new TextFormattingButton(id, action);
    }

    public static TextFormattingButton between(MultilineTextField field, String id, String start, String end) {
        return TextFormattingButton.of(id,
            () -> {
                String text = field.hasSelection() ? field.getSelectedText() : "";
                field.insertText(start + text + end);
            }
        );
    }

    public static TextFormattingButton between(MultilineTextField field, String id, String value) {
        return TextFormattingButton.between(field, id, value, value);
    }

    public static TextFormattingButton startOf(MultilineTextField field, String id, String value) {
        return TextFormattingButton.of(id, () -> appendToStartOfLines(field, value));
    }

    public static TextFormattingButton insertAtNewLine(MultilineTextField field, String id, String value) {
        return TextFormattingButton.of(id, () -> insertAtNewLine(field, value));
    }

    public static void insertAtNewLine(MultilineTextField field, String text) {
        int line = field.getLineAtCursor();
        String value = field.value();
        List<MultilineTextField.StringView> lines = field.lines();
        StringBuilder builder = new StringBuilder();
        if (line >= 0 && line < lines.size()) {
            MultilineTextField.StringView view = lines.get(line);
            builder.append(value, 0, view.endIndex());
            builder.append("\n").append(text);
            builder.append(value, view.endIndex(), value.length());
            field.setValue(builder.toString(), true);
            field.setSelecting(false);
            field.setCursor(view.endIndex() + value.length());
            field.setSelectCursor(field.cursor());
        }
    }

    public static void appendToStartOfLines(MultilineTextField field, String text) {
        int textLength = text.length();
        int min = field.getSelected().beginIndex();
        int max = field.getSelected().endIndex();
        String[] values = field.value().split("\n");

        boolean add = false;
        int count = 0;
        for (String s : values) {
            int newCount = count + s.length();
            if ((count >= min && count <= max) || (newCount >= min && newCount <= max)) {
                if (!s.startsWith(text)) {
                    add = true;
                    break;
                }
            }
            count = newCount + 1;
        }

        List<String> newValue = new ArrayList<>();
        count = 0;

        for (String s : values) {
            int newCount = count + s.length();
            if ((count >= min && count <= max) || (newCount >= min && newCount <= max)) {
                if (s.startsWith(text)) {
                    if (add) {
                        newValue.add(s);
                        continue;
                    }
                    newValue.add(s.substring(textLength));
                } else if (add) {
                    newValue.add(text + s);
                }
            } else {
                newValue.add(s);
            }
            count = newCount + 1;
        }

        int cursor = field.cursor();
        int selection = field.selectCursor();

        boolean changed = !String.join("\n", newValue).equals(field.value());
        if (!changed) return;

        field.setValue(String.join("\n", newValue), true);

        if (cursor == selection) {
            int offset = add ? textLength : -textLength;
            field.setCursor(Mth.clamp(cursor + offset, 0, field.value().length()));
        }
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation sprite = this.sprites.get(this.isHovered(), !this.isActive());
        graphics.blit(sprite, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return !this.isActive() ? CursorScreen.Cursor.DISABLED : CursorScreen.Cursor.POINTER;
    }
}

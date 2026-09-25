package earth.terrarium.heracles.client.components.quest.editor;

import earth.terrarium.heracles.client.components.quest.editor.overlays.LinkModal;
import earth.terrarium.heracles.client.components.quest.editor.overlays.ObjectModal;
import earth.terrarium.heracles.client.components.quest.editor.overlays.color.ColorButton;
import earth.terrarium.heracles.client.components.quest.editor.overlays.color.ColorPickerOverlay;
import earth.terrarium.heracles.client.components.widgets.buttons.SpriteButton;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.components.widgets.textbox.editor.MultiLineTextBox;
import earth.terrarium.heracles.client.components.widgets.textbox.editor.MultilineTextField;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.network.chat.CommonComponents;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class QuestTextEditor {

    private static final AtomicReference<MarkdownTextBox> CURRENT_EDITOR = new AtomicReference<>(null);
    public static final int PADDING = 10;
    public static final int BUTTON_SIZE = 16;

    public static MarkdownTextBox init(ClientQuests.QuestEntry quest, MarkdownTextBox old, GridLayout layout, AtomicInteger row, int width, int height) {
        int buttonsRow = row.getAndIncrement();

        MarkdownTextBox box = layout.addChild(
            new MarkdownTextBox(old, width, height - 20),
            row.getAndIncrement(), 0,
            layout.newCellSettings().padding(QuestTextEditor.PADDING).paddingTop(0)
        );

        CURRENT_EDITOR.set(box);

        layout.addChild(
            createButtons(quest, box),
            buttonsRow, 0,
            layout.newCellSettings().padding(QuestTextEditor.PADDING)
        );

        return box;
    }

    private static GridLayout createButtons(ClientQuests.QuestEntry quest, MultiLineTextBox editor) {
        MultilineTextField field = editor.field();

        int column = 0;

        GridLayout colorPicker = new GridLayout();
        colorPicker.addChild(ColorButton.of(() -> DisplayConfig.editorColor, CommonComponents.EMPTY, () -> {
            String code = "/%s/".formatted(DisplayConfig.editorColor.getChar());
            String text = field.hasSelection() ? field.getSelectedText() : "";
            field.insertText(code + text + code);
        }), 0, 0);
        colorPicker.addChild(SpriteButton.create(12, BUTTON_SIZE, ColorPickerOverlay.SPRITES, ColorPickerOverlay::open), 0, 1);

        GridLayout layout = new GridLayout().columnSpacing(2);
        layout.addChild(TextFormattingButton.startOf(field, "header1", "# "), 0, column++);
        layout.addChild(TextFormattingButton.startOf(field, "header2", "## "), 0, column++);
        layout.addChild(new SpacerElement(8, BUTTON_SIZE), 0, column++);
        layout.addChild(TextFormattingButton.between(field, "bold", "**"), 0, column++);
        layout.addChild(TextFormattingButton.between(field, "italics", "--"), 0, column++);
        layout.addChild(TextFormattingButton.between(field, "underline", "__"), 0, column++);
        layout.addChild(TextFormattingButton.between(field, "strikethrough", "~~"), 0, column++);
        layout.addChild(TextFormattingButton.between(field, "spoiler", "||"), 0, column++);
        layout.addChild(new SpacerElement(8, BUTTON_SIZE), 0, column++);
        layout.addChild(colorPicker, 0, column++);
        layout.addChild(TextFormattingButton.startOf(field, "list", "- "), 0, column++);
        layout.addChild(TextFormattingButton.startOf(field, "blockquote", "> "), 0, column++);
        layout.addChild(new SpacerElement(8, BUTTON_SIZE), 0, column++);
        layout.addChild(TextFormattingButton.of("link", () -> LinkModal.open(QuestTextEditor.CURRENT_EDITOR)), 0, column++);
        layout.addChild(TextFormattingButton.of("reward", () -> ObjectModal.openRewards(quest, QuestTextEditor.CURRENT_EDITOR)), 0, column++);
        layout.addChild(TextFormattingButton.of("task", () -> ObjectModal.openTasks(quest, QuestTextEditor.CURRENT_EDITOR)), 0, column++);
        return layout;
    }
}

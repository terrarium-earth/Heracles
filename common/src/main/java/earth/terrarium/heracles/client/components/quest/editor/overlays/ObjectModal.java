package earth.terrarium.heracles.client.components.quest.editor.overlays;

import earth.terrarium.heracles.client.components.quest.editor.MarkdownTextBox;
import earth.terrarium.heracles.client.components.quest.editor.TextFormattingButton;
import earth.terrarium.heracles.client.components.widgets.buttons.TextButton;
import earth.terrarium.heracles.client.components.widgets.dropdown.Dropdown;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.ui.modals.BaseModal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ObjectModal extends BaseModal {

    private static final int WIDGET_HEIGHT = 24;

    private final Collection<String> keys;
    private final Consumer<String> callback;

    private Dropdown<String> label;

    protected ObjectModal(Collection<String> keys, Component title, Consumer<String> callback, Screen background) {
        super(title, background);

        this.callback = callback;
        this.keys = keys;

        this.minHeight = TITLE_BAR_HEIGHT + INNER_PADDING * 4 + WIDGET_HEIGHT * 3;
        this.minWidth = 150;
        this.ratio = 0f;
    }

    @Override
    protected void init() {
        super.init();

        GridLayout layout = new GridLayout().spacing(INNER_PADDING);

        var button = layout.addChild(
            new TextButton(
                this.modalContentWidth, WIDGET_HEIGHT,
                0xFEFEFE, UIConstants.PRIMARY_BUTTON, Component.literal("Create"),
                b -> {
                    this.onClose();
                    this.callback.accept(this.label.selected());
                }
            ),
            2, 0
        );

        this.label = layout.addChild(
            new Dropdown<>(
                this.label,
                this.modalContentWidth, WIDGET_HEIGHT,
                this.keyMap(),
                null, text -> button.active = text != null
            ),
            0, 0
        );
        button.active = this.label.selected() != null;

        layout.arrangeElements();
        layout.setPosition(this.modalContentLeft, this.modalContentTop);
        layout.visitWidgets(this::addRenderableWidget);
    }

    private Map<String, Component> keyMap() {
        Map<String, Component> map = new HashMap<>();
        for (var id : this.keys) {
            map.put(id, Component.literal(id));
        }
        return map;
    }

    public static void openRewards(ClientQuests.QuestEntry quest, AtomicReference<MarkdownTextBox> box) {
        open(quest.value().rewards().keySet(), Component.literal("Reward Display"), (reward) ->
            TextFormattingButton.insertAtNewLine(
                box.get().field(),
                "<reward reward=\"%s\"/>".formatted(reward)
            )
        );
    }

    public static void openTasks(ClientQuests.QuestEntry quest, AtomicReference<MarkdownTextBox> box) {
        open(quest.value().tasks().keySet(), Component.literal("Task Display"), (task) ->
            TextFormattingButton.insertAtNewLine(
                box.get().field(),
                "<task task=\"%s\"/>".formatted(task)
            )
        );
    }

    public static void open(Collection<String> keys, Component title, Consumer<String> callback) {
        Screen background = Minecraft.getInstance().screen;
        ObjectModal modal = new ObjectModal(keys, title, callback, background);
        Minecraft.getInstance().setScreen(modal);
    }
}

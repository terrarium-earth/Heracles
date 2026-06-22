package earth.terrarium.heracles.client.components.quest.editor.overlays;

import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.heracles.client.components.quest.editor.MarkdownTextBox;
import earth.terrarium.heracles.client.components.quest.editor.TextFormattingButton;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.buttons.Button;
import earth.terrarium.olympus.client.components.dropdown.DropdownState;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.modals.BaseModal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class ObjectModal extends BaseModal {

    private static final int WIDGET_HEIGHT = 24;

    private final Collection<String> keys;
    private final Consumer<String> callback;

    private DropdownState<String> dropdownState;

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

        if (this.dropdownState == null) {
            this.dropdownState = DropdownState.of(null);
        }

        List<String> options = new ArrayList<>(this.keys);

        GridLayout layout = new GridLayout().spacing(INNER_PADDING);

        var button = layout.addChild(Widgets.button()
                .withCallback(() -> {
                    this.onClose();
                    this.callback.accept(this.dropdownState.get());
                })
                .withRenderer(WidgetRenderers.text(Component.literal("Create")).withColor(Color.tryParse("#FEFEFE")))
                .withSize(this.modalContentWidth, WIDGET_HEIGHT)
                .withTexture(UIConstants.PRIMARY_BUTTON),
            2, 0
        );

        Button dropdownButton = layout.addChild(
            Widgets.dropdown(
                this.dropdownState,
                options,
                Component::literal,
                btn -> btn.withSize(this.modalContentWidth, WIDGET_HEIGHT),
                dropdown -> dropdown.withSize(this.modalContentWidth, WIDGET_HEIGHT * 4).withCallback(text -> button.active = text != null)
            ),
            0, 0
        );
        button.active = this.dropdownState.get() != null;

        layout.arrangeElements();
        layout.setPosition(this.modalContentLeft, this.modalContentTop);
        layout.visitWidgets(this::addRenderableWidget);
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

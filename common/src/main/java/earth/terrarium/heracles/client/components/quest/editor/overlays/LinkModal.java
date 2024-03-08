package earth.terrarium.heracles.client.components.quest.editor.overlays;

import earth.terrarium.heracles.client.components.quest.editor.MarkdownTextBox;
import earth.terrarium.heracles.client.components.widgets.buttons.TextButton;
import earth.terrarium.heracles.client.components.widgets.textbox.TextBox;
import earth.terrarium.heracles.client.components.widgets.textbox.ValidatingTextBox;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.ui.modals.BaseModal;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public class LinkModal extends BaseModal {

    private static final int WIDGET_HEIGHT = 24;
    private static final Pattern URL_PATTERN = Pattern.compile("^https?:\\/\\/(?:www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b(?:[-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)$");

    private final String initialLabel;
    private final BiConsumer<String, String> callback;

    private TextBox label;
    private TextBox link;

    protected LinkModal(String initialLabel, BiConsumer<String, String> callback, Screen background) {
        super(Component.literal("Display Link"), background);

        this.initialLabel = initialLabel;
        this.callback = callback;

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
                    this.callback.accept(this.label.getValue(), this.link.getValue());
                    this.onClose();
                }
            ),
            2, 0
        );
        button.active = false;

        this.label = layout.addChild(
            new TextBox(
                this.label, this.initialLabel,
                this.modalContentWidth, WIDGET_HEIGHT,
                Short.MAX_VALUE,
                ModUtils.predicateTrue(), text -> button.active = !text.isEmpty() && isValidLink(this.link.getValue())
            ),
            0, 0
        );
        this.label.setPlaceholder(Component.literal("Label"));

        this.link = layout.addChild(
            new ValidatingTextBox(
                this.link, "",
                this.modalContentWidth, WIDGET_HEIGHT,
                text -> {
                    boolean valid = isValidLink(text);
                    button.active = valid && !this.label.getValue().isEmpty();
                    return valid;
                }
            ),
            1, 0
        );
        this.link.setPlaceholder(Component.literal("Link"));

        layout.arrangeElements();
        layout.setPosition(this.modalContentLeft, this.modalContentTop);
        layout.visitWidgets(this::addRenderableWidget);
    }

    private boolean isValidLink(String link) {
        return URL_PATTERN.matcher(link).matches();
    }

    public static void open(AtomicReference<MarkdownTextBox> box) {
        open(box.get().field().hasSelection() ? box.get().field().getSelectedText() : "", (label, link) ->
            box.get().field().insertText("[" + label + "](" + link + ")")
        );
    }

    public static void open(String initialLabel, BiConsumer<String, String> callback) {
        Screen background = Minecraft.getInstance().screen;
        LinkModal modal = new LinkModal(initialLabel, callback, background);
        Minecraft.getInstance().setScreen(modal);
    }
}

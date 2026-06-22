package earth.terrarium.heracles.client.components.quest.editor.overlays;

import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.heracles.client.components.quest.editor.MarkdownTextBox;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.components.textbox.TextBox;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.modals.BaseModal;
import earth.terrarium.olympus.client.utils.ListenableState;
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

    private final BiConsumer<String, String> callback;

    private ListenableState<String> label;
    private ListenableState<String> link = ListenableState.of("");

    protected LinkModal(String initialLabel, BiConsumer<String, String> callback, Screen background) {
        super(Component.literal("Display Link"), background);

        this.label = ListenableState.of(initialLabel);
        this.callback = callback;

        this.minHeight = TITLE_BAR_HEIGHT + INNER_PADDING * 4 + WIDGET_HEIGHT * 3;
        this.minWidth = 150;
        this.ratio = 0f;
    }

    @Override
    protected void init() {
        super.init();

        GridLayout layout = new GridLayout().spacing(INNER_PADDING);

        var button = layout.addChild(Widgets.button()
                .withCallback(() -> {
                    this.callback.accept(this.label.get(), this.link.get());
                    this.onClose();
                })
                .withRenderer(WidgetRenderers.text(Component.literal("Create")).withColor(Color.parse("#FEFEFE")))
                .withSize(this.modalContentWidth, WIDGET_HEIGHT)
                .withTexture(UIConstants.PRIMARY_BUTTON),
            2, 0
        );
        button.active = false;

        TextBox label = Widgets.textInput(this.label).withPlaceholder("Label").withMaxLength(Short.MAX_VALUE);

        this.label.registerListener(s -> button.active = !s.isEmpty() && isValidLink(this.link.get()));

        layout.addChild(label.withSize(this.modalContentWidth, WIDGET_HEIGHT), 0, 0);

        TextBox link = Widgets.textInput(this.link).withPlaceholder("Link").withMaxLength(Short.MAX_VALUE);

        this.link.registerListener(s -> {
            boolean valid = isValidLink(s);
            button.active = !this.label.get().isEmpty() && valid;
            if (valid) {
                link.withTextColor(Color.parse("#e0e0e0"));
            } else {
                link.withTextColor(Color.parse("#FF5555"));
            }
        });

        layout.addChild(link.withSize(this.modalContentWidth, WIDGET_HEIGHT), 1, 0);

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

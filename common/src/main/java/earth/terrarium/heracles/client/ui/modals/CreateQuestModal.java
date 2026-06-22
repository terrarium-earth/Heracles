package earth.terrarium.heracles.client.ui.modals;

import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.buttons.Button;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.components.textbox.TextBox;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.modals.BaseModal;
import earth.terrarium.olympus.client.utils.ListenableState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

public class CreateQuestModal extends BaseModal {

    private static final Pattern REGEX = Pattern.compile("[\\x00-\\x7F]{2,}");

    private static final int WIDTH = 150;
    private static final int WIDGET_HEIGHT = 24;

    private final BiConsumer<String, String> callback;
    private final BiPredicate<String, String> validator;

    private Button button;
    private TextBox idBox;
    private TextBox nameBox;
    private ListenableState<String> idState;
    private ListenableState<String> nameState;

    protected CreateQuestModal(Screen background, BiConsumer<String, String> callback, BiPredicate<String, String> validator) {
        super(Component.translatable("gui.heracles.add_quest"), background);
        this.callback = callback;
        this.validator = validator;

        this.minHeight = 4 * INNER_PADDING + 3 * WIDGET_HEIGHT + TITLE_BAR_HEIGHT;
        this.minWidth = WIDTH;
        this.ratio = 0f;
    }

    @Override
    protected void init() {
        super.init();

        GridLayout layout = new GridLayout().rowSpacing(INNER_PADDING);

        boolean wasActive = this.button != null && this.button.active;

        this.button = layout.addChild(
            Widgets.button()
                .withCallback(() -> {
                    this.onClose();
                    this.callback.accept(this.idBox.getValue(), this.nameBox.getValue());
                })
                .withRenderer(WidgetRenderers.text(Component.literal("Create")).withColor(Color.tryParse("#FEFEFE")))
                .withSize(this.modalContentWidth, WIDGET_HEIGHT)
                .withTexture(UIConstants.PRIMARY_BUTTON),
            2, 0
        );
        this.button.active = wasActive;

        if (this.nameState == null) this.nameState = ListenableState.of("");
        if (this.idState == null) this.idState = ListenableState.of("");

        this.nameState.registerListener(text -> this.button.active = this.validator.test(this.idBox.getValue(), this.nameBox.getValue()));
        this.idState.registerListener(text -> this.button.active = this.validator.test(this.idBox.getValue(), this.nameBox.getValue()));

        this.nameBox = layout.addChild(
            Widgets.textInput(this.nameState, tb -> {
                tb.withSize(this.modalContentWidth, WIDGET_HEIGHT);
                tb.withMaxLength(Short.MAX_VALUE);
                tb.withPlaceholder(Component.translatable("gui.heracles.name").getString());
            }),
            0, 0
        );

        this.idBox = layout.addChild(
            Widgets.textInput(this.idState, tb -> {
                tb.withSize(this.modalContentWidth, WIDGET_HEIGHT);
                tb.withMaxLength(Short.MAX_VALUE);
                tb.withPlaceholder(Component.translatable("gui.heracles.id").getString());
            }),
            1, 0
        );

        layout.arrangeElements();
        layout.setPosition(this.modalContentLeft, this.modalContentTop);
        layout.visitWidgets(this::addRenderableWidget);
    }

    public static void open(BiConsumer<String, String> callback) {
        Screen background = Minecraft.getInstance().screen;
        CreateQuestModal modal = new CreateQuestModal(
            background, callback,
            (id, name) -> REGEX.matcher(id).matches() && ClientQuests.get(id.trim()).isEmpty()
        );
        Minecraft.getInstance().setScreen(modal);
    }
}

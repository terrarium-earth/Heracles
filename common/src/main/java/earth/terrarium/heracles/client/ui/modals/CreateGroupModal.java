package earth.terrarium.heracles.client.ui.modals;

import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.constants.ConstantComponents;
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

import java.util.function.Consumer;

public class CreateGroupModal extends BaseModal {

    private static final int WIDTH = 150;
    private static final int WIDGET_HEIGHT = 24;

    private final Consumer<String> callback;

    private Button button;
    private TextBox nameBox;
    private ListenableState<String> nameState;

    protected CreateGroupModal(Screen background, Consumer<String> callback) {
        super(ConstantComponents.Groups.CREATE, background);
        this.callback = callback;

        this.minHeight = 3 * INNER_PADDING + 2 * WIDGET_HEIGHT + TITLE_BAR_HEIGHT;
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
                    this.callback.accept(this.nameBox.getValue());
                })
                .withRenderer(WidgetRenderers.text(Component.literal("Create")).withColor(Color.tryParse("#FEFEFE")))
                .withSize(this.modalContentWidth, WIDGET_HEIGHT)
                .withTexture(UIConstants.PRIMARY_BUTTON),
            1, 0
        );
        this.button.active = wasActive;

        if (this.nameState == null) this.nameState = ListenableState.of("");

        this.nameState.registerListener(text -> this.button.active = !text.isBlank() && !ClientQuests.groups().contains(text.trim()));

        this.nameBox = layout.addChild(
            Widgets.textInput(this.nameState, tb -> {
                tb.withSize(this.modalContentWidth, WIDGET_HEIGHT);
                tb.withMaxLength(Short.MAX_VALUE);
                tb.withPlaceholder(Component.translatable("gui.heracles.name").getString());
            }),
            0, 0
        );

        layout.arrangeElements();
        layout.setPosition(this.modalContentLeft, this.modalContentTop);
        layout.visitWidgets(this::addRenderableWidget);
    }

    public static void open(Consumer<String> callback) {
        Screen background = Minecraft.getInstance().screen;
        CreateGroupModal modal = new CreateGroupModal(background, callback);
        Minecraft.getInstance().setScreen(modal);
    }
}

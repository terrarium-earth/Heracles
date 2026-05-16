package earth.terrarium.heracles.client.ui.modals;

import earth.terrarium.heracles.client.components.widgets.buttons.TextButton;
import earth.terrarium.heracles.client.components.widgets.textbox.TextBox;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.utils.ModUtils;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.modals.BaseModal;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
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
            new TextButton(this.modalContentWidth, WIDGET_HEIGHT, 0xFEFEFE, UIConstants.PRIMARY_BUTTON, Component.literal("Create"), b -> {
                this.onClose();
                this.callback.accept(this.nameBox.getValue());
            }),
            1, 0
        );
        this.button.active = wasActive;

        this.nameBox = layout.addChild(
            new TextBox(
                this.nameBox, "",
                this.modalContentWidth, WIDGET_HEIGHT,
                Short.MAX_VALUE, ModUtils.predicateTrue(),
                text -> this.button.active = !text.isBlank() && !ClientQuests.groups().contains(text.trim())
            ),
            0, 0
        );
        this.nameBox.setPlaceholder(Component.translatable("gui.heracles.name"));

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

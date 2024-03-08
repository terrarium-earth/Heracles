package earth.terrarium.heracles.client.ui.modals;

import earth.terrarium.heracles.client.components.string.MultilineTextWidget;
import earth.terrarium.heracles.client.components.widgets.buttons.TextButton;
import earth.terrarium.heracles.client.ui.UIConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class DeleteConfirmModal extends BaseModal {

    private static final int WIDTH = 150;
    private static final int HEIGHT = 100;
    private static final int WIDGET_HEIGHT = 24;

    private final Component description;
    private final Runnable action;

    protected DeleteConfirmModal(Component title, Component description, Runnable action, Screen background) {
        super(title, background);

        this.description = description;
        this.action = action;

        this.minHeight = HEIGHT;
        this.minWidth = WIDTH;
        this.ratio = 0f;
    }

    @Override
    protected void init() {
        this.modalWidth = Math.round(Math.max(this.minWidth, this.width * this.ratio));
        this.left = (this.width - this.modalWidth) / 2;
        this.modalContentLeft = this.left + INNER_PADDING;
        this.modalContentWidth = this.modalWidth - INNER_PADDING * 2;

        int buttonWidth = (this.modalContentWidth - INNER_PADDING) / 2;

        GridLayout layout = new GridLayout().rowSpacing(INNER_PADDING);

        layout.addChild(
            new MultilineTextWidget(this.modalContentWidth, this.description, this.font).setColor(0xFFFFFF),
            0, 0
        );

        GridLayout buttons = new GridLayout().columnSpacing(INNER_PADDING);

        buttons.addChild(
            new TextButton(
                buttonWidth, WIDGET_HEIGHT,
                0x333333, UIConstants.BUTTON,
                CommonComponents.GUI_CANCEL,
                button -> this.onClose()
            ),
            0, 0
        );

        buttons.addChild(
            new TextButton(
                buttonWidth, WIDGET_HEIGHT,
                0xFFFFFF, UIConstants.DANGER_BUTTON,
                Component.literal("Delete"),
                button -> {
                    this.action.run();
                    this.onClose();
                }
            ),
            0, 1
        );

        layout.addChild(buttons, 1, 0);
        layout.arrangeElements();

        this.modalHeight = layout.getHeight() + TITLE_BAR_HEIGHT + INNER_PADDING * 2;
        this.top = (this.height - this.modalHeight) / 2;
        this.modalContentTop = this.top + TITLE_BAR_HEIGHT + INNER_PADDING;
        this.modalContentHeight = this.modalHeight - TITLE_BAR_HEIGHT - INNER_PADDING * 2;

        layout.setPosition(this.modalContentLeft, this.modalContentTop);
        layout.visitWidgets(this::addRenderableWidget);
    }

    public static void open(Component title, Component description, Runnable action) {
        Screen background = Minecraft.getInstance().screen;
        DeleteConfirmModal modal = new DeleteConfirmModal(title, description, action, background);
        Minecraft.getInstance().setScreen(modal);
    }
}

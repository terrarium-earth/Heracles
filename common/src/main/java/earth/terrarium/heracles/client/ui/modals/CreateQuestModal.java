package earth.terrarium.heracles.client.ui.modals;

import earth.terrarium.heracles.client.components.widgets.buttons.TextButton;
import earth.terrarium.heracles.client.components.widgets.textbox.TextBox;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.UIComponents;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
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

    protected CreateQuestModal(Screen background, BiConsumer<String, String> callback, BiPredicate<String, String> validator) {
        super(UIComponents.ADD_QUEST, background);
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
            new TextButton(this.modalContentWidth, WIDGET_HEIGHT, 0xFEFEFE, UIConstants.PRIMARY_BUTTON, Component.literal("Create"), b -> {
                this.onClose(); // Close the previous modal
                this.callback.accept(this.idBox.getValue(), this.nameBox.getValue());
            }),
            2, 0
        );
        this.button.active = wasActive;

        this.nameBox = layout.addChild(
            new TextBox(
                this.nameBox, "",
                this.modalContentWidth, WIDGET_HEIGHT,
                Short.MAX_VALUE, ModUtils.predicateTrue(),
                text -> this.button.active = this.validator.test(this.idBox.getValue(), this.nameBox.getValue())
            ),
            0, 0
        );
        this.nameBox.setPlaceholder(UIComponents.NAME);

        this.idBox = layout.addChild(
            new TextBox(
                this.idBox, "",
                this.modalContentWidth, WIDGET_HEIGHT,
                Short.MAX_VALUE, ModUtils.predicateTrue(),
                text -> this.button.active = this.validator.test(this.idBox.getValue(), this.nameBox.getValue())
            ),
            1, 0
        );
        this.idBox.setPlaceholder(UIComponents.ID);

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

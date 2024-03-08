package earth.terrarium.heracles.client.ui.modals;

import earth.terrarium.heracles.client.components.widgets.buttons.TextButton;
import earth.terrarium.heracles.client.components.widgets.dropdown.Dropdown;
import earth.terrarium.heracles.client.components.widgets.textbox.TextBox;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CreateObjectModal extends BaseModal {

    private static final int WIDTH = 150;
    private static final int WIDGET_HEIGHT = 24;

    private final String type;
    private final BiConsumer<ResourceLocation, String> callback;
    private final BiPredicate<ResourceLocation, String> validator;
    private final Collection<ResourceLocation> suggestions;

    private Button button;
    private TextBox nameBox;
    private Dropdown<ResourceLocation> dropdown;

    protected CreateObjectModal(Screen background, String type, BiConsumer<ResourceLocation, String> callback, BiPredicate<ResourceLocation, String> validator, Collection<ResourceLocation> suggestions) {
        super(Component.translatable("gui.heracles." + type + ".create"), background);
        this.type = type;
        this.callback = callback;
        this.validator = validator;
        this.suggestions = suggestions;

        this.minHeight = 4 * INNER_PADDING + 3 * WIDGET_HEIGHT + TITLE_BAR_HEIGHT;
        this.minWidth = WIDTH;
        this.ratio = 0f;
    }

    @Override
    protected void init() {
        super.init();

        Map<ResourceLocation, Component> suggestions = this.suggestions.stream()
            .collect(Collectors.toMap(Function.identity(), id -> Component.translatable(id.toLanguageKey(type))));

        GridLayout layout = new GridLayout().rowSpacing(INNER_PADDING);

        boolean wasActive = this.button != null && this.button.active;

        this.button = layout.addChild(
            new TextButton(this.modalContentWidth, WIDGET_HEIGHT, 0xFEFEFE, UIConstants.PRIMARY_BUTTON, Component.literal("Create"), b -> {
                this.onClose(); // Close the previous modal
                this.callback.accept(this.dropdown.selected(), this.nameBox.getValue());
            }),
            2, 0
        );
        this.button.active = wasActive;

        this.nameBox = layout.addChild(
            new TextBox(
                this.nameBox, "",
                this.modalContentWidth, WIDGET_HEIGHT,
                Short.MAX_VALUE, ModUtils.predicateTrue(),
                text -> this.button.active = this.validator.test(this.dropdown.selected(), this.nameBox.getValue())
            ),
            0, 0
        );

        this.dropdown = layout.addChild(
            new Dropdown<>(
                this.dropdown,
                this.modalContentWidth, WIDGET_HEIGHT,
                suggestions, null,
                value -> this.button.active = this.validator.test(this.dropdown.selected(), this.nameBox.getValue())
            ),
            1, 0
        );

        layout.arrangeElements();
        layout.setPosition(this.modalContentLeft, this.modalContentTop);
        layout.visitWidgets(this::addRenderableWidget);
    }

    public static void open(String type, BiConsumer<ResourceLocation, String> callback, BiPredicate<ResourceLocation, String> validator, Collection<ResourceLocation> suggestions) {
        Screen background = Minecraft.getInstance().screen;
        CreateObjectModal modal = new CreateObjectModal(background, type, callback, validator, suggestions);
        Minecraft.getInstance().setScreen(modal);
    }
}

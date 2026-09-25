package earth.terrarium.heracles.client.ui.modals;

import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.buttons.Button;
import earth.terrarium.olympus.client.components.dropdown.DropdownState;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.components.textbox.TextBox;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.modals.BaseModal;
import earth.terrarium.olympus.client.utils.ListenableState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class CreateObjectModal extends BaseModal {

    private static final int WIDTH = 150;
    private static final int WIDGET_HEIGHT = 24;

    private final String type;
    private final BiConsumer<ResourceLocation, String> callback;
    private final BiPredicate<ResourceLocation, String> validator;
    private final Collection<ResourceLocation> suggestions;

    private Button button;
    private TextBox nameBox;
    private ListenableState<String> nameState;
    private DropdownState<ResourceLocation> dropdownState;

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

        if (this.dropdownState == null) {
            this.dropdownState = DropdownState.of(null);
        }

        Function<ResourceLocation, Component> optionText = id -> Component.translatable(id.toLanguageKey(type));

        GridLayout layout = new GridLayout().rowSpacing(INNER_PADDING);

        boolean wasActive = this.button != null && this.button.active;

        this.button = layout.addChild(
            Widgets.button()
                .withCallback(() -> {
                    this.onClose();
                    this.callback.accept(this.dropdownState.get(), this.nameBox.getValue());
                })
                .withRenderer(WidgetRenderers.text(Component.literal("Create")).withColor(Color.tryParse("#FEFEFE")))
                .withSize(this.modalContentWidth, WIDGET_HEIGHT)
                .withTexture(UIConstants.PRIMARY_BUTTON),
            2, 0
        );
        this.button.active = wasActive;

        if (this.nameState == null) this.nameState = ListenableState.of("");

        this.nameState.registerListener(text -> this.button.active = this.validator.test(this.dropdownState.get(), this.nameBox.getValue()));

        this.nameBox = layout.addChild(
            Widgets.textInput(this.nameState, tb -> {
                tb.withSize(this.modalContentWidth, WIDGET_HEIGHT);
                tb.withMaxLength(Short.MAX_VALUE);
            }),
            0, 0
        );

        Button dropdownButton = layout.addChild(
            Widgets.dropdown(
                this.dropdownState,
                new ArrayList<>(this.suggestions),
                optionText,
                btn -> btn.withSize(this.modalContentWidth, WIDGET_HEIGHT),
                dropdown -> dropdown.withSize(this.modalContentWidth, WIDGET_HEIGHT * 4).withCallback(
                    value -> this.button.active = this.validator.test(this.dropdownState.get(), this.nameBox.getValue())
                )
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

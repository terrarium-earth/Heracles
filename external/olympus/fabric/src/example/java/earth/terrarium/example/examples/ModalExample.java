package earth.terrarium.example.examples;

import earth.terrarium.example.base.ExampleScreen;
import earth.terrarium.example.base.OlympusExample;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.dropdown.DropdownState;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.layouts.Layouts;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.modals.ActionModal;
import earth.terrarium.olympus.client.ui.modals.Modals;
import earth.terrarium.olympus.client.utils.State;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.network.chat.Component;

@OlympusExample(id = "modal", description = "A simple modal example")
public class ModalExample extends ExampleScreen {

    @Override
    protected void init() {
        DropdownState<ChatFormatting> dropdownState = DropdownState.of(ChatFormatting.RESET);
        ActionModal.Builder actionModal = Modals.action()
                .withTitle(Component.literal("Action modal"))
                .withContent(Component.literal(
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                                "Nunc vulputate euismod velit, et maximus erat tincidunt quis. Etiam quis nibh tellus. " +
                                "Aliquam eros enim, vulputate ut quam venenatis, rhoncus ullamcorper ipsum. " +
                                "Donec pellentesque risus ex, id elementum erat condimentum eu. " +
                                "Fusce sagittis neque vitae felis semper, nec tempus orci aliquam. Suspendisse imperdiet sagittis vestibulum. " +
                                "Nam ut mollis nisl."
                ))
                .withContent(i -> Widgets.textInput(State.of("Input text")).withSize(i, 20))
                .withContent(i -> Widgets.dropdown(dropdownState, ChatFormatting.class).withSize(i, 20))
                .withAction(Widgets.button()
                        .withSize(50, 20)
                        .withTexture(UIConstants.PRIMARY_BUTTON)
                        .withCallback(() -> System.out.println("Primary button clicked"))
                )
                .withAction(Widgets.button()
                        .withSize(50, 20)
                        .withTexture(UIConstants.DANGER_BUTTON)
                        .withCallback(() -> System.out.println("Danger button clicked"))
                )
                .withAction(Widgets.button()
                        .withSize(50, 20)
                        .withTexture(UIConstants.DARK_BUTTON)
                        .withCallback(() -> System.out.println("Dark button clicked"))
                );

        ActionModal.Builder deleteModal = Modals.delete(
                Component.literal("Delete modal"),
                Component.literal("Are you sure you want to delete this item?"),
                () -> System.out.println("Item deleted")
        );

        ActionModal.Builder linkModal = Modals.link("https://github.com/terrarium-earth/olympus");

        FrameLayout.centerInRectangle(
                Layouts.layout()
                        .withGap(10)
                        .withRow(
                                Widgets.button()
                                        .withCallback(actionModal::open)
                                        .withRenderer(WidgetRenderers.text(Component.literal("Open modal")))
                                        .withSize(100, 20),
                                Widgets.button()
                                        .withCallback(deleteModal::open)
                                        .withRenderer(WidgetRenderers.text(Component.literal("Open Delete modal")))
                                        .withSize(100, 20),
                                Widgets.button()
                                        .withCallback(linkModal::open)
                                        .withRenderer(WidgetRenderers.text(Component.literal("Open Link modal")))
                                        .withSize(100, 20)
                        )
                        .build(this::addRenderableWidget),
                0, 0,
                this.width, this.height
        );
    }
}

package earth.terrarium.example.examples;

import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.example.base.ExampleScreen;
import earth.terrarium.example.base.OlympusExample;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.dropdown.DropdownState;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.constants.MinecraftColors;
import earth.terrarium.olympus.client.ui.OverlayAlignment;
import earth.terrarium.olympus.client.ui.UIIcons;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.function.Consumers;

import java.util.Arrays;
import java.util.HashMap;

@OlympusExample(id = "dropdown", description = "A simple dropdown example")
public class DropdownExample extends ExampleScreen {
    public final DropdownState<Color> state1 = DropdownState.of(MinecraftColors.RED);
    public final DropdownState<Color> state2 = DropdownState.empty();
    public final DropdownState<Color> state3 = DropdownState.empty();

    public final HashMap<OverlayAlignment, DropdownState<Color>> states = new HashMap<>();

    public DropdownExample() {
        states.put(OverlayAlignment.BOTTOM_RIGHT, DropdownState.of(MinecraftColors.RED));
        states.put(OverlayAlignment.TOP_RIGHT, DropdownState.of(MinecraftColors.RED));
        states.put(OverlayAlignment.TOP_LEFT, DropdownState.of(MinecraftColors.RED));
        states.put(OverlayAlignment.BOTTOM_LEFT, DropdownState.of(MinecraftColors.RED));
        states.put(OverlayAlignment.RIGHT_TOP, DropdownState.of(MinecraftColors.RED));
        states.put(OverlayAlignment.RIGHT_BOTTOM, DropdownState.of(MinecraftColors.RED));
        states.put(OverlayAlignment.LEFT_TOP, DropdownState.of(MinecraftColors.RED));
        states.put(OverlayAlignment.LEFT_BOTTOM, DropdownState.of(MinecraftColors.RED));
    }

    @Override
    protected void init() {
        super.init();

        LinearLayout horizontal = LinearLayout.horizontal().spacing(20);

        LinearLayout secondary = LinearLayout.horizontal().spacing(20);

        horizontal.addChild(Widgets.button()
                .withRenderer(state1.withRenderer((color, bool) -> color == null ? WidgetRenderers.ellpsisWithChevron(bool) : WidgetRenderers.textWithChevron(Component.literal("Color"), bool).withColor(color).withShadow()).withPadding(4, 6))
                .withSize(100, 24)
                .withDropdown(state1)
                        .withOptions(Arrays.asList(MinecraftColors.COLORS))
                        .withEntryRenderer((color) -> WidgetRenderers.text(Component.literal("Color")).withColor(color))
                .build());

        horizontal.addChild(Widgets.button()
                .withRenderer(state2.withRenderer((color, bool) -> color == null ? WidgetRenderers.ellpsisWithChevron(bool) : WidgetRenderers.textWithChevron(Component.literal("Color"), bool).withColor(color)).withPadding(4, 6))
                .withSize(100, 24)
                .withDropdown(state2)
                .withAlignment(OverlayAlignment.TOP_LEFT)
                .withOptions(Arrays.asList(MinecraftColors.COLORS))
                .withEntryRenderer((color) -> WidgetRenderers.text(Component.literal("Color")).withColor(color).withAlignment(0).withPadding(0, 4))
                .build());

        horizontal.addChild(Widgets.dropdown(
                state3,
                Arrays.asList(MinecraftColors.COLORS),
                color -> Component.literal(color.toString()),
                button -> button.withSize(100, 24),
                Consumers.nop()
        ));

        for (OverlayAlignment align : OverlayAlignment.values()) {
            var state = states.get(align);
            secondary.addChild(Widgets.button()
                    .withRenderer(state.withRenderer((color, bool) -> {
                        var renderer = WidgetRenderers.icon(bool ? UIIcons.CHEVRON_UP : UIIcons.CHEVRON_DOWN);
                        if (color != null) renderer.withColor(color).withShadow();
                        return renderer;
                    }).withCentered(10, 10))
                    .withSize(20, 20)
                    .withDropdown(state)
                    .withSize(100, 150)
                    .withAlignment(align)
                    .withOptions(Arrays.asList(MinecraftColors.COLORS))
                    .withEntryRenderer((color) -> WidgetRenderers.text(Component.literal("Color")).withColor(color))
                    .build()
            );
        }

        horizontal.arrangeElements();
        secondary.arrangeElements();
        FrameLayout.centerInRectangle(horizontal, 0, 0, this.width, this.height);
        FrameLayout.centerInRectangle(secondary, 0, 0, this.width, this.height);
        secondary.setY(secondary.getY() + 50);
        horizontal.visitWidgets(this::addRenderableWidget);
        secondary.visitWidgets(this::addRenderableWidget);
    }
}

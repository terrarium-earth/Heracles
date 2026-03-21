package earth.terrarium.example.examples;


import earth.terrarium.example.base.ExampleScreen;
import earth.terrarium.example.base.OlympusExample;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.compound.radio.RadioState;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.constants.MinecraftColors;
import earth.terrarium.olympus.client.ui.UIConstants;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.function.Consumers;

@OlympusExample(id = "radio", description = "A simple radio example")
public class RadioExample extends ExampleScreen {
    private final RadioState<String> state = RadioState.empty();

    @Override
    protected void init() {
        var radio = Widgets.radio(
                state,
                builder -> builder.withOption("1")
                        .withOption("2")
                        .withOption("3")
                        .withRenderer((option, selected) -> WidgetRenderers.layered(
                                WidgetRenderers.sprite(selected ? UIConstants.PRIMARY_BUTTON : UIConstants.BUTTON),
                                WidgetRenderers.text(Component.literal(option)).withColor(selected ? MinecraftColors.WHITE : MinecraftColors.DARK_GRAY)
                        ))
                        .withoutEntrySprites()
                        .withSize(60, 20),
                Consumers.nop()
        );

        FrameLayout.centerInRectangle(radio, 0, 0, this.width, this.height);

        addRenderableWidget(radio);
    }
}

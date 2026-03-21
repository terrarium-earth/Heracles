package earth.terrarium.example.examples;

import earth.terrarium.example.base.ExampleScreen;
import earth.terrarium.example.base.OlympusExample;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.utils.ListenableState;
import net.minecraft.client.gui.layouts.FrameLayout;

@OlympusExample(id = "multiline_text_input", description = "A simple multiline text input example")
public class MultilineTextInputExample extends ExampleScreen {
    private final ListenableState<String> text = ListenableState.of("""
    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque pulvinar, mauris mattis tristique ultrices, quam est pharetra nisl, in ultrices erat nibh at nulla.
    Nullam aliquam pretium orci mattis tempor. Nullam sed commodo turpis, sed placerat erat.
    Phasellus et ante pharetra, lobortis dui in, sollicitudin odio.
    Suspendisse ut bibendum diam. Nulla quis auctor lorem.
    Donec eget tellus sapien. Aenean accumsan arcu elit, pellentesque pellentesque arcu porta quis.
    Donec pellentesque ligula a purus sodales faucibus. Sed eu nibh neque. Integer accumsan eu enim a tempor.
    Pellentesque eu commodo tortor, sed pellentesque ipsum. Curabitur est purus, gravida vel rhoncus sit amet, aliquet eu nibh.
    Praesent porta porttitor quam sed dignissim.
    """);

    @Override
    protected void init() {
        super.init();

        FrameLayout.centerInRectangle(
                addRenderableWidget(Widgets.multilineTextInput(text).withSize(200, 100)),
                0, 0, this.width, this.height
        );
    }
}

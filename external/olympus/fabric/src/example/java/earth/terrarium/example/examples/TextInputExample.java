package earth.terrarium.example.examples;

import earth.terrarium.example.base.ExampleScreen;
import earth.terrarium.example.base.OlympusExample;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.layouts.Layouts;
import earth.terrarium.olympus.client.utils.ListenableState;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.stream.Stream;

@OlympusExample(id = "text_input", description = "A simple text input example")
public class TextInputExample extends ExampleScreen {
    private final ListenableState<String> text = ListenableState.of("Hello, World!");
    private final ListenableState<Integer> number = ListenableState.of(0);
    private final ListenableState<Double> doubleNumber = ListenableState.of(0.0);
    private final ListenableState<String> autocomplete = ListenableState.of("Red");

    public TextInputExample() {
        text.registerListener(newValue -> System.out.println("Text changed to: " + newValue));
        number.registerListener(newValue -> System.out.println("Number changed to: " + newValue));
        doubleNumber.registerListener(newValue -> System.out.println("Double changed to: " + newValue));
    }

    @Override
    protected void init() {
        super.init();

        var layout = Layouts.column();

        // Add a text input widget
        layout.withChild(Widgets.textInput(text, textBox -> {
            textBox.withSize(200, 20);
            textBox.withTooltip(Component.literal("This is a text input widget"));
        }));

        // Add a number input widget
        layout.withChild(Widgets.intInput(number, textBox -> {
            textBox.withSize(200, 20);
            textBox.withTooltip(Component.literal("This is a number input widget"));
        }));

        // Add a decimal input widget
        layout.withChild(Widgets.doubleInput(doubleNumber, textBox -> {
            textBox.withSize(200, 20);
            textBox.withTooltip(Component.literal("This is a double number input widget"));
        }));

        // Add a autocomplete widget
        layout.withChild(Widgets.<String>autocomplete(autocomplete, textBox -> {
            textBox.withSize(200, 20);
            textBox.withTooltip(Component.literal("This is a autocomplete widget"));
            textBox.withSuggestions(value ->
                    Stream.of("Red", "Green", "Blue")
                            .filter(s -> s.toLowerCase(Locale.ROOT).startsWith(value.toLowerCase(Locale.ROOT)))
                            .toList()
            );
            textBox.withAlwaysShow(true);
        }));

        layout.build(this::addRenderableWidget);
        FrameLayout.centerInRectangle(layout, 0, 0, this.width, this.height);
    }
}

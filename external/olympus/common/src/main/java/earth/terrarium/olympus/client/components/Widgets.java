package earth.terrarium.olympus.client.components;

import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.olympus.client.components.base.BaseWidget;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer;
import earth.terrarium.olympus.client.components.buttons.Button;
import earth.terrarium.olympus.client.components.color.ColorPickerOverlay;
import earth.terrarium.olympus.client.components.compound.LayoutWidget;
import earth.terrarium.olympus.client.components.compound.radio.RadioBuilder;
import earth.terrarium.olympus.client.components.compound.radio.RadioState;
import earth.terrarium.olympus.client.components.dropdown.DropdownBuilder;
import earth.terrarium.olympus.client.components.dropdown.DropdownState;
import earth.terrarium.olympus.client.components.map.MapRenderer;
import earth.terrarium.olympus.client.components.map.MapWidget;
import earth.terrarium.olympus.client.components.renderers.Renderable;
import earth.terrarium.olympus.client.components.renderers.TristateRenderers;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.components.string.MultilineTextWidget;
import earth.terrarium.olympus.client.components.string.TextWidget;
import earth.terrarium.olympus.client.components.textbox.TextBox;
import earth.terrarium.olympus.client.components.textbox.autocomplete.AutocompleteTextBox;
import earth.terrarium.olympus.client.components.textbox.multiline.MultilineTextBox;
import earth.terrarium.olympus.client.constants.MinecraftColors;
import earth.terrarium.olympus.client.layouts.Layouts;
import earth.terrarium.olympus.client.layouts.LinearViewLayout;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.utils.State;
import earth.terrarium.olympus.client.utils.StateUtils;
import earth.terrarium.olympus.client.utils.Translatable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;
import org.apache.commons.lang3.function.Consumers;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public final class Widgets {

    public static Button button(Consumer<Button> factory) {
        return Util.make(new Button(), factory);
    }

    public static Button button() {
        return button(Consumers.nop());
    }

    public static Button toggle(State<Boolean> state, Consumer<Button> factory) {
        WidgetRenderer<Button> onRenderer = WidgetRenderers.sprite(UIConstants.SWITCH_ON);
        WidgetRenderer<Button> offRenderer = WidgetRenderers.sprite(UIConstants.SWITCH);
        Consumer<Button> switchFactory = button -> button
                .withCallback(StateUtils.booleanToggle(state))
                .withTexture(null)
                .withRenderer((graphics, context, partialTick) ->
                        (state.get() ? onRenderer : offRenderer).render(graphics, context, partialTick)
                );
        return button(switchFactory.andThen(factory));
    }

    public static Button toggle(State<Boolean> state) {
        return toggle(state, Consumers.nop());
    }

    public static <T> Button dropdown(DropdownState<T> state, List<T> options, Function<T, Component> optionText, Consumer<Button> factory, Consumer<DropdownBuilder<T>> builder) {
        var button = button((btn -> btn.withRenderer(state.withRenderer((value, open) -> value == null ? WidgetRenderers.ellpsisWithChevron(open) : WidgetRenderers.textWithChevron(optionText.apply(value), open)).withPadding(4, 6))));
        factory.accept(button);

        var dropdown = button.withDropdown(state);
        dropdown.withOptions(options).withEntryRenderer(t -> WidgetRenderers.text(optionText.apply(t)).withColor(MinecraftColors.WHITE).withAlignment(0).withPadding(0, 4));

        builder.accept(dropdown);
        return dropdown.build();
    }

    public static <T extends Enum<?>> Button dropdown(DropdownState<T> state, Class<T> clazz, Consumer<DropdownBuilder<T>> dropdownFactory, Consumer<Button> buttonFactory) {
        return dropdown(
                state,
                List.of(clazz.getEnumConstants()),
                Translatable::toComponent,
                buttonFactory,
                dropdownFactory
        );
    }

    public static <T extends Enum<?>> Button dropdown(DropdownState<T> state, Class<T> clazz) {
        return dropdown(state, clazz, Consumers.nop(), Consumers.nop());
    }

    public static MapWidget map(State<MapRenderer> state, Consumer<MapWidget> factory) {
        return Util.make(new MapWidget(state).withRenderDistanceScale(), factory);
    }

    public static MapWidget map(State<MapRenderer> state) {
        return map(state, Consumers.nop());
    }

    public static <T> LayoutWidget<LinearViewLayout> radio(RadioState<T> state, Consumer<RadioBuilder<T>> builder, Consumer<LayoutWidget<LinearViewLayout>> factory) {
        RadioBuilder<T> radioBuilder = new RadioBuilder<>(state);
        builder.accept(radioBuilder);
        factory.accept(radioBuilder.build());
        return radioBuilder.build();
    }

    public static LayoutWidget<LinearViewLayout> tristate(RadioState<TriState> state, Consumer<RadioBuilder<TriState>> builder, Consumer<LayoutWidget<LinearViewLayout>> factory) {
        RadioBuilder<TriState> radioBuilder = new RadioBuilder<>(state);
        radioBuilder.withoutEntrySprites()
                .withRenderer((triState, depressed) -> WidgetRenderers.layered(
                        WidgetRenderers.sprite(depressed ? TristateRenderers.getButtonSprites(triState) : UIConstants.BUTTON),
                        WidgetRenderers.icon(TristateRenderers.getIcon(triState)).withColor(depressed ? MinecraftColors.WHITE : TristateRenderers.getColor(triState)).withCentered(12, 12).withPadding(0, 0, 2, 0)
                ))
                .withOption(TriState.TRUE)
                .withOption(TriState.UNDEFINED)
                .withOption(TriState.FALSE)
                .withSize(60, 20);
        builder.accept(radioBuilder);
        factory.accept(radioBuilder.build());
        return radioBuilder.build();
    }

    public static LayoutWidget<LinearViewLayout> tristate(RadioState<TriState> state) {
        return tristate(state, Consumers.nop(), Consumers.nop());
    }

    public static LayoutWidget<FrameLayout> frame(Consumer<LayoutWidget<FrameLayout>> factory) {
        return Util.make(new LayoutWidget<>(new FrameLayout()), factory);
    }

    public static LayoutWidget<FrameLayout> frame() {
        return frame(Consumers.nop());
    }

    public static LayoutWidget<LinearViewLayout> list(Consumer<LayoutWidget<LinearViewLayout>> factory) {
        return Util.make(new LayoutWidget<>(Layouts.column()), factory);
    }

    public static LayoutWidget<LinearViewLayout> carousel(Consumer<LayoutWidget<LinearViewLayout>> factory) {
        return Util.make(new LayoutWidget<>(Layouts.row()), factory);
    }

    public static LayoutWidget<FrameLayout> labelled(Font font, Component label, Color color, AbstractWidget widget, Consumer<LayoutWidget<FrameLayout>> factory) {
        return frame(frame -> {
            frame.withStretchToContentHeight();
            frame.withWidthCallback((frameWidget, frameLayout) -> frameLayout.setMinWidth(frameWidget.getViewWidth()));
            frame.withContents(contents -> {
                var text = label.copy().withColor(color.getValue());
                contents.addChild(new StringWidget(text, font), LayoutSettings::alignHorizontallyLeft);
                contents.addChild(widget, LayoutSettings::alignHorizontallyRight);
            });
            factory.accept(frame);
        });
    }

    public static LayoutWidget<FrameLayout> labelled(Font font, Component label, AbstractWidget widget, Consumer<LayoutWidget<FrameLayout>> factory) {
        return labelled(font, label, MinecraftColors.WHITE, widget, factory);
    }

    public static LayoutWidget<FrameLayout> labelled(Font font, Component label, Color color, AbstractWidget widget) {
        return labelled(font, label, color, widget, Consumers.nop());
    }

    public static LayoutWidget<FrameLayout> labelled(Font font, Component label, AbstractWidget widget) {
        return labelled(font, label, widget, Consumers.nop());
    }

    public static TextBox textInput(State<String> state, Consumer<TextBox> factory) {
        return Util.make(new TextBox(state), factory);
    }

    public static TextBox textInput(State<String> state) {
        return textInput(state, Consumers.nop());
    }

    public static MultilineTextBox multilineTextInput(State<String> state, Consumer<MultilineTextBox> factory) {
        return Util.make(new MultilineTextBox(state), factory);
    }

    public static MultilineTextBox multilineTextInput(State<String> state) {
        return multilineTextInput(state, Consumers.nop());
    }

    public static TextBox doubleInput(State<Double> state, Consumer<TextBox> factory) {
        var textBox = new TextBox(new State<>() {
            final NumberFormat formatter = Util.make(new DecimalFormat("#"), it -> {
                it.setGroupingUsed(false);
                it.setMaximumFractionDigits(15);
            });
            String temp = formatter.format(state.get());

            @Override
            public void set(String value) {
                value = value.trim();
                if (value.isEmpty() || value.equals("-")) {
                    state.set(0.0);
                    temp = value;
                } else try {
                    state.set(Double.parseDouble(value));
                    temp = value;
                } catch (NumberFormatException e) {
                    state.set(0.0);
                    temp = "";
                }
            }

            @Override
            public String get() {
                return temp;
            }
        }).withFilter(s -> {
            if (s.isEmpty() || s.equals("-")) return true;
            try {
                Double.parseDouble(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });

        factory.accept(textBox);
        return textBox;
    }

    public static TextBox doubleInput(State<Double> state) {
        return doubleInput(state, Consumers.nop());
    }

    public static TextBox intInput(State<Integer> state, Consumer<TextBox> factory) {
        return doubleInput(state.map(Integer::doubleValue, Double::intValue), factory);
    }

    public static TextBox intInput(State<Integer> state) {
        return intInput(state, Consumers.nop());
    }

    public static TextBox colorInput(State<Color> state, Consumer<TextBox> factory) {
        return textInput(new State<>() {
            String temp = state.get().toString();

            @Override
            public void set(String value) {
                state.set(Color.parse(value));
                temp = value;
            }

            @Override
            public String get() {
                return temp;
            }
        }, factory);
    }

    public static TextBox colorInput(State<Color> state) {
        return colorInput(state, Consumers.nop());
    }

    public static <T> AutocompleteTextBox<T> autocomplete(State<String> state, Consumer<AutocompleteTextBox<T>> factory) {
        return Util.make(new AutocompleteTextBox<>(state), factory);
    }

    public static <T> AutocompleteTextBox<T> autocomplete(State<String> state) {
        return autocomplete(state, Consumers.nop());
    }

    public static Button colorPicker(State<Color> state, boolean hasAlpha, Consumer<Button> factory, Consumer<ColorPickerOverlay> overlayFactory) {
        var button = new Button();
        button.withRenderer(state.withRenderer((color) -> {
            var renderer = WidgetRenderers.solid().withColor(color);
            if (!hasAlpha) renderer.withoutAlpha();
            return renderer.withPadding(2, 2, 4, 2);
        }));
        button.withSize(16);
        button.withCallback(() -> {
            ColorPickerOverlay overlay = new ColorPickerOverlay(button, state, hasAlpha);
            overlayFactory.accept(overlay);
            Minecraft.getInstance().setScreen(overlay);
        });
        factory.accept(button);
        return button;
    }

    public static TextWidget text(Component text) {
        return new TextWidget(text);
    }

    public static TextWidget text(Component text, Consumer<TextWidget> factory) {
        TextWidget widget = text(text);
        factory.accept(widget);
        return widget;
    }

    public static TextWidget text(String text) {
        return new TextWidget(Component.literal(text));
    }

    public static TextWidget text(String text, Consumer<TextWidget> factory) {
        TextWidget widget = text(Component.literal(text));
        factory.accept(widget);
        return widget;
    }

    public static MultilineTextWidget textarea(Component text, int width, Consumer<MultilineTextWidget> factory) {
        MultilineTextWidget widget = new MultilineTextWidget(text, width);
        factory.accept(widget);
        return widget;
    }

    public static MultilineTextWidget textarea(Component text, int width) {
        return textarea(text, width, Consumers.nop());
    }

    public static BaseWidget renderable(WidgetRenderer<? super BaseWidget> renderer) {
        return renderable(renderer, Consumers.nop());
    }

    public static BaseWidget renderable(WidgetRenderer<? super BaseWidget> renderer, Consumer<BaseWidget> factory) {
        Renderable renderable = new Renderable(renderer);
        factory.accept(renderable);
        return renderable;
    }
}

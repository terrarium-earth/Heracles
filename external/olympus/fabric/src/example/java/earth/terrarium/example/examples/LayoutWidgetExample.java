package earth.terrarium.example.examples;

import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.example.base.ExampleScreen;
import earth.terrarium.example.base.OlympusExample;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.compound.radio.RadioState;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.utils.Orientation;
import earth.terrarium.olympus.client.utils.State;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.network.chat.Component;

@OlympusExample(id = "layout_widget", description = "A simple list example")
public class LayoutWidgetExample extends ExampleScreen {
    public State<String> text = State.of("List Example");
    public State<Integer> number = State.of(0);

    @Override
    protected void init() {
        super.init();

        var list = Widgets.list(widget -> {
            widget.withSize(200, 100);
            widget.withScrollableY(TriState.UNDEFINED);
            widget.withContentFillWidth();
            widget.withContents(layout -> {
                layout.withChild(Widgets.button(button -> {
                    button.withSize(100, 20);
                    button.withRenderer(WidgetRenderers.text(Component.literal("Button 1")));
                }));
                layout.withChild(Widgets.tristate(RadioState.empty()));
                layout.withChild(Widgets.tristate(RadioState.empty()));
                layout.withChild(Widgets.tristate(RadioState.empty()));

                for (int i = 0; i < 4; i++) {
                    layout.withChild(Widgets.frame(compoundWidget -> compoundWidget
                            .withContents(contents -> {
                                contents.addChild(new StringWidget(Component.literal("WOAAHHH"), font), LayoutSettings::alignHorizontallyLeft);
                                contents.addChild(Widgets.tristate(RadioState.empty()), LayoutSettings::alignHorizontallyRight);
                            })
                            .withStretchToContentHeight()
                            .withWidthCallback((frameWidget, frameLayout) -> frameLayout.setMinWidth(frameWidget.getViewWidth()))
                    ));
                }

                layout.withChild(Widgets.frame(frame -> frame.withContents(frameLayout -> {
                    frameLayout.addChild(new StringWidget(Component.literal("WOAAHHH"), font), LayoutSettings::alignHorizontallyLeft);
                    frameLayout.addChild(Widgets.list(listWidget -> {
                        listWidget.withSize(60, 50);
                        listWidget.withScrollableY(TriState.UNDEFINED);
                        listWidget.withContentFillWidth();
                        listWidget.withContents(innerLayout -> {
                            innerLayout.withChild(Widgets.button(button -> {
                                button.withSize(100, 20);
                                button.withRenderer(WidgetRenderers.text(Component.literal("Button 1")));
                            }));
                            innerLayout.withChild(Widgets.tristate(RadioState.empty()));
                            innerLayout.withChild(Widgets.tristate(RadioState.empty()));
                            innerLayout.withChild(Widgets.tristate(RadioState.empty()));
                        });
                    }), LayoutSettings::alignHorizontallyRight);
                    })
                    .withStretchToContentHeight()
                    .withWidthCallback((frameWidget, frameLayout) -> frameLayout.setMinWidth(frameWidget.getViewWidth()))
                ));

                layout.withChild(Widgets.textInput(text, textBox -> textBox.withSize(200, 20)));
                layout.withChild(Widgets.intInput(number, textBox -> textBox.withSize(200, 20)));
            });
        });

        addRenderableWidget(list);

        // addRenderableWidget(Widgets.text(State.of("List Example"), textBox -> textBox.withSize(200, 20)));

        FrameLayout.centerInRectangle(list, 0, 0, this.width, this.height / 2);

        var equalLayout = Widgets.frame(widget -> {
            widget.withTexture(UIConstants.MODAL_HEADER);
            widget.withSize(200, 50);
            widget.withContentMargin(5);

            widget.withEqualSpacing(Orientation.HORIZONTAL);
            widget.withEqualSpacing(Orientation.VERTICAL);

            widget.withContents(contents -> {
                contents.addChild(Widgets.button(button -> {
                    button.withSize(50, 20);
                    button.withRenderer(WidgetRenderers.text(Component.literal("Button 1")));
                }));

                contents.addChild(Widgets.button(button -> {
                    button.withSize(50, 20);
                    button.withRenderer(WidgetRenderers.text(Component.literal("Button 2")));
                }));

                contents.addChild(Widgets.button(button -> {
                    button.withSize(50, 20);
                    button.withRenderer(WidgetRenderers.text(Component.literal("Button 3")));
                }));
            });
        });

        addRenderableWidget(equalLayout);

        FrameLayout.centerInRectangle(equalLayout, 0, this.height / 2, this.width, this.height / 2);
    }
}

package earth.terrarium.example.examples;

import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.example.base.ExampleScreen;
import earth.terrarium.example.base.OlympusExample;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.base.ListWidget;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.network.chat.Component;

@OlympusExample(id = "list", description = "A simple list example")
public class ListExample extends ExampleScreen {
    @Override
    protected void init() {
        super.init();

        var list = new ListWidget(100, 100);

        for (int i = 0; i < 20; i++) {
            list.add(Widgets.button(button -> {
                button.withSize(100, 20);
                button.withRenderer(WidgetRenderers.text(Component.literal("Button")));
            }));
        }

        list.add(Widgets.text(Component.literal("middle")));

        list.add(Widgets.text(Component.literal("left"), (text) -> {
            text.withColor(Color.RAINBOW);
            text.withLeftAlignment();
        }));

        list.add(Widgets.text(Component.literal("right"), (text) -> {
            text.withColor(Color.RAINBOW);
            text.withRightAlignment();
        }));

        list.add(Widgets.text(Component.literal("middle"), (text) -> {
            text.withColor(Color.RAINBOW).withShadow();
        }));

        addRenderableWidget(list);

        FrameLayout.centerInRectangle(list, 0, 0, this.width, this.height);
    }
}
package earth.terrarium.example.examples;

import earth.terrarium.example.base.ExampleScreen;
import earth.terrarium.example.base.OlympusExample;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.utils.State;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.network.chat.Component;

@OlympusExample(id = "toggles", description = "Toggle switches")
public class TogglesExample extends ExampleScreen {

    @Override
    protected void init() {
        LinearLayout horizontal = LinearLayout.horizontal().spacing(20);

        State<Boolean> state = State.of(false);

        horizontal.addChild(Widgets.toggle(state)
                .withTooltip(Component.literal("This is a toggle switch"))
                .withSize(22, 12)
        );

        horizontal.addChild(Widgets.toggle(state)
                .withTooltip(Component.literal("This is another toggle switch"))
                .withSize(22, 12)
        );

        horizontal.arrangeElements();
        FrameLayout.centerInRectangle(horizontal, 0, 0, this.width, this.height);
        horizontal.visitWidgets(this::addRenderableWidget);
    }
}

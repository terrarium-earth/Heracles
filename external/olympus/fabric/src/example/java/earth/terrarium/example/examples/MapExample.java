package earth.terrarium.example.examples;

import earth.terrarium.example.base.ExampleScreen;
import earth.terrarium.example.base.OlympusExample;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.map.MapRenderer;
import earth.terrarium.olympus.client.utils.State;
import net.minecraft.client.gui.layouts.FrameLayout;

@OlympusExample(id = "maps", description = "A simple example of using maps")
public class MapExample extends ExampleScreen {
    public State<MapRenderer> mapState = State.empty();

    @Override
    protected void init() {
        var map = Widgets.map(mapState, widget -> widget.withSize(128));
        FrameLayout.centerInRectangle(map, 0, 0, this.width, this.height);
        this.addRenderableWidget(map);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

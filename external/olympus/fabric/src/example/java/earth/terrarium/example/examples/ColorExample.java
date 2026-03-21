package earth.terrarium.example.examples;

import com.teamresourceful.resourcefullib.common.color.Color;
import com.teamresourceful.resourcefullib.common.color.ConstantColors;
import earth.terrarium.example.base.ExampleScreen;
import earth.terrarium.example.base.OlympusExample;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.constants.MinecraftColors;
import earth.terrarium.olympus.client.layouts.Layouts;
import earth.terrarium.olympus.client.utils.State;
import net.minecraft.client.gui.layouts.FrameLayout;
import org.apache.commons.lang3.function.Consumers;

@OlympusExample(id = "color", description = "A simple color example")
public class ColorExample extends ExampleScreen {
    public final State<Color> color = State.of(MinecraftColors.RED);
    public final Color[] presets = new Color[] { ConstantColors.red, ConstantColors.green, ConstantColors.blue };

    @Override
    protected void init() {
        var layout = Layouts.row();

        layout.withChild(Widgets.colorPicker(color, false, Consumers.nop(), Consumers.nop()));

        layout.withChild(Widgets.colorInput(color, textBox -> textBox.withSize(100, 20)));

        FrameLayout.alignInRectangle(layout, 0, 0, this.width, this.height, 0.5f, 0f);
        layout.build(this::addRenderableWidget);
    }
}

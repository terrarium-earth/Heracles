package earth.terrarium.heracles.client.widgets;

import earth.terrarium.heracles.client.widgets.buttons.ThemedButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

import java.util.function.IntConsumer;

public class StateImageButton extends Button implements ThemedButton {

    private final IntConsumer onPress;
    private final int states;

    private int selected;

    private final ResourceLocation resourceLocation;

    public StateImageButton(
        int x, int y,
        int width, int height,
        ResourceLocation resourceLocation,
        int states,
        IntConsumer onPress
    ) {
        super(
            x, y,
            width, height,
            CommonComponents.EMPTY,
            b -> {},
            DEFAULT_NARRATION
        );
        this.resourceLocation = resourceLocation;
        this.states = states;
        this.onPress = onPress;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int i, int j, float f) {
        ResourceLocation actualRL = resourceLocation;
        if (this.isHovered()) {
            actualRL = actualRL.withSuffix("_hovered");
        }
        actualRL = actualRL.withSuffix("_" + selected);

        graphics.blitSprite(actualRL, getX(), getY(), width, height);
    }

    @Override
    public void onPress() {
        this.selected = (this.selected + 1) % this.states;
        this.onPress.accept(this.selected);
    }

    public int state() {
        return this.selected;
    }
}

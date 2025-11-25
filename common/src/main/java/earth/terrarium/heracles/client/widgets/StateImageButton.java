package earth.terrarium.heracles.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import earth.terrarium.heracles.client.widgets.buttons.ThemedButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;

import java.util.function.IntConsumer;

public class StateImageButton extends ImageButton implements ThemedButton {

    private final IntConsumer onPress;
    private final int states;

    private final WidgetSprites sprites;

    private int selected;

    public StateImageButton(
        int x, int y,
        int width, int height,
        WidgetSprites sprites,
        int states,
        IntConsumer onPress
    ) {
        super(
            x, y,
            width, height,
            sprites,
            b -> {}, CommonComponents.EMPTY
        );
        this.states = states;
        this.onPress = onPress;
        this.sprites = sprites;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int i, int j, float f) {
        RenderSystem.enableDepthTest();
        ResourceLocation resourceLocation = this.sprites.get(this.selected == 0, this.isHoveredOrFocused());
        graphics.blitSprite(resourceLocation, getX(), getY(), width, height);
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

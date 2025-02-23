package earth.terrarium.heracles.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import earth.terrarium.heracles.client.widgets.buttons.ThemedButton;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.network.chat.CommonComponents;

public class SelectableImageButton extends ImageButton implements ThemedButton {

    private boolean selected;

    public SelectableImageButton(int x, int y, int width, int height, WidgetSprites sprites, Button.OnPress onPress) {
        super(
            x, y,
            width, height,
            sprites,
            onPress,
            CommonComponents.EMPTY
        );
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int i, int j, float f) {
        RenderSystem.enableDepthTest();
        graphics.blitSprite(sprites.get(false, false), getX(), getY(), width, height);
    }

    @Override
    public void onPress() {
        if (!isSelected()) {
            super.onPress();
        }
        setSelected(true);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }
}

package earth.terrarium.olympus.client.components.base;

import com.teamresourceful.resourcefullib.client.components.CursorWidget;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public abstract class BaseWidget extends AbstractWidget implements CursorWidget {

    public BaseWidget(int width, int height) {
        super(0, 0, width, height, CommonComponents.EMPTY);
    }

    public BaseWidget() {
        super(0, 0, 0, 0, CommonComponents.EMPTY);
    }

    public BaseWidget withTooltip(Component tooltip) {
        this.setTooltip(Tooltip.create(tooltip));
        return this;
    }

    public BaseWidget withSize(int width, int height) {
        this.setSize(width, height);
        return this;
    }

    public BaseWidget withSize(int size) {
        return this.withSize(size, size);
    }

    public BaseWidget withPosition(int x, int y) {
        this.setPosition(x, y);
        return this;
    }

    public BaseWidget asDisabled() {
        this.active = false;
        return this;
    }

    @Override
    protected abstract void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {

    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return CursorScreen.Cursor.DEFAULT;
    }

    @Override
    public void playDownSound(SoundManager handler) {

    }
}
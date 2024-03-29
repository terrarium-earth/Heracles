package earth.terrarium.heracles.client.components.widgets.buttons;

import com.teamresourceful.resourcefullib.client.components.CursorWidget;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import earth.terrarium.heracles.client.components.widgets.WidgetSprites;
import earth.terrarium.heracles.client.ui.UIConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TextButton extends Button implements CursorWidget {

    protected final WidgetSprites sprites;
    protected final int color;

    public TextButton(int width, int height, int color, WidgetSprites sprites, Component text, OnPress onPress) {
        super(0, 0, width, height, text, onPress, DEFAULT_NARRATION);
        this.color = color;
        this.sprites = sprites;
    }

    public static TextButton create(int width, int height, Component text, OnPress onPress) {
        return new TextButton(width, height, 0x555555, UIConstants.BUTTON, text, onPress);
    }

    public static TextButton create(int width, int height, Component text, Runnable onPress) {
        return new TextButton(width, height, 0x555555, UIConstants.BUTTON, text, (button) -> onPress.run());
    }

    public TextButton withTooltip(Component component) {
        this.setTooltip(Tooltip.create(component));
        return this;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation sprite = this.sprites.get(this.isHoveredOrFocused(), !this.isActive());
        UIConstants.blitWithEdge(graphics, sprite, getX(), getY(), this.width, this.height, 5);

        Font font = Minecraft.getInstance().font;

        int textX = getX() + (this.width - font.width(this.getMessage())) / 2;
        int textY = getY() + 1 + (this.height - font.lineHeight) / 2;

        graphics.drawString(font, this.getMessage(), textX, textY, this.color, false);
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return !this.isActive() ? CursorScreen.Cursor.DISABLED : CursorScreen.Cursor.POINTER;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return this.visible
            && mouseX >= (double)this.getX()
            && mouseY >= (double)this.getY()
            && mouseX < (double)(this.getX() + this.width)
            && mouseY < (double)(this.getY() + this.height);
    }
}

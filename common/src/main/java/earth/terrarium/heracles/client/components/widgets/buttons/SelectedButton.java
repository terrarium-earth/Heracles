package earth.terrarium.heracles.client.components.widgets.buttons;

import com.teamresourceful.resourcefullib.client.components.CursorWidget;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.ui.UIConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SelectedButton extends Button implements CursorWidget {

    private static final ResourceLocation NORMAL = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/tab/normal.png");
    private static final ResourceLocation SELECTED = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/tab/selected.png");
    private static final ResourceLocation HOVERED = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/tab/hovered.png");

    protected boolean isSelected;

    public SelectedButton(int width, int height, Component text, OnPress onPress) {
        super(0, 0, width, height, text, onPress, DEFAULT_NARRATION);
    }

    public static SelectedButton create(int width, int height, Component text, OnPress onPress) {
        return new SelectedButton(width, height, text, onPress);
    }

    public static SelectedButton create(int width, int height, Component text, Runnable onPress) {
        return new SelectedButton(width, height, text, (button) -> onPress.run());
    }

    public SelectedButton withTooltip(Component component) {
        this.setTooltip(Tooltip.create(component));
        return this;
    }

    @Override
    public void onPress() {
        if (this.isSelected) return;
        super.onPress();
    }

    public ResourceLocation get(boolean hovered, boolean selected) {
        if (selected) {
            return SELECTED;
        } else if (hovered) {
            return HOVERED;
        } else {
            return NORMAL;
        }
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation sprite = get(this.isHovered(), this.isSelected);
        UIConstants.blitWithEdge(graphics, sprite, getX(), getY(), this.width, this.height, 5);

        Font font = Minecraft.getInstance().font;

        int textX = getX() + (this.width - font.width(this.getMessage())) / 2;
        int textY = getY() + 1 + (this.height - font.lineHeight) / 2;

        graphics.drawString(font, this.getMessage(), textX, textY, isSelected ? -1 : 0, false);
    }

    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return this.isSelected || !this.isActive() ? CursorScreen.Cursor.DISABLED : CursorScreen.Cursor.POINTER;
    }
}

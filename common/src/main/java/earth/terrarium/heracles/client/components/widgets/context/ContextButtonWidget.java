package earth.terrarium.heracles.client.components.widgets.context;

import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.base.BaseWidget;
import earth.terrarium.heracles.client.utils.UIUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ContextButtonWidget extends BaseWidget {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/context/button.png");
    private static final ResourceLocation DANGER_TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/context/danger_button.png");

    private static final int PADDING = 5;
    private static final int COLOR = 0xAAAAAA;
    private static final int HOVER_COLOR = 0xFFFFFF;
    private static final int DANGER_COLOR = 0xCA3636;
    private static final int DANGER_HOVER_COLOR = 0xFFFFFF;

    private final Component text;
    private final Runnable action;
    private final boolean danger;

    public ContextButtonWidget(Component text, Runnable action, boolean danger) {
        super(Minecraft.getInstance().font.width(text) + PADDING * 2, 16);

        this.text = text;
        this.action = action;
        this.danger = danger;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        if (this.isHovered) {
            ResourceLocation texture = this.danger ? DANGER_TEXTURE : TEXTURE;
            UIUtils.blitWithEdge(graphics, texture, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 4);
        }

        graphics.drawString(
            font, this.text,
            this.getX() + PADDING, this.getY() + (this.getHeight() - 8) / 2,
            color(this.isHovered()), false
        );
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        this.action.run();
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return CursorScreen.Cursor.POINTER;
    }

    public int color(boolean hovered) {
        if (this.danger) {
            return hovered ? DANGER_HOVER_COLOR : DANGER_COLOR;
        } else {
            return hovered ? HOVER_COLOR : COLOR;
        }
    }
}

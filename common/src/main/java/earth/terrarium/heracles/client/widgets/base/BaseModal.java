package earth.terrarium.heracles.client.widgets.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.client.ClientUtils;
import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;

public abstract class BaseModal extends BaseWidget implements TemporyWidget {

    protected final int screenWidth;
    protected final int screenHeight;

    protected final int width;
    protected final int height;

    protected final int x;
    protected final int y;

    protected boolean visible = false;

    public BaseModal(int screenWidth, int screenHeight, int width, int height) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.width = width;
        this.height = height;
        this.x = (screenWidth / 2) - (width / 2);
        this.y = (screenHeight / 2) - (height / 2);
    }

    @Override
    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (visible) {
            this.setFocused(null);
        }
    }

    @Override
    public final void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        if (mouseY > 15) {
            ClientUtils.clearTooltip();
        }

        RenderSystem.disableDepthTest();
        renderBackground(pose, mouseX, mouseY, partialTick);

        RenderUtils.bindTexture(AbstractQuestScreen.HEADING);
        boolean hoveringClose = mouseX >= x + width - 18 && mouseX <= x + width - 7 && mouseY >= y + 5 && mouseY <= y + 16;
        Gui.blit(pose, x + width - 18, y + 5, 11, hoveringClose ? 26 : 15, 11, 11);

        if (hoveringClose) {
            ClientUtils.setTooltip(Component.literal("Close"));
        }
        CursorUtils.setCursor(true, hoveringClose ? CursorScreen.Cursor.POINTER : CursorScreen.Cursor.DEFAULT);

        renderForeground(pose, mouseX, mouseY, partialTick);
        RenderSystem.enableDepthTest();

        if (Minecraft.getInstance().screen instanceof CursorScreen cursorScreen) {
            cursorScreen.setCursor(children());
        }

        CursorUtils.setCursor(mouseY <= 15, CursorScreen.Cursor.DISABLED);
    }

    protected abstract void renderBackground(PoseStack pose, int mouseX, int mouseY, float partialTick);

    protected abstract void renderForeground(PoseStack pose, int mouseX, int mouseY, float partialTick);
}

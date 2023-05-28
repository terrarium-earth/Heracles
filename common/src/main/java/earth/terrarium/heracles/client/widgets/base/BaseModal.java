package earth.terrarium.heracles.client.widgets.base;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.screens.AbstractQuestScreen;
import earth.terrarium.heracles.client.utils.ClientUtils;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.resources.ResourceLocation;

public abstract class BaseModal extends BaseWidget implements TemporyWidget {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/modal.png");

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

        addChild(new ImageButton(this.x + width - 18, this.y + 5, 11, 11, 11, 15, 11, AbstractQuestScreen.HEADING, 256, 256, b ->
            setVisible(false)
        )).setTooltip(Tooltip.create(ConstantComponents.CLOSE));
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
    public final void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!visible) return;
        if (mouseY > 15) {
            ClientUtils.clearTooltip();
        }

        CursorUtils.setCursor(true, CursorScreen.Cursor.DEFAULT);

        RenderSystem.disableDepthTest();
        try (var pose = new CloseablePoseStack(graphics)) {
            pose.translate(0, 0, 150);
            graphics.fill(0, 15, this.screenWidth, this.screenHeight, 0x80000000);
            renderBackground(graphics, mouseX, mouseY, partialTick);

            renderForeground(graphics, mouseX, mouseY, partialTick);
            pose.popPose();
        }
        RenderSystem.enableDepthTest();

        if (Minecraft.getInstance().screen instanceof CursorScreen cursorScreen) {
            cursorScreen.setCursor(children());
        }

        CursorUtils.setCursor(mouseY <= 15, CursorScreen.Cursor.DISABLED);
    }

    protected abstract void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);

    protected abstract void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
}

package earth.terrarium.heracles.client.widgets;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.heracles.Heracles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SelectableTabButton extends AbstractButton {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/list_buttons.png");

    private final Runnable onSelect;
    private boolean selected;

    public SelectableTabButton(int x, int y, int width, int height, Component component, Runnable onSelect) {
        super(x, y, width, height, component);
        this.onSelect = onSelect;
    }

    @Override
    public void renderWidget(PoseStack poseStack, int i, int j, float f) {
        int v = (selected ? 40 : 0) + (this.isHovered() ? 20 : 0);
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShaderTexture(0, TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();
        blitNineSliced(poseStack, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 1, 144, 20, 0, v);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int k = this.active ? 16777215 : 10526880;
        this.renderString(poseStack, minecraft.font, k | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public void onPress() {
        if (!this.selected && this.onSelect != null) {
            this.onSelect.run();
        }
        this.selected = true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}

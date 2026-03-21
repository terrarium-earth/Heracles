package earth.terrarium.olympus.client.ui;

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.Nullable;

public abstract class Overlay extends BaseCursorScreen {

    @Nullable
    protected final Screen background;
    private boolean isInitialized = false;

    protected Overlay(@Nullable Screen background) {
        super(CommonComponents.EMPTY);
        this.background = background;
    }

    @Override
    public void added() {
        super.added();
        if (this.background == null) return;
        this.background.clearFocus();
    }

    @Override
    protected void init() {
        super.init();
        if (!this.isInitialized) {
            this.isInitialized = true;
        }
    }

    @Override
    protected void repositionElements() {
        if (this.background instanceof Overlay overlay) overlay.isInitialized = false;
        if (this.background != null) this.background.resize(this.width, this.height);
        super.repositionElements();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        // We want to close all overlays when the screen is resized
        Screen screenToGoTo = this.background;
        while (screenToGoTo instanceof Overlay overlay) {
            overlay.onClose();
            screenToGoTo = overlay.background;
        }
        Minecraft.getInstance().setScreen(screenToGoTo);
    }

    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (this.background == null) return;
        this.background.renderWithTooltipAndSubtitles(graphics, -1, -1, partialTick);
        graphics.nextStratum();
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.background);
    }

    /**
     * @deprecated This seems really useless
     */
    @Deprecated(forRemoval = true)
    public void renderWidgets(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return this.background != null && this.background.isPauseScreen();
    }
}

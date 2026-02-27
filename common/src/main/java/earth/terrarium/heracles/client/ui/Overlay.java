package earth.terrarium.heracles.client.ui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;

public abstract class Overlay extends BaseCursorScreen {

    protected final Screen background;

    protected Overlay(Screen background) {
        super(CommonComponents.EMPTY);
        this.background = background;
    }

    @Override
    public void added() {
        super.added();
        ComponentPath path = this.background.getCurrentFocusPath();
        if (path == null) return;
        path.applyFocus(false);
    }

    @Override
    protected void repositionElements() {
        this.background.resize(Minecraft.getInstance(), this.width, this.height);
        super.repositionElements();
    }

    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.background.render(graphics, -1, -1, partialTick);
        graphics.flush();
        RenderSystem.clear(256, Minecraft.ON_OSX);
        renderBackground(graphics);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(graphics, mouseX, mouseY, partialTick);
        renderWidgets(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(this.background);
    }

    public void renderWidgets(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

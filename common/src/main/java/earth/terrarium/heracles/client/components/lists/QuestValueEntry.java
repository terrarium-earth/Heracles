package earth.terrarium.heracles.client.components.lists;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.tasks.QuestTask;
import net.minecraft.client.gui.GuiGraphics;

public record QuestValueEntry<T>(T value, DisplayWidget widget) implements BaseListEntry<T> {

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        BaseListEntry.super.render(graphics, scissor, x, y, width, mouseX, mouseY, hovered, partialTicks);
        this.widget.render(graphics, scissor, x, y, width, mouseX, mouseY, hovered, partialTicks);
    }

    @Override
    public int getHeight(int width) {
        return this.widget.getHeight(width);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        return this.widget.mouseClicked(mouseX, mouseY, mouseButton, width);
    }
}

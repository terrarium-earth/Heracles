package earth.terrarium.heracles.api.quests;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.WidgetUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public interface QuestIcon<T extends QuestIcon<T>> {

    void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int height);

    QuestIconType<T> type();

    default boolean isVisible() {
        return false;
    }

    default boolean renderOverride(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int size) {
        if (isVisible()) {
            render(graphics, scissor, x, y, size, size);
            return true;
        }
        return false;
    }

    default void renderOverrideOrStack(ItemStack stack, GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int size, int mouseX, int mouseY) {
        if (isVisible()) {
            render(graphics, scissor, x, y, size, size);
        } else {
            WidgetUtils.drawItemIconWithTooltip(graphics, stack, x, y, size, mouseX, mouseY);
        }
    }

    default void renderOverrideOrStack(ItemStack stack, GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int size) {
        if (isVisible()) {
            render(graphics, scissor, x, y, size, size);
        } else {
            WidgetUtils.drawItemIcon(graphics, stack, x, y, size);
        }
    }
}

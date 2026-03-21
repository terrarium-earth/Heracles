package earth.terrarium.heracles.api.quests;

import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.client.compat.RecipeViewerHelper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public interface QuestIcon<T extends QuestIcon<T>> {

    boolean render(GuiGraphics graphics, int x, int y, int width, int height);

    QuestIconType<T> type();

    default void renderOrStack(ItemStack stack, GuiGraphics graphics, int x, int y, int size, int mouseX, int mouseY) {
        if (!render(graphics, x, y, size, size)) {
            WidgetUtils.drawItemIconWithTooltip(graphics, stack, x, y, size, mouseX, mouseY);
            boolean inBounds = (mouseX >= x && mouseX < x + size) && (mouseY >= y && mouseY < y + size);
            if (inBounds) {
                RecipeViewerHelper.showItem(stack);
            }
        }
    }

    default void renderOrStack(ItemStack stack, GuiGraphics graphics, int x, int y, int size) {
        if (!render(graphics, x, y, size, size)) {
            WidgetUtils.drawItemIcon(graphics, stack, x, y, size);
        }
    }
}

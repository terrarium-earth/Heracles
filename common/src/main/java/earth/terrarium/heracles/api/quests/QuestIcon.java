package earth.terrarium.heracles.api.quests;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.WidgetUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

public interface QuestIcon<T extends QuestIcon<T>> {

    boolean render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int height);

    QuestIconType<T> type();

    default void renderOrStack(ItemStack stack, GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int size, int mouseX, int mouseY) {
        if (!render(graphics, scissor, x, y, size, size)) {
            WidgetUtils.drawItemIconWithTooltip(graphics, stack, x, y, size, mouseX, mouseY);
        }
    }

    default void renderOrStack(ItemStack stack, GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int size) {
        if (!render(graphics, scissor, x, y, size, size)) {
            WidgetUtils.drawItemIcon(graphics, stack, x, y, size);
        }
    }
}

package earth.terrarium.heracles.api.rewards.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.quests.QuestIcon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface BaseItemRewardWidget extends DisplayWidget {

    QuestIcon<?> getIconOverride();

    ItemStack getIcon();

    @Override
    default void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        WidgetUtils.drawBackground(graphics, x, y, width, getHeight(width));
        int iconSize = 32;
        if (!getIconOverride().render(graphics, scissor, x, y, iconSize, iconSize)) {
            WidgetUtils.drawItemIconWithTooltip(graphics, getIcon(), x, y, iconSize, this::getTooltip, mouseX, mouseY);
        }
        graphics.fill(x + iconSize + 9, y + 5, x + iconSize + 10, y + getHeight(width) - 5, 0xFF909090);
    }

    @Override
    default int getHeight(int width) {
        return 42;
    }

    default List<Component> getTooltip() {
        return Screen.getTooltipFromItem(Minecraft.getInstance(), getIcon());
    }
}

package earth.terrarium.heracles.api.rewards.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface BaseItemRewardWidget extends DisplayWidget {

    ItemStack getIcon();

    @Override
    default void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        WidgetUtils.drawBackground(graphics, x, y, width);
        int iconSize = (int) (width * 0.1f);
        ItemStack icon = getIcon();
        WidgetUtils.drawItemIconWithTooltip(graphics, icon, x, y, iconSize, this::getTooltip, mouseX, mouseY);
    }

    @Override
    default int getHeight(int width) {
        return (int) (width * 0.1f) + 10;
    }

    default List<Component> getTooltip() {
        return Screen.getTooltipFromItem(Minecraft.getInstance(), getIcon());
    }
}

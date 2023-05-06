package earth.terrarium.heracles.api.rewards.client.defaults;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

public interface BaseItemRewardWidget extends DisplayWidget {

    ItemStack getIcon();

    @Override
    default void render(PoseStack pose, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        WidgetUtils.drawBackground(pose, x, y, width);
        int iconSize = (int) (width * 0.1f);
        ItemStack icon = getIcon();
        Minecraft.getInstance().getItemRenderer().renderGuiItem(pose, icon, x + 5 + (int) (iconSize / 2f) - 8, y + 5 + (int) (iconSize / 2f) - 8);
    }

    @Override
    default int getHeight(int width) {
        return (int) (width * 0.1f) + 10;
    }
}

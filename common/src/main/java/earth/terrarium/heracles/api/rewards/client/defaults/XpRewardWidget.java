package earth.terrarium.heracles.api.rewards.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.rewards.defaults.XpQuestReward;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public record XpRewardWidget(XpQuestReward reward) implements DisplayWidget {

    private static final String TITLE_SINGULAR = "reward.heracles.xp.title.singular";
    private static final String TITLE_PLURAL = "reward.heracles.xp.title.plural";
    private static final String DESC_SINGULAR = "reward.heracles.xp.desc.singular";
    private static final String DESC_PLURAL = "reward.heracles.xp.desc.plural";

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        WidgetUtils.drawBackground(graphics, x, y, width, getHeight(width));
        int iconSize = 32;
        WidgetUtils.drawItemIcon(graphics, Items.EXPERIENCE_BOTTLE.getDefaultInstance(), x, y, iconSize);
        graphics.fill(x + iconSize + 9, y + 5, x + iconSize + 10, y + getHeight(width) - 5, 0xFF909090);
        String title = this.reward.amount() == 1 ? TITLE_SINGULAR : TITLE_PLURAL;
        String desc = this.reward.amount() == 1 ? DESC_SINGULAR : DESC_PLURAL;
        graphics.drawString(
            font,
            Component.translatable(title, this.reward.amount()), x + iconSize + 16, y + 5, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            Component.translatable(desc, this.reward.amount(), this.reward.xpType().text()), x + iconSize + 16, y + 7 + font.lineHeight, 0xFF808080,
            false
        );
    }

    @Override
    public int getHeight(int width) {
        return 42;
    }
}

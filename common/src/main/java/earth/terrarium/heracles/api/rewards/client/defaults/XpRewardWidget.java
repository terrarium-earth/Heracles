package earth.terrarium.heracles.api.rewards.client.defaults;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.rewards.defaults.XpQuestReward;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public record XpRewardWidget(XpQuestReward reward) implements DisplayWidget {

    private static final String TITLE_SINGULAR = "reward.heracles.xp.title.singular";
    private static final String TITLE_PLURAL = "reward.heracles.xp.title.plural";
    private static final String DESC_SINGULAR = "reward.heracles.xp.desc.singular";
    private static final String DESC_PLURAL = "reward.heracles.xp.desc.plural";

    @Override
    public void render(PoseStack pose, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        WidgetUtils.drawBackground(pose, x, y, width);
        int iconSize = (int) (width * 0.1f);
        Minecraft.getInstance().getItemRenderer().renderGuiItem(
            pose, Items.EXPERIENCE_BOTTLE.getDefaultInstance(), x + 5 + (int) (iconSize / 2f) - 8, y + 5 + (int) (iconSize / 2f) - 8
        );
        String title = this.reward.amount() == 1 ? TITLE_SINGULAR : TITLE_PLURAL;
        String desc = this.reward.amount() == 1 ? DESC_SINGULAR : DESC_PLURAL;
        font.draw(pose, Component.translatable(title, this.reward.xpType().text()), x + iconSize + 10, y + 5, 0xFFFFFFFF);
        font.draw(pose, Component.translatable(desc, this.reward.amount(), this.reward.xpType().text()), x + iconSize + 10, y + 7 + font.lineHeight, 0xFF808080);
    }

    @Override
    public int getHeight(int width) {
        return (int) (width * 0.1f) + 10;
    }
}

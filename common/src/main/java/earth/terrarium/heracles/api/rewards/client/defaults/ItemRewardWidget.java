package earth.terrarium.heracles.api.rewards.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.rewards.defaults.ItemReward;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public record ItemRewardWidget(ItemReward reward) implements BaseItemRewardWidget {

    private static final String TITLE_SINGULAR = "reward.heracles.item.title.singular";
    private static final String TITLE_PLURAL = "reward.heracles.item.title.plural";
    private static final String DESC_SINGULAR = "reward.heracles.item.desc.singular";
    private static final String DESC_PLURAL = "reward.heracles.item.desc.plural";

    @Override
    public QuestIcon<?> getIconOverride() {
        return reward.icon();
    }

    @Override
    public ItemStack getIcon() {
        return this.reward.stack();
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        BaseItemRewardWidget.super.render(graphics, scissor, x, y, width, mouseX, mouseY, hovered, partialTicks);
        String title = getIcon().getCount() == 1 ? TITLE_SINGULAR : TITLE_PLURAL;
        String desc = getIcon().getCount() == 1 ? DESC_SINGULAR : DESC_PLURAL;
        graphics.drawString(
            font,
            reward.titleOr(Component.translatable(title, getIcon().getHoverName())), x + 48, y + 6, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            Component.translatable(desc, getIcon().getCount(), getIcon().getHoverName()), x + 48, y + 8 + font.lineHeight, 0xFF808080,
            false
        );
    }
}

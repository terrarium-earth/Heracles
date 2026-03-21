package earth.terrarium.heracles.api.rewards.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.rewards.defaults.ItemReward;
import earth.terrarium.heracles.client.compat.RecipeViewerHelper;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.quest.AbstractQuestScreen;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.rewards.ClaimRewardsPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public record ItemRewardWidget(ItemReward reward, String quest, QuestProgress progress, boolean isInteractive) implements BaseItemRewardWidget {

    private static final String TITLE_SINGULAR = "reward.heracles.item.title.singular";
    private static final String TITLE_PLURAL = "reward.heracles.item.title.plural";
    private static final String DESC_SINGULAR = "reward.heracles.item.desc.singular";
    private static final String DESC_PLURAL = "reward.heracles.item.desc.plural";
    public static ItemRewardWidget of(ItemReward reward, boolean interactive) {
        if (Minecraft.getInstance().screen instanceof AbstractQuestScreen screen) {
            String id = screen.content().id();
            return new ItemRewardWidget(reward, id, ClientQuests.getProgress(id), interactive);
        }
        return new ItemRewardWidget(reward, "", null, interactive);
    }

    @Override
    public QuestIcon<?> getIconOverride() {
        return reward.icon();
    }

    @Override
    public ItemStack getIcon() {
        return this.reward.stack();
    }

    @Override
    public boolean canClaim() {
        return progress != null && progress.canClaim(reward.id());
    }

    @Override
    public void claimReward() {
        this.progress.claimReward(this.reward.id());
        NetworkHandler.CHANNEL.sendToServer(new ClaimRewardsPacket(this.quest, this.reward.id()));
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        BaseItemRewardWidget.super.render(graphics, scissor, x, y, width, mouseX, mouseY, hovered, partialTicks);
        if (mouseX >= x + 5 && mouseX <= x + 37 && mouseY >= y + 5 && mouseY <= y + 37) {
            RecipeViewerHelper.showItem(reward.stack());
        }
        String title = getIcon().getCount() == 1 ? TITLE_SINGULAR : TITLE_PLURAL;
        String desc = getIcon().getCount() == 1 ? DESC_SINGULAR : DESC_PLURAL;
        graphics.drawString(
            font,
            reward.titleOr(Component.translatable(title, getIcon().getHoverName())), x + 48, y + 6, QuestScreenTheme.getRewardTitle(),
            false
        );
        graphics.drawString(
            font,
            Component.translatable(desc, getIcon().getCount(), getIcon().getHoverName()), x + 48, y + 8 + font.lineHeight, QuestScreenTheme.getRewardDescription(),
            false
        );
    }
}

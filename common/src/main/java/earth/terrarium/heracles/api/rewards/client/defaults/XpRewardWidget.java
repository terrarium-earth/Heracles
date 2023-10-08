package earth.terrarium.heracles.api.rewards.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.rewards.defaults.XpQuestReward;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.quest.BaseQuestScreen;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.rewards.ClaimRewardPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record XpRewardWidget(XpQuestReward reward, String quest, QuestProgress progress) implements BaseItemRewardWidget {

    private static final String TITLE_SINGULAR = "reward.heracles.xp.title.singular";
    private static final String TITLE_PLURAL = "reward.heracles.xp.title.plural";
    private static final String DESC_SINGULAR = "reward.heracles.xp.desc.singular";
    private static final String DESC_PLURAL = "reward.heracles.xp.desc.plural";

    public static XpRewardWidget of(XpQuestReward reward) {
        if (Minecraft.getInstance().screen instanceof BaseQuestScreen screen) {
            return new XpRewardWidget(reward, screen.getQuestId(), ClientQuests.getProgress(screen.getQuestId()));
        }
        return new XpRewardWidget(reward, "", null);
    }

    @Override
    public QuestIcon<?> getIconOverride() {
        return reward.icon();
    }

    @Override
    public ItemStack getIcon() {
        return Items.EXPERIENCE_BOTTLE.getDefaultInstance();
    }

    @Override
    public boolean canClaim() {
        return progress != null && progress.canClaim(reward.id());
    }

    @Override
    public void claimReward() {
        this.progress.claimReward(this.reward.id());
        NetworkHandler.CHANNEL.sendToServer(new ClaimRewardPacket(this.quest, this.reward.id()));
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        BaseItemRewardWidget.super.render(graphics, scissor, x, y, width, mouseX, mouseY, hovered, partialTicks);
        String title = this.reward.amount() == 1 ? TITLE_SINGULAR : TITLE_PLURAL;
        String desc = this.reward.amount() == 1 ? DESC_SINGULAR : DESC_PLURAL;
        graphics.drawString(
            font,
            reward.titleOr(Component.translatable(title, this.reward.amount())), x + 48, y + 6, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            Component.translatable(desc, this.reward.amount(), this.reward.xpType().text()), x + 48, y + 8 + font.lineHeight, 0xFF808080,
            false
        );
    }
}

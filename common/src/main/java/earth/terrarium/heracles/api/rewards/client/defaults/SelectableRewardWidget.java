package earth.terrarium.heracles.api.rewards.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon;
import earth.terrarium.heracles.api.rewards.defaults.SelectableReward;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.quest.BaseQuestScreen;
import earth.terrarium.heracles.client.widgets.base.TemporaryWidget;
import earth.terrarium.heracles.client.widgets.modals.SelectRewardsModal;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.rewards.ClaimSelectableRewardsPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public record SelectableRewardWidget(SelectableReward reward, String quest, QuestProgress progress, boolean isInteractive) implements BaseItemRewardWidget {

    private static final String TITLE_SINGULAR = "reward.heracles.select.title.singular";
    private static final String TITLE_PLURAL = "reward.heracles.select.title.plural";
    private static final String DESC_SINGULAR = "reward.heracles.select.desc.singular";
    private static final String DESC_PLURAL = "reward.heracles.select.desc.plural";

    public static SelectableRewardWidget of(SelectableReward reward, boolean interactive) {
        if (Minecraft.getInstance().screen instanceof BaseQuestScreen screen) {
            return new SelectableRewardWidget(reward, screen.getQuestId(), ClientQuests.getProgress(screen.getQuestId()), interactive);
        }
        return new SelectableRewardWidget(reward, "", null, interactive);
    }

    @Override
    public QuestIcon<?> getIconOverride() {
        return new ItemQuestIcon(Items.AIR);
    }

    @Override
    public ItemStack getIcon() {
        return Items.CHEST.getDefaultInstance();
    }

    @Override
    public boolean canClaim() {
        return progress != null && progress.canClaim(reward.id());
    }

    @Override
    public void claimReward() {
        if (Minecraft.getInstance().screen instanceof BaseQuestScreen screen) {
            boolean found = false;
            SelectRewardsModal widget = new SelectRewardsModal(screen.width, screen.height);
            for (TemporaryWidget temporaryWidget : screen.temporaryWidgets()) {
                if (temporaryWidget instanceof SelectRewardsModal modal) {
                    found = true;
                    widget = modal;
                    break;
                }
            }
            widget.setVisible(true);
            widget.updateRewards(this.reward.rewards().values(), this.reward.amount(), stuff -> {
                this.progress.claimReward(this.reward.id());
                NetworkHandler.CHANNEL.sendToServer(new ClaimSelectableRewardsPacket(this.quest, this.reward.id(), stuff));
            });
            if (!found) {
                screen.addTemporary(widget);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        BaseItemRewardWidget.super.render(graphics, scissor, x, y, width, mouseX, mouseY, hovered, partialTicks);
        String title = this.reward.amount() == 1 ? TITLE_SINGULAR : TITLE_PLURAL;
        String desc = this.reward.amount() == 1 ? DESC_SINGULAR : DESC_PLURAL;
        graphics.drawString(
            font,
            Component.translatable(title), x + 48, y + 6, QuestScreenTheme.getRewardTitle(),
            false
        );
        graphics.drawString(
            font,
            Component.translatable(desc, this.reward.amount()), x + 48, y + 8 + font.lineHeight, QuestScreenTheme.getRewardDescription(),
            false
        );
    }
}

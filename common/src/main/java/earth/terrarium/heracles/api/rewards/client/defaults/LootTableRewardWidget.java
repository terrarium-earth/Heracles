package earth.terrarium.heracles.api.rewards.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.rewards.defaults.LootTableReward;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.quest.AbstractQuestScreen;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.rewards.ClaimRewardsPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public record LootTableRewardWidget(LootTableReward reward, String quest, QuestProgress progress, boolean isInteractive) implements BaseItemRewardWidget {

    private static final Component TITLE_SINGULAR = Component.translatable("reward.heracles.loottable.title.singular");
    private static final String DESC_SINGULAR = "reward.heracles.loottable.desc.singular";
    private static final String TOOLTIP_SINGULAR = "reward.heracles.loottable.tooltip.singular";

    public static LootTableRewardWidget of(LootTableReward reward, boolean interactive) {
        if (Minecraft.getInstance().screen instanceof AbstractQuestScreen screen) {
            String id = screen.content().id();
            return new LootTableRewardWidget(reward, id, ClientQuests.getProgress(id), interactive);
        }
        return new LootTableRewardWidget(reward, "", null, interactive);
    }

    @Override
    public QuestIcon<?> getIconOverride() {
        return reward.icon();
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
        this.progress.claimReward(this.reward.id());
        NetworkHandler.CHANNEL.sendToServer(new ClaimRewardsPacket(this.quest, this.reward.id()));
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        BaseItemRewardWidget.super.render(graphics, scissor, x, y, width, mouseX, mouseY, hovered, partialTicks);
        graphics.drawString(
            font,
             TITLE_SINGULAR, x + 48, y + 6, QuestScreenTheme.getRewardTitle(),
            false
        );
        graphics.drawString(
            font,
            Component.translatable(DESC_SINGULAR, this.reward.lootTable()), x + 48, y + 8 + font.lineHeight, QuestScreenTheme.getRewardDescription(),
            false
        );
    }

    @Override
    public List<Component> getTooltip() {
        return List.of(
            Component.translatable(TOOLTIP_SINGULAR, this.reward.lootTable()).withStyle(ChatFormatting.GREEN));
    }
}

package earth.terrarium.heracles.api.rewards.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.rewards.defaults.CommandReward;
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

public record CommandRewardWidget(CommandReward reward, String quest, QuestProgress progress, boolean isInteractive) implements BaseItemRewardWidget {

    private static final String TITLE_SINGULAR = "reward.heracles.command.title.singular";
    private static final String DESC_SINGULAR = "reward.heracles.command.desc.singular";
    private static final String TOOLTIP_SINGULAR = "reward.heracles.command.tooltip.singular";

    public static CommandRewardWidget of(CommandReward reward, boolean interactive) {
        if (Minecraft.getInstance().screen instanceof AbstractQuestScreen screen) {
            String id = screen.content().id();
            return new CommandRewardWidget(reward, id, ClientQuests.getProgress(id), interactive);
        }
        return new CommandRewardWidget(reward, "", null, interactive);
    }

    @Override
    public QuestIcon<?> getIconOverride() {
        return reward.icon();
    }

    @Override
    public ItemStack getIcon() {
        return new ItemStack(Items.COMMAND_BLOCK);
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
        int firstSpace = reward.command().indexOf(" ");
        String desc = firstSpace > 0 ? reward.command().substring(0, reward.command().indexOf(" ")) : reward.command();
        graphics.drawString(
            font,
            reward.titleOr(Component.translatable(TITLE_SINGULAR)), x + 48, y + 6, QuestScreenTheme.getRewardTitle(),
            false
        );
        graphics.drawString(
            font,
            Component.translatable(DESC_SINGULAR, desc), x + 48, y + 8 + font.lineHeight, QuestScreenTheme.getRewardDescription(),
            false
        );
    }

    @Override
    public List<Component> getTooltip() {
        return List.of(
            Component.translatable(TOOLTIP_SINGULAR, this.reward.command()).withStyle(ChatFormatting.GREEN));
    }
}

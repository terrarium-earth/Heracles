package earth.terrarium.heracles.api.rewards.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.api.rewards.defaults.CommandReward;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.quest.BaseQuestScreen;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.rewards.ClaimRewardPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public record CommandRewardWidget(CommandReward reward, String quest, QuestProgress progress) implements BaseItemRewardWidget {

    private static final String TITLE_SINGULAR = "reward.heracles.command.title.singular";
    private static final String DESC_SINGULAR = "reward.heracles.command.desc.singular";
    private static final String TOOLTIP_SINGULAR = "reward.heracles.command.tooltip.singular";

    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation LOOTBAG_TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/item/lootbag.png");

    public static CommandRewardWidget of(CommandReward reward) {
        if (Minecraft.getInstance().screen instanceof BaseQuestScreen screen) {
            return new CommandRewardWidget(reward, screen.getQuestId(), ClientQuests.getProgress(screen.getQuestId()));
        }
        return new CommandRewardWidget(reward, "", null);
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
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        BaseItemRewardWidget.super.render(graphics, scissor, x, y, width, mouseX, mouseY, hovered, partialTicks);
        int firstSpace = reward.command().indexOf(" ");
        String desc = firstSpace > 0 ? reward.command().substring(0, reward.command().indexOf(" ")) : reward.command();
        graphics.drawString(
            font,
            reward.titleOr(Component.translatable(TITLE_SINGULAR)), x + 48, y + 6, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            Component.translatable(DESC_SINGULAR, desc), x + 48, y + 8 + font.lineHeight, 0xFF808080,
            false
        );
        int buttonY = y + 11;
        boolean buttonHovered = mouseX > x + width - 30 && mouseX < x + width - 10 && mouseY > buttonY && mouseY < buttonY + 20;
        int v = progress == null || !progress.canClaim(reward.id()) ? 46 : buttonHovered ? 86 : 66;
        graphics.blitNineSliced(BUTTON_TEXTURE, x + width - 30, buttonY, 20, 20, 3, 3, 3, 3, 200, 20, 0, v);
        graphics.blit(LOOTBAG_TEXTURE, x + width - 30 + 2, buttonY + 2, 0, 0, 16, 16, 16, 16);
        if (buttonHovered) {
            CursorUtils.setCursor(true, progress != null && !progress.isComplete() ? CursorScreen.Cursor.POINTER : CursorScreen.Cursor.DISABLED);
            ScreenUtils.setTooltip(ConstantComponents.Tasks.CHECK);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        int buttonY = 11;
        boolean buttonHovered = mouseX > width - 30 && mouseX < width - 10 && mouseY > buttonY && mouseY < buttonY + 20;
        if (buttonHovered && progress != null && progress.canClaim(reward.id())) {
            this.progress.claimReward(this.reward.id());
            NetworkHandler.CHANNEL.sendToServer(new ClaimRewardPacket(this.quest, this.reward.id()));
            return true;
        }
        return false;
    }

    @Override
    public List<Component> getTooltip() {
        return List.of(
            Component.translatable(TOOLTIP_SINGULAR, this.reward.command()).withStyle(ChatFormatting.GREEN));
    }
}

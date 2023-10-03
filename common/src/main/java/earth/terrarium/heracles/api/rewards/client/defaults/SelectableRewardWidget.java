package earth.terrarium.heracles.api.rewards.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.rewards.defaults.SelectableReward;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.quest.BaseQuestScreen;
import earth.terrarium.heracles.client.widgets.base.TemporyWidget;
import earth.terrarium.heracles.client.widgets.modals.SelectRewardsModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.rewards.ClaimSelectableRewardsPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

public record SelectableRewardWidget(SelectableReward reward, String quest,
                                     QuestProgress progress) implements DisplayWidget {

    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation LOOTBAG_TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/item/lootbag.png");

    private static final String TITLE_SINGULAR = "reward.heracles.select.title.singular";
    private static final String TITLE_PLURAL = "reward.heracles.select.title.plural";
    private static final String DESC_SINGULAR = "reward.heracles.select.desc.singular";
    private static final String DESC_PLURAL = "reward.heracles.select.desc.plural";

    public static SelectableRewardWidget of(SelectableReward reward) {
        if (Minecraft.getInstance().screen instanceof BaseQuestScreen screen) {
            return new SelectableRewardWidget(reward, screen.getQuestId(), ClientQuests.getProgress(screen.getQuestId()));
        }
        return new SelectableRewardWidget(reward, "", null);
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        WidgetUtils.drawBackground(graphics, x, y, width, getHeight(width));
        int iconSize = 32;
        WidgetUtils.drawItemIcon(graphics, Items.CHEST.getDefaultInstance(), x, y, iconSize);
        String title = this.reward.amount() == 1 ? TITLE_SINGULAR : TITLE_PLURAL;
        String desc = this.reward.amount() == 1 ? DESC_SINGULAR : DESC_PLURAL;
        graphics.drawString(
            font,
            Component.translatable(title), x + iconSize + 16, y + 5, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            Component.translatable(desc, this.reward.amount()), x + iconSize + 16, y + 7 + font.lineHeight, 0xFF808080,
            false
        );

        int buttonY = y + (iconSize - 10) / 2;
        boolean buttonHovered = mouseX > x + width - 30 && mouseX < x + width - 10 && mouseY > buttonY && mouseY < buttonY + 20;
        int v = progress == null || !progress.canClaim(reward.id()) ? 46 : buttonHovered ? 86 : 66;
        graphics.blitNineSliced(BUTTON_TEXTURE, x + width - 30, buttonY, 20, 20, 3, 3, 3, 3, 200, 20, 0, v);

        graphics.blit(LOOTBAG_TEXTURE, x + width - 30 + 2, buttonY + 2, 0, 0, 16, 16, 16, 16);

        if (buttonHovered) {
            CursorUtils.setCursor(true, progress != null && progress.canClaim(reward.id()) ? CursorScreen.Cursor.POINTER : CursorScreen.Cursor.DISABLED);
            ScreenUtils.setTooltip(ConstantComponents.Rewards.SELECT_CLAIM);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        int buttonY = ((int) (width * 0.1f) - 10) / 2;
        boolean buttonHovered = mouseX > width - 30 && mouseX < width - 10 && mouseY > buttonY && mouseY < buttonY + 20;
        if (buttonHovered && progress != null && progress.canClaim(reward.id())) {
            if (Minecraft.getInstance().screen instanceof BaseQuestScreen screen) {
                boolean found = false;
                SelectRewardsModal widget = new SelectRewardsModal(screen.width, screen.height);
                for (TemporyWidget temporaryWidget : screen.temporaryWidgets()) {
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
            return true;
        }
        return false;
    }

    @Override
    public int getHeight(int width) {
        return 42;
    }
}

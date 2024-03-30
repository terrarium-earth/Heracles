package earth.terrarium.heracles.api.rewards.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.ItemDisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public interface BaseItemRewardWidget extends ItemDisplayWidget {
    ResourceLocation BUTTON_TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/buttons.png");
    ResourceLocation LOOTBAG_TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/item/lootbag.png");

    QuestIcon<?> getIconOverride();

    ItemStack getIcon();

    boolean canClaim();

    void claimReward();

    boolean isInteractive();

    @Override
    default ItemStack getCurrentItem() {
        return getIcon();
    }

    @Override
    default void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        int iconSize = 32;
        if (!getIconOverride().render(graphics, x + 5, y + 5, iconSize, iconSize)) {
            WidgetUtils.drawItemIconWithTooltip(graphics, getIcon(), x + 5, y + 5, iconSize, this::getTooltip, mouseX, mouseY);
        }
        if (isInteractive()) {
            int buttonY = y + 11;
            boolean buttonHovered = mouseX > x + width - 30 && mouseX < x + width - 10 && mouseY > buttonY && mouseY < buttonY + 20;
            int v = canClaim() ? (buttonHovered ? 40 : 20) : 0;
            graphics.blitNineSliced(BUTTON_TEXTURE, x + width - 30, buttonY, 20, 20, 3, 200, 20, 0, v);
            graphics.blit(LOOTBAG_TEXTURE, x + width - 30 + 2, buttonY + 2, 0, 0, 16, 16, 16, 16);
            if (buttonHovered) {
                CursorUtils.setCursor(true, canClaim() ? CursorScreen.Cursor.POINTER : CursorScreen.Cursor.DISABLED);
                if (canClaim()) ScreenUtils.setTooltip(ConstantComponents.Rewards.SELECT_CLAIM);
            }
        }
    }

    @Override
    default boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        if (ItemDisplayWidget.super.mouseClicked(mouseX, mouseY, mouseButton, width)) return true;
        if (isInteractive()) {
            int buttonY = 11;
            boolean buttonHovered = mouseX > width - 30 && mouseX < width - 10 && mouseY > buttonY && mouseY < buttonY + 20;
            if (buttonHovered && canClaim()) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                claimReward();
                return true;
            }
        }
        return false;
    }

    @Override
    default int getHeight(int width) {
        return 42;
    }

    default List<Component> getTooltip() {
        return Screen.getTooltipFromItem(Minecraft.getInstance(), getIcon());
    }
}

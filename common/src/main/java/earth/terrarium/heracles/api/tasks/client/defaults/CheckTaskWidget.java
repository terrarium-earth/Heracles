package earth.terrarium.heracles.api.tasks.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter;
import earth.terrarium.heracles.api.tasks.defaults.CheckTask;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.tasks.CheckTaskPacket;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.NumericTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;

public record CheckTaskWidget(
    String questId, CheckTask task, TaskProgress<NumericTag> progress, ModUtils.QuestStatus status
) implements DisplayWidget {

    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/buttons.png");
    private static final ResourceLocation CHECK_TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/item/check.png");

    private static final String DESC_SINGULAR = "task.heracles.check.desc.singular";

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        int iconSize = 32;
        if (!task.icon().render(graphics, x + 5, y + 5, iconSize, iconSize)) {
            graphics.blit(CHECK_TEXTURE, x + 5, y + 5, 0, 0, 32, 32, 32, 32);
        }
        graphics.drawString(
            font,
            task.titleOr(TaskTitleFormatter.create(task)), x + iconSize + 16, y + 6, QuestScreenTheme.getTaskTitle(),
            false
        );
        graphics.drawString(
            font,
            Component.translatable(DESC_SINGULAR), x + iconSize + 16, y + 8 + font.lineHeight, QuestScreenTheme.getTaskDescription(),
            false
        );

        int buttonY = y + 11;
        boolean buttonHovered = mouseX > x + width - 30 && mouseX < x + width - 10 && mouseY > buttonY && mouseY < buttonY + 20;
        int v = isCompletable() ? (buttonHovered ? 40 : 20) : 0;
        graphics.blitNineSliced(BUTTON_TEXTURE, x + width - 30, buttonY, 20, 20, 3, 200, 20, 0, v);

        graphics.blit(CHECK_TEXTURE, x + width - 30 + 2, buttonY + 2, 0, 0, 16, 16, 16, 16);

        if (buttonHovered) {
            CursorUtils.setCursor(true, isCompletable() ? CursorScreen.Cursor.POINTER : CursorScreen.Cursor.DISABLED);
            if (isCompletable()) ScreenUtils.setTooltip(ConstantComponents.Tasks.CHECK);
        }
    }

    private boolean isCompletable() {
        return status != ModUtils.QuestStatus.LOCKED && progress != null && !progress.isComplete();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        int buttonY = 11;
        boolean buttonHovered = mouseX > width - 30 && mouseX < width - 10 && mouseY > buttonY && mouseY < buttonY + 20;
        if (buttonHovered && isCompletable()) {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            NetworkHandler.CHANNEL.sendToServer(new CheckTaskPacket(this.questId, this.task.id()));
            return true;
        }
        return false;
    }

    @Override
    public int getHeight(int width) {
        return 42;
    }
}

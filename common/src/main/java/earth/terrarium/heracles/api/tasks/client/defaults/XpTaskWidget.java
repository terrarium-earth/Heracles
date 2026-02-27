package earth.terrarium.heracles.api.tasks.client.defaults;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.api.tasks.CollectionType;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter;
import earth.terrarium.heracles.api.tasks.defaults.XpTask;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.tasks.ManualXpTaskPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.NumericTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;

public record XpTaskWidget(
    String quest, XpTask task, TaskProgress<NumericTag> progress
) implements DisplayWidget {

    private static final String DESC_SINGULAR = "task.heracles.xp.desc.singular";
    private static final String DESC_PLURAL = "task.heracles.xp.desc.plural";
    private static final String DESC_SUBMIT_SINGULAR = "task.heracles.xp.desc.submit.singular";
    private static final String DESC_SUBMIT_PLURAL = "task.heracles.xp.desc.submit.plural";

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        int iconSize = 32;
        this.task.icon().renderOrStack(Items.EXPERIENCE_BOTTLE.getDefaultInstance(), graphics, x + 5, y + 5, iconSize);

        String desc = task.collectionType() == CollectionType.AUTOMATIC ? (this.task.target() == 1 ? DESC_SINGULAR : DESC_PLURAL) : (this.task.target() == 1 ? DESC_SUBMIT_SINGULAR : DESC_SUBMIT_PLURAL);

        graphics.drawString(
            font,
            task.titleOr(TaskTitleFormatter.create(this.task)), x + iconSize + 16, y + 6, QuestScreenTheme.getTaskTitle(),
            false
        );
        graphics.drawString(
            font,
            Component.translatable(desc, this.task.target(), this.task.xpType().text()), x + iconSize + 16, y + 8 + font.lineHeight, QuestScreenTheme.getTaskDescription(),
            false
        );
        WidgetUtils.drawProgressText(graphics, x, y, width, this.task, this.progress);

        int height = getHeight(width);
        WidgetUtils.drawProgressBar(graphics, x + iconSize + 16, y + height - font.lineHeight - 5, x + width - 5, y + height - 6, this.task, this.progress);

        if (task.collectionType() == CollectionType.MANUAL) {
            int buttonY = y + height - font.lineHeight - 16;
            int buttonWidth = font.width(ConstantComponents.Tasks.SUBMIT_XP);
            boolean buttonHovered = mouseX > x + width - 5 - buttonWidth && mouseX < x + width - 5 && mouseY > buttonY && mouseY < buttonY + font.lineHeight;

            Component text = buttonHovered ? ConstantComponents.Tasks.SUBMIT_XP.copy().withStyle(ChatFormatting.UNDERLINE) : ConstantComponents.Tasks.SUBMIT_XP;
            graphics.drawString(font, text, x + width - 5 - buttonWidth, buttonY, QuestScreenTheme.getTaskSubmit(this.progress.isComplete()), false);
            CursorUtils.setCursor(buttonHovered, this.progress.isComplete() ? CursorScreen.Cursor.DISABLED : CursorScreen.Cursor.POINTER);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        if (task.collectionType() == CollectionType.MANUAL && !progress.isComplete()) {
            Font font = Minecraft.getInstance().font;
            int buttonY = getHeight(width) - font.lineHeight - 16;
            int buttonWidth = font.width(ConstantComponents.Tasks.SUBMIT_XP);
            boolean buttonHovered = mouseX > width - 5 - buttonWidth && mouseX < width - 5 && mouseY > buttonY && mouseY < buttonY + font.lineHeight;
            if (buttonHovered) {
                NetworkHandler.CHANNEL.sendToServer(new ManualXpTaskPacket(this.quest, this.task.id()));

                if (Minecraft.getInstance().player != null) {
                    this.progress.addProgress(XpTask.TYPE, task, Pair.of(Minecraft.getInstance().player, XpTask.Cause.MANUALLY_COMPLETED));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public int getHeight(int width) {
        return 42;
    }
}

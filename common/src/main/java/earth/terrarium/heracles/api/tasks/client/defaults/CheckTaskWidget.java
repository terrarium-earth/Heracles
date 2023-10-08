package earth.terrarium.heracles.api.tasks.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter;
import earth.terrarium.heracles.api.tasks.defaults.CheckTask;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.tasks.CheckTaskPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.ByteTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record CheckTaskWidget(
    String questId, CheckTask task, TaskProgress<ByteTag> progress
) implements DisplayWidget {

    private static final ResourceLocation BUTTON_TEXTURE = new ResourceLocation("textures/gui/widgets.png");
    private static final ResourceLocation CHECK_TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/item/check.png");

    private static final String DESC_SINGULAR = "task.heracles.check.desc.singular";

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        WidgetUtils.drawBackground(graphics, x, y, width, getHeight(width));
        int iconSize = 32;
        if (!task.icon().render(graphics, scissor, x + 5, y + 5, iconSize, iconSize)) {
            graphics.blit(CHECK_TEXTURE, x + 5, y + 5, 0, 0, 32, 32, 32, 32);
        }
        graphics.fill(x + iconSize + 9, y + 5, x + iconSize + 10, y + getHeight(width) - 5, 0xFF909090);
        graphics.drawString(
            font,
            task.titleOr(TaskTitleFormatter.create(task)), x + iconSize + 16, y + 6, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            Component.translatable(DESC_SINGULAR), x + iconSize + 16, y + 8 + font.lineHeight, 0xFF808080,
            false
        );

        int buttonY = y + 11;
        boolean buttonHovered = mouseX > x + width - 30 && mouseX < x + width - 10 && mouseY > buttonY && mouseY < buttonY + 20;
        int v = progress == null || progress.isComplete() ? 46 : buttonHovered ? 86 : 66;
        graphics.blitNineSliced(BUTTON_TEXTURE, x + width - 30, buttonY, 20, 20, 3, 3, 3, 3, 200, 20, 0, v);

        graphics.blit(CHECK_TEXTURE, x + width - 30 + 2, buttonY + 2, 0, 0, 16, 16, 16, 16);

        if (buttonHovered) {
            CursorUtils.setCursor(true, progress != null && !progress.isComplete() ? CursorScreen.Cursor.POINTER : CursorScreen.Cursor.DISABLED);
            if (progress != null && !progress.isComplete()) ScreenUtils.setTooltip(ConstantComponents.Tasks.CHECK);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        int buttonY = 11;
        boolean buttonHovered = mouseX > width - 30 && mouseX < width - 10 && mouseY > buttonY && mouseY < buttonY + 20;
        if (buttonHovered && progress != null && !progress.isComplete()) {
            this.progress.setComplete(true);
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

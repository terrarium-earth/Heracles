package earth.terrarium.heracles.client.components.quest;

import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import earth.terrarium.heracles.client.components.base.BaseWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.FormattedCharSequence;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class QuestError extends BaseWidget {

    private final String error;
    private Component tooltip;

    public QuestError(int width, int height, Throwable error) {
        this(width, height, error.getMessage());

        MutableComponent component = Component.empty();

        if (error.getCause() == null) return;
        AtomicInteger counter = new AtomicInteger(0);
        ExceptionUtils.getStackTrace(error.getCause()).lines().forEach(line -> {
            if (counter.getAndIncrement() > 10) return;
            String cleanedLine = line.replaceFirst("\tat ", "    at ");
            component.append(Component.literal(cleanedLine + "\n").withStyle(ChatFormatting.GRAY));
        });
        withTooltip(component);
    }

    public QuestError(int width, int height, String message) {
        super(width, height);
        this.error = message;
    }

    public QuestError withTooltip(Component tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        int x = this.getX() + this.width / 2;
        int y = this.getY() + this.height / 5;

        int textWidth = font.width(this.error);

        graphics.drawString(font, this.error, x - textWidth / 2, y, 0xFF5555, false);

        if (this.tooltip == null) return;

        graphics.flush();
        graphics.pose().pushPose();
        graphics.pose().scale(0.5f, 0.5f, 0);
        List<FormattedCharSequence> lines = font.split(this.tooltip, this.width * 2);
        y *= 2;
        y += font.lineHeight * 4;

        for (FormattedCharSequence line : lines) {
            graphics.drawString(font, line, x, y, 0xFFFFFF, false);
            y += font.lineHeight * 2;
        }

        graphics.pose().popPose();
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return CursorScreen.Cursor.DISABLED;
    }
}

package earth.terrarium.heracles.client.toasts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

public class WrappingHintToast implements Toast {
    Component title;
    List<FormattedCharSequence> lines;
    List<FormattedCharSequence> hints;
    private final long duration;

    public WrappingHintToast(Component title, List<Component> lines, List<Component> hints, long duration) {
        this.title = title;
        this.lines = wrap(lines);
        this.hints = wrap(hints);
        this.duration = duration;
    }

    @Override
    public Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long timeSinceLastVisible) {
        int lineHeight = Math.max(lines.size(), hints.size());

        graphics.blit(TEXTURE, 0, 0, 0, 0, width(), 16);
        for (int i = 0; i < lineHeight - 1; i++) {
            graphics.blit(TEXTURE, 0, 16 + 11 * i, 0, 8, width(), 11);
        }
        graphics.blit(TEXTURE, 0, 16 + 11 * (lineHeight - 1), 0, 16, width(), 16);

        graphics.drawString(
            toastComponent.getMinecraft().font,
            title, 32, 7, 0xFF800080,
            false
        );

        double time = duration * toastComponent.getNotificationDisplayTimeMultiplier();

        List<FormattedCharSequence> description = timeSinceLastVisible >= (time / 2) && !hints.isEmpty() ? hints : lines;

        for (int i = 0; i < description.size(); i++) {
            graphics.drawString(toastComponent.getMinecraft().font, description.get(i), 32, 18 + i * 11, 0xFFFFFF, false);
        }

        return timeSinceLastVisible >= time ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    private List<FormattedCharSequence> wrap(List<Component> messages) {
        List<FormattedCharSequence> list = new ArrayList<>();
        messages.forEach(text -> list.addAll(Minecraft.getInstance().font.split(text, width() - 40)));
        return list;
    }

    @Override
    public int height() {
        return 32 + Math.max(0, lines.size() - 1) * 11;
    }
}

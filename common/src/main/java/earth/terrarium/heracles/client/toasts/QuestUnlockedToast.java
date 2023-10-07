package earth.terrarium.heracles.client.toasts;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public record QuestUnlockedToast(Quest quest) implements Toast {
    private static final long DISPLAY_TIME = 5000L;
    private static final Component TITLE_TEXT = Component.translatable("quest_unlocked.heracles.toast");

    @Override
    @NotNull
    public Toast.Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long timeSinceLastVisible) {
        graphics.blit(TEXTURE, 0, 0, 0, 0, width(), height());
        graphics.drawString(
            toastComponent.getMinecraft().font,
            TITLE_TEXT, 32, 7, 0xFF800080,
            false
        );

        double time = DISPLAY_TIME * toastComponent.getNotificationDisplayTimeMultiplier();

        Component text = timeSinceLastVisible >= (time / 2) ?
            Component.translatable("quest.heracles.toast.desc", Component.keybind("key.heracles.open_quests")) :
            quest.display().title();

        graphics.drawString(
            toastComponent.getMinecraft().font,
            text, 32, 18, 0xFFFFFFFF,
            false
        );

        quest.display().icon().render(graphics, new ScissorBoxStack(), 8, 8, height() / 2, height() / 2);

        return timeSinceLastVisible >= DISPLAY_TIME * toastComponent.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
    }

    public static void add(ToastComponent toastComponent, String quest) {
        ClientQuests.get(quest).ifPresent(entry ->
            toastComponent.addToast(new QuestUnlockedToast(entry.value()))
        );
    }
}

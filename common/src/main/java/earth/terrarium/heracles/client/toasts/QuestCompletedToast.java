package earth.terrarium.heracles.client.toasts;

import earth.terrarium.heracles.api.client.theme.ToastsTheme;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestIcon;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class QuestCompletedToast extends WrappingHintToast implements Toast {
    private static final Component TITLE_TEXT = Component.translatable("quest.heracles.toast");
    private static final Component KEY_HINT = Component.translatable("quest.heracles.toast.desc", Component.keybind("key.heracles.open_quests")
        .withStyle(style -> style.withBold(true).withColor(ToastsTheme.getKeybinding())));
    private final QuestIcon<?> icon;

    public QuestCompletedToast(Quest quest) {
        super(TITLE_TEXT, List.of(quest.display().title()), List.of(KEY_HINT),5000L);
        this.icon = quest.display().icon();
    }

    @Override
    @NotNull
    public Toast.Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long timeSinceLastVisible) {
        Toast.Visibility visible = super.render(graphics, toastComponent, timeSinceLastVisible);
        icon.render(graphics, 8, height() / 2 - 8, 16, 16);
        return visible;
    }

    public static void add(ToastComponent toastComponent, String quest) {
        ClientQuests.get(quest).ifPresent(entry ->
            toastComponent.addToast(new QuestCompletedToast(entry.value()))
        );
    }
}

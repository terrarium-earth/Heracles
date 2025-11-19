package earth.terrarium.heracles.client.toasts;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
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
    private static final Component TITLE_TEXT_PROVISIONAL = Component.translatable("quest.heracles.toast.provisional");
    private static final Component KEY_HINT_PROVISIONAL = Component.translatable("quest.heracles.toast.provisional.desc");
    private final QuestIcon<?> icon;

    public QuestCompletedToast(Quest quest, boolean provisional) {
        super(
            provisional ? TITLE_TEXT_PROVISIONAL : TITLE_TEXT,
            List.of(quest.display().title()),
            List.of(provisional ? KEY_HINT_PROVISIONAL : KEY_HINT),
            5000L);
        this.icon = quest.display().icon();
    }

    @Override
    @NotNull
    public Toast.Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long timeSinceLastVisible) {
        Toast.Visibility visible = super.render(graphics, toastComponent, timeSinceLastVisible);
        icon.render(graphics, new ScissorBoxStack(), 8, height() / 2 - 8, 16, 16);
        return visible;
    }

    public static void add(ToastComponent toastComponent, String quest, boolean provisional) {
        ClientQuests.get(quest).ifPresent(entry ->
            toastComponent.addToast(new QuestCompletedToast(entry.value(), provisional))
        );
    }
}

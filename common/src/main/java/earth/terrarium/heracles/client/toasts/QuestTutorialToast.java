package earth.terrarium.heracles.client.toasts;

import earth.terrarium.heracles.api.client.theme.ToastsTheme;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.handlers.QuestTutorial;
import earth.terrarium.heracles.common.regisitries.ModItems;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class QuestTutorialToast implements Toast {
    private static final Component TITLE_TEXT = Component.translatable("quest.heracles.tutorial.title");

    @Override
    @NotNull
    public Toast.Visibility render(GuiGraphics graphics, ToastComponent toastComponent, long timeSinceLastVisible) {
        graphics.blit(TEXTURE, 0, 0, 0, 96, this.width(), this.height());
        graphics.drawString(
            toastComponent.getMinecraft().font,
            TITLE_TEXT, 32, 7, ToastsTheme.getTutorialTitle(),
            false
        );
        graphics.drawString(
            toastComponent.getMinecraft().font,
            Component.translatable(
                "quest.heracles.tutorial.desc", Component.keybind("key.heracles.open_quests")
                    .withStyle(style -> style.withBold(true).withColor(ToastsTheme.getKeybinding()))
            ), 32, 18, ToastsTheme.getTutorialContent(),
            false
        );

        graphics.renderFakeItem(Objects.requireNonNullElse(ModItems.QUEST_BOOK.get(), Items.KNOWLEDGE_BOOK).getDefaultInstance(), 8, 8);

        return DisplayConfig.showTutorial && QuestTutorial.toast == this ? Visibility.SHOW : Visibility.HIDE;
    }
}
package earth.terrarium.heracles.client.toasts;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.api.quests.Quest;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public record QuestCompletedToast(Quest quest) implements Toast {
    private static final long DISPLAY_TIME = 5000L;
    private static final Component TITLE_TEXT = Component.translatable("quest.heracles.toast");

    @Override
    @NotNull
    public Toast.Visibility render(PoseStack poseStack, ToastComponent toastComponent, long timeSinceLastVisible) {
        RenderUtils.bindTexture(TEXTURE);

        GuiComponent.blit(poseStack, 0, 0, 0, 0, width(), height());
        toastComponent.getMinecraft().font.draw(poseStack, TITLE_TEXT, 32.0F, 7.0F, 0xFF800080);

        double time = DISPLAY_TIME * toastComponent.getNotificationDisplayTimeMultiplier();

        Component text = timeSinceLastVisible >= (time / 2) ?
            Component.translatable("quest.heracles.toast.desc", Component.keybind("key.heracles.open_quests")) :
            quest.display().title();

        toastComponent.getMinecraft().font.draw(poseStack, text, 32.0F, 18.0F, 0xFFFFFFFF);

        quest.display().icon().render(poseStack, new ScissorBoxStack(), 0, 0, height(), height());

        return timeSinceLastVisible >= DISPLAY_TIME * toastComponent.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    public static void add(ToastComponent toastComponent, Quest quest) {
        toastComponent.addToast(new QuestCompletedToast(quest));
    }
}

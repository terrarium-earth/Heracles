package earth.terrarium.heracles.client.handlers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.common.regisitries.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.GameType;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class QuestTutorial {

    private static QuestTutorialToast toast;
    private static String tutorialText = """
        <h2>Tutorial</h2>
        <p>Quests are a way to guide you through the progression. They are a way to help you learn about things and the world around you.</p>
        <p>Quests are split into groups. Each group has a set of quests that you can complete. You can complete quests in any order you want, but some quests may require you to complete other quests first.</p>
        <hr></hr>
        <hint color="gray" icon="heracles:quest_book" title="Note:">
            <p>You can change this file in the tutorial.html file in the Heracles config folder.</p>
        </hint>
        <hr></hr>
        <p>Happy Questing!</p>
        """;

    public static void tick() {
        if (Minecraft.getInstance().level == null) return;
        var gameMode = Minecraft.getInstance().gameMode;
        if (gameMode != null && gameMode.getPlayerMode() == GameType.SURVIVAL) {
            boolean hasQuests = ClientQuests.entries().size() > 0;
            if (DisplayConfig.showTutorial && toast == null && hasQuests) {
                showTutorial();
            }
        }
    }

    public static void showTutorial() {
        QuestTutorial.toast = new QuestTutorialToast();
        ToastComponent toastComponent = Minecraft.getInstance().getToasts();
        toastComponent.addToast(toast);
    }

    public static void load(Path path) {
        Path heraclesPath = path.resolve(Heracles.MOD_ID);
        File displayFile = heraclesPath.resolve("tutorial.html").toFile();
        try {
            Files.createDirectories(heraclesPath);
            if (displayFile.exists()) {
                QuestTutorial.tutorialText = FileUtils.readFileToString(displayFile, StandardCharsets.UTF_8);
            } else {
                try {
                    FileUtils.write(displayFile, tutorialText, StandardCharsets.UTF_8);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String tutorialText() {
        return tutorialText;
    }

    public static class QuestTutorialToast implements Toast {
        private static final Component TITLE_TEXT = Component.translatable("quest.heracles.tutorial.title");

        @Override
        @NotNull
        public Toast.Visibility render(PoseStack poseStack, ToastComponent toastComponent, long timeSinceLastVisible) {
            RenderUtils.bindTexture(TEXTURE);

            GuiComponent.blit(poseStack, 0, 0, 0, 96, this.width(), this.height());
            toastComponent.getMinecraft().font.draw(poseStack, TITLE_TEXT, 32.0F, 7.0F, 0xFF404040);
            toastComponent.getMinecraft().font.draw(poseStack,
                Component.translatable("quest.heracles.tutorial.desc",
                    Component.keybind("key.heracles.open_quests")
                        .withStyle(style -> style.withBold(true).withColor(0xFFA0A0A0))),
                32.0F, 18.0F, 0xFF808080);

            toastComponent.getMinecraft().getItemRenderer().renderAndDecorateFakeItem(poseStack, ModItems.QUEST_BOOK.get().getDefaultInstance(), 8, 8);

            return DisplayConfig.showTutorial ? Visibility.SHOW : Visibility.HIDE;
        }
    }
}

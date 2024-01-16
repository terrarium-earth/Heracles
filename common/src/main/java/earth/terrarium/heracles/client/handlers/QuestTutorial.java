package earth.terrarium.heracles.client.handlers;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.toasts.QuestTutorialToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.world.level.GameType;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class QuestTutorial {
    public static QuestTutorialToast toast;
    private static String tutorialText = """
        <h2>Tutorial</h2>
        <p>Quests are a way to guide you through the progression. They are a way to help you learn about things and the world around you.</p>
        <p>Quests are split into groups. Each group has a set of quests that you can complete. You can complete quests in any order you want, but some quests may require you to complete other quests first.</p>
        <hr/>
        <hint color="gray" icon="heracles:quest_book" title="Note:">
            <p>You can change this file in the tutorial.html file in the Heracles config folder.</p>
        </hint>
        <hr/>
        <p>Happy Questing!</p>
        """;

    public static void tick() {
        if (Minecraft.getInstance().level == null) return;
        var gameMode = Minecraft.getInstance().gameMode;
        if (gameMode != null && gameMode.getPlayerMode() == GameType.SURVIVAL) {
            boolean hasQuests = !ClientQuests.entries().isEmpty();
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
                    Heracles.LOGGER.error("Failed to write tutorial", e);
                }
            }
        } catch (Exception e) {
            Heracles.LOGGER.error("Failed to load tutorial", e);
        }
    }

    public static String tutorialText() {
        return tutorialText;
    }
}

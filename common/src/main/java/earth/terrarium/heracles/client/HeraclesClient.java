package earth.terrarium.heracles.client;

import com.mojang.blaze3d.platform.InputConstants;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.data.ThemeHandler;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.handlers.QuestTutorial;
import earth.terrarium.heracles.client.screens.QuestTutorialScreen;
import earth.terrarium.heracles.client.toasts.QuestClaimedToast;
import earth.terrarium.heracles.client.toasts.QuestCompletedToast;
import earth.terrarium.heracles.client.toasts.QuestUnlockedToast;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.groups.OpenGroupPacket;
import earth.terrarium.hermes.api.rendering.HtmlBlockquoteStyleConfig;
import earth.terrarium.hermes.impl.HermesStyle;
import earth.terrarium.hermes.libs.minemark.providers.DefaultImageProvider;
import earth.terrarium.hermes.libs.minemark.style.*;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.item.Item;

import java.awt.*;
import java.util.List;
import java.util.function.BiConsumer;

public class HeraclesClient {

    public static final KeyMapping OPEN_QUESTS = new KeyMapping(
        "key.heracles.open_quests",
        InputConstants.KEY_U,
        "key.categories.odyssey"
    );

    public static String lastGroup = "";

    public static void init() {
        Heracles.setRegistryAccess(() -> {
            var connection = Minecraft.getInstance().getConnection();
            if (connection == null) return RegistryAccess.EMPTY;
            return connection.registryAccess();
        });
    }

    public static void clientTick() {
        if (OPEN_QUESTS.consumeClick()) {
            openQuestScreen();
        }
        QuestTutorial.tick();
    }

    public static void openQuestScreen() {
        if (!ClientQuests.groups().contains(lastGroup)) {
            lastGroup = "";
        }
        if (DisplayConfig.showTutorial) {
            if (!QuestTutorial.tutorialText().isBlank()) {
                Minecraft.getInstance().setScreen(new QuestTutorialScreen());
            } else {
                DisplayConfig.showTutorial = false;
                DisplayConfig.save();
                NetworkHandler.CHANNEL.sendToServer(new OpenGroupPacket(lastGroup, false));
            }
        } else {
            NetworkHandler.CHANNEL.sendToServer(new OpenGroupPacket(lastGroup, false));
        }
    }

    public static void displayItemsRewardedToast(String id, List<Item> items) {
        QuestClaimedToast.addOrUpdate(Minecraft.getInstance().getToasts(), id, items);
    }

    public static void displayQuestCompleteToast(String id) {
        QuestCompletedToast.add(Minecraft.getInstance().getToasts(), id);
    }

    public static void displayQuestUnlockedToast(String id) {
        QuestUnlockedToast.add(Minecraft.getInstance().getToasts(), id);
    }

    public static void initReloadListeners(BiConsumer<ResourceLocation, PreparableReloadListener> init) {
        init.accept(ResourceLocation.fromNamespaceAndPath(Heracles.MOD_ID, "theme"), ThemeHandler.INSTANCE);
    }

    private static HermesStyle getDefaultStyle() {
        return new HermesStyle(
            new TextStyleConfig(1f, Color.WHITE, 1f),
            new ParagraphStyleConfig(1f),
            Color.BLUE,
            new HeadingStyleConfig(
                new HeadingLevelStyleConfig(1.6f, 4f),
                new HeadingLevelStyleConfig(1.4f, 4f),
                new HeadingLevelStyleConfig(1.2f, 4f),
                new HeadingLevelStyleConfig(1.0f, 4f),
                new HeadingLevelStyleConfig(0.8f, 4f),
                new HeadingLevelStyleConfig(0.6f, 4f)
            ),
            new HorizontalRuleStyleConfig(1f, 1f, Color.WHITE),
            new ImageStyleConfig(DefaultImageProvider.INSTANCE),
            new ListStyleConfig(1f, 1f),
            new HtmlBlockquoteStyleConfig(1f, 1f, 1f, 1f, Color.DARK_GRAY, Color.LIGHT_GRAY),
            new CodeBlockStyleConfig(1f, 1f, 1f, 1f, Color.BLACK),
            new TableStyleConfig(1f, 1f, 1f, Color.BLACK, Color.LIGHT_GRAY, Color.DARK_GRAY)
        );
    }

    public static HermesStyle getCurrentStyle() {
        // TODO Custom styles
        return getDefaultStyle();
    }
}

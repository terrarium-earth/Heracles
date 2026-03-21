package earth.terrarium.olympus.client.ui.modals;

import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.constants.MinecraftColors;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.ui.UITexts;
import earth.terrarium.olympus.client.utils.OlympusUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Util;

public class Modals {

    private static void closeScreen() {
        Screen screen = Minecraft.getInstance().screen;
        if (screen == null) return;
        screen.onClose();
    }

    public static ActionModal.Builder action() {
        return ActionModal.builder();
    }

    public static ActionModal.Builder delete(Component title, Component description, Runnable action) {
        return ActionModal.builder()
                .withTitle(title)
                .withContent(description)
                .withAction(Widgets.button()
                        .withRenderer(WidgetRenderers.text(UITexts.CANCEL))
                        .withSize(80, 24)
                        .withCallback(Modals::closeScreen)
                )
                .withAction(Widgets.button()
                        .withRenderer(WidgetRenderers.text(UITexts.DELETE).withColor(MinecraftColors.WHITE))
                        .withSize(80, 24)
                        .withTexture(UIConstants.DANGER_BUTTON)
                        .withCallback(() -> {
                            action.run();
                            closeScreen();
                        })
                );
    }

    public static ActionModal.Builder link(String link) {
        var displayLink = OlympusUtils.removePrefix(link, "https://", "http://");
        var description = Component.empty()
                .append(UITexts.OPEN_DESC)
                .append("\n\n")
                .append(Component.literal(displayLink).withStyle(ChatFormatting.BLUE));

        return ActionModal.builder()
                .withTitle(UITexts.OPEN_LINK)
                .withContent(description)
                .withAction(Widgets.button()
                        .withRenderer(WidgetRenderers.text(UITexts.CANCEL))
                        .withSize(80, 24)
                        .withCallback(Modals::closeScreen)
                )
                .withAction(Widgets.button()
                        .withRenderer(WidgetRenderers.text(UITexts.OPEN).withColor(MinecraftColors.WHITE))
                        .withSize(80, 24)
                        .withTexture(UIConstants.PRIMARY_BUTTON)
                        .withCallback(() -> {
                            Util.getPlatform().openUri(link);
                            closeScreen();
                        })
                );
    }
}

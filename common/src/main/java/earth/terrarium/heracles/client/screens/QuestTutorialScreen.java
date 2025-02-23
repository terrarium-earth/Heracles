package earth.terrarium.heracles.client.screens;

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import dev.dediamondpro.minemark.LayoutStyle;
import dev.dediamondpro.minemark.elements.MineMarkElement;
import earth.terrarium.heracles.client.HeraclesClient;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.handlers.QuestTutorial;
import earth.terrarium.heracles.client.widgets.buttons.ThemedButton;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.groups.OpenGroupPacket;
import earth.terrarium.hermes.HermesWidget;
import earth.terrarium.hermes.api.rendering.HtmlRenderer;
import earth.terrarium.hermes.api.rendering.HtmlStyle;
import earth.terrarium.hermes.elements.Parser;
import earth.terrarium.hermes.elements.html.HtmlParagraph;
import earth.terrarium.hermes.impl.HermesStyle;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestTutorialScreen extends BaseCursorScreen {

    private HermesWidget document;
    private boolean saved = false;

    public QuestTutorialScreen() {
        super(CommonComponents.EMPTY);
        DisplayConfig.showTutorial = false;
    }

    @Override
    protected void init() {
        super.init();
        int widgetWidth = (int) ((this.width * AbstractQuestScreen.QUEST_CONTENT_PORTION) + 0.5f);
        int widgetOffset = (int) (((this.width - widgetWidth) / 2f) + 0.5f);
        @Nullable MineMarkElement<HtmlStyle, HtmlRenderer> parsedElement;
        HermesStyle style = HeraclesClient.getCurrentStyle();

        try {
            parsedElement = new Parser(style).parse(QuestTutorial.tutorialText());
        } catch (Exception e) {
            var layout = new LayoutStyle(LayoutStyle.Alignment.CENTER, style.getTextStyle().getDefaultFontSize(), style.getTextStyle().getDefaultTextColor(), false, false, false, false, false, false, false, new HashMap<>());

            String errorBuilder = "Error parsing tutorial text\n" + e.getMessage();

            parsedElement = new MineMarkElement<>(style, layout, null);

            new HtmlParagraph(errorBuilder, style, layout, parsedElement, "p", null);
        }

        this.document = addRenderableOnly(new HermesWidget(widgetOffset, 0, widgetWidth, height - 30, parsedElement));
        int buttonX = (this.width - 150) / 2;
        addRenderableWidget(ThemedButton.builder(ConstantComponents.Quests.VIEW, button -> {
            DisplayConfig.save();
            saved = true;
            NetworkHandler.CHANNEL.sendToServer(new OpenGroupPacket("", false));
        }).bounds(buttonX, this.height - 30, 150, 20).build());
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int i, int j, float f) {
        this.renderBackground(graphics, i, j, f);
        super.render(graphics, i, j, f);
    }

    @Override
    public void removed() {
        super.removed();
        if (!saved) {
            DisplayConfig.showTutorial = true;
            QuestTutorial.showTutorial();
        }
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        List<GuiEventListener> children = new ArrayList<>();
        children.add(this.document);
        children.addAll(super.children());
        return children;
    }
}

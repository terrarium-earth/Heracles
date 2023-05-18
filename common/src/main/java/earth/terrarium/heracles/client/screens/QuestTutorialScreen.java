package earth.terrarium.heracles.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.handlers.QuestTutorial;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.OpenGroupPacket;
import earth.terrarium.hermes.api.DefaultTagProvider;
import earth.terrarium.hermes.api.TagElement;
import earth.terrarium.hermes.api.defaults.ParagraphTagElement;
import earth.terrarium.hermes.api.themes.DefaultTheme;
import earth.terrarium.hermes.client.DocumentWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class QuestTutorialScreen extends BaseCursorScreen {

    private DocumentWidget document;
    private boolean saved = false;

    public QuestTutorialScreen() {
        super(CommonComponents.EMPTY);
        DisplayConfig.showTutorial = false;
    }

    @Override
    protected void init() {
        super.init();
        int widgetWidth = this.width / 2;
        List<TagElement> elements;
        try {
            elements = new DefaultTagProvider().parse(QuestTutorial.tutorialText());
        } catch (Exception e) {
            elements = new ArrayList<>();
            var e1 = new ParagraphTagElement(Map.of());
            e1.setContent("Error parsing tutorial text: " + e.getMessage());
            elements.add(e1);
        }
        this.document = addRenderableOnly(new DocumentWidget(widgetWidth / 2, 20, widgetWidth, height - 60, new DefaultTheme(), elements));
        int buttonX = (this.width - 150) / 2;
        addRenderableWidget(Button.builder(Component.literal("View Quests"), button -> {
            DisplayConfig.save();
            saved = true;
            NetworkHandler.CHANNEL.sendToServer(new OpenGroupPacket("", false));
        }).bounds(buttonX, this.height - 30, 150, 20).build());
    }

    @Override
    public void render(@NotNull PoseStack stack, int i, int j, float f) {
        this.renderDirtBackground(stack);
        super.render(stack, i, j, f);
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

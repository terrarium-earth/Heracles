package earth.terrarium.heracles.client.ui.quest;

import com.teamresourceful.resourcefullib.client.components.CursorWidget;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.HeraclesClient;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.components.quest.QuestError;
import earth.terrarium.heracles.client.components.quest.editor.parser.MarkdownBodyParser;import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.hermes.HermesWidget;
import earth.terrarium.hermes.api.rendering.HtmlRenderer;
import earth.terrarium.hermes.api.rendering.HtmlStyle;
import earth.terrarium.hermes.elements.Parser;
import earth.terrarium.hermes.libs.minemark.elements.MineMarkElement;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;

import java.util.concurrent.atomic.AtomicInteger;

public class DescriptionQuestScreen extends AbstractQuestScreen {

    private static final double OVERSCROLL = 5.0D;

    public DescriptionQuestScreen(Screen parent, QuestContent content) {
        super(parent, content, QuestTab.OVERVIEW);
    }

    @Override
    protected GridLayout initContent(AtomicInteger row) {
        GridLayout layout = super.initContent(row);

        Quest quest = quest();

        AbstractWidget widget;
        if (quest == null) {
            widget = new QuestError(this.contentWidth, this.contentHeight, "Quest not found");
        } else {
            try {
                String desc = String.join("", MarkdownBodyParser.parse(this.quest().display().description()));
                MineMarkElement<HtmlStyle, HtmlRenderer> parsed = new Parser(HeraclesClient.getCurrentStyle()).parse(desc);
                widget = new QuestDocument(this.contentWidth, this.contentHeight, parsed);
            } catch (Throwable e) {
                Heracles.LOGGER.error("Error parsing quest description: ", e);
                widget = new QuestError(this.contentWidth, this.contentHeight, e);
            }
        }
        layout.addChild(widget, row.getAndIncrement(), 0);
        return layout;
    }

    private static class QuestDocument extends HermesWidget implements CursorWidget {

        public QuestDocument(int width, int height, MineMarkElement<HtmlStyle, HtmlRenderer> parsed) {
            super(0, 0, width, height, parsed);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.DEFAULT;
        }
    }
}
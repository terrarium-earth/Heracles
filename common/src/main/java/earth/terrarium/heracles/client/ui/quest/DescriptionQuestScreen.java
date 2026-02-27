package earth.terrarium.heracles.client.ui.quest;

import com.teamresourceful.resourcefullib.client.components.CursorWidget;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.components.quest.QuestError;
import earth.terrarium.heracles.client.components.quest.editor.parser.MarkdownBodyParser;
import earth.terrarium.heracles.client.components.quest.QuestTagProvider;
import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.hermes.api.TagElement;
import earth.terrarium.hermes.api.TagProvider;
import earth.terrarium.hermes.api.themes.DefaultTheme;
import earth.terrarium.hermes.client.DocumentWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;

import java.util.List;
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
                TagProvider provider = new QuestTagProvider(quest, this.content.id());
                List<String> description = MarkdownBodyParser.parse(quest.display().description());
                List<TagElement> tags = provider.parse(String.join("", description));
                widget = new QuestDocument(this.contentWidth, this.contentHeight, OVERSCROLL, tags);
            } catch (Throwable e) {
                Heracles.LOGGER.error("Error parsing quest description: ", e);
                widget = new QuestError(this.contentWidth, this.contentHeight, e);
            }
        }
        layout.addChild(widget, row.getAndIncrement(), 0);
        return layout;
    }

    private static class QuestDocument extends DocumentWidget implements CursorWidget {

        public QuestDocument(int width, int height, double overscroll, List<TagElement> elements) {
            super(0, 0, width, height, overscroll, overscroll, new DefaultTheme(), elements);
        }

        @Override
        public Cursor getCursor() {
            return Cursor.DEFAULT;
        }
    }
}

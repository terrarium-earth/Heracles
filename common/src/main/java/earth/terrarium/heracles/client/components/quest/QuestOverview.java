package earth.terrarium.heracles.client.components.quest;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestDisplay;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.components.base.BaseWidget;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.utils.UIUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class QuestOverview extends BaseWidget {

    private static final int PADDING = 5;
    private static final int ICON_SIZE = 16;
    private static final int MAX_LINES = 3;

    private final Font font = Minecraft.getInstance().font;

    private final Quest quest;
    private final List<FormattedCharSequence> lines;

    public QuestOverview(int width, String id) {
        super(width, 0);
        this.quest = ClientQuests.getQuest(id).orElse(null);
        this.lines = this.quest == null ? List.of() : UIUtils.splitText(font, quest.display().subtitle(), width - 2 * PADDING, MAX_LINES);
        this.height = ICON_SIZE + 3 * PADDING + font.lineHeight * lines.size();

        if (this.quest == null) return;
        setTooltip(Tooltip.create(this.quest.display().subtitle()));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ScissorBoxStack stack = new ScissorBoxStack();

        UIUtils.blitWithEdge(graphics, UIConstants.OVERVIEW, getX(), getY(), this.width, this.height, 2);

        if (this.quest == null) return;

        QuestDisplay display = this.quest.display();

        display.icon().render(graphics, getX() + PADDING, getY() + PADDING, ICON_SIZE, ICON_SIZE);

        UIUtils.drawText(
            graphics, this.font, this.font.split(display.title(), this.width - 3 * PADDING - ICON_SIZE),
            getX() + 2 * PADDING + ICON_SIZE, getY() + PADDING,
            QuestScreenTheme.getSummaryTitle(), false
        );

        UIUtils.drawText(
            graphics, this.font, this.lines,
            getX() + PADDING, getY() + PADDING + 16 + PADDING,
            QuestScreenTheme.getSummaryDescription(), false
        );
    }
}

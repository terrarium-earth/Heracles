package earth.terrarium.heracles.client.ui.quest.editng;

import com.mojang.blaze3d.platform.InputConstants;
import earth.terrarium.heracles.client.components.quest.editor.MarkdownTextBox;
import earth.terrarium.heracles.client.components.quest.editor.QuestTextEditor;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.ui.quest.AbstractQuestScreen;
import earth.terrarium.heracles.client.utils.UIUtils;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class EditDescriptionQuestScreen extends AbstractQuestScreen {

    private MarkdownTextBox description;

    public EditDescriptionQuestScreen(Screen parent, QuestContent content) {
        super(parent, content, QuestTab.OVERVIEW);
    }

    @Override
    protected GridLayout initContent(AtomicInteger row) {
        GridLayout layout = super.initContent(row);

        boolean wasLoaded = this.description != null;

        this.description = QuestTextEditor.init(this.entry(), this.description, layout, row, this.contentWidth - QuestTextEditor.PADDING * 2, this.contentHeight - QuestTextEditor.PADDING * 2);
        if (!wasLoaded) {
            this.description.setValue(String.join("\n", this.quest().display().description()).replace("§", "&&"));
        }

        return layout;
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        super.renderBackground(graphics);

        int halfPadding = QuestTextEditor.PADDING / 2;

        UIUtils.blitWithEdge(
            graphics, UIConstants.MODAL_HEADER,
            this.description.getX() - halfPadding, HEADER_HEIGHT + SPACER + halfPadding,
            this.description.getWidth() + halfPadding * 2, QuestTextEditor.BUTTON_SIZE + halfPadding * 2,
            3
        );

        UIUtils.blitWithEdge(
            graphics, UIConstants.MODAL,
            this.description.getX() - halfPadding + 2, this.description.getY() - halfPadding,
            this.description.getWidth() + halfPadding * 2 - 4, this.description.getHeight() + halfPadding,
            3
        );
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Screen.hasControlDown() && keyCode == InputConstants.KEY_S) {
            save();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void removed() {
        super.removed();
        save();
    }

    public void save() {
        ClientQuests.updateQuest(
            entry(),
            quest -> NetworkQuestData.builder().description(List.of(this.description.getValue().split("\n"))),
            false
        );
    }
}

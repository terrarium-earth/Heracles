package earth.terrarium.heracles.client.ui.quests;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.components.AlignedLayout;
import earth.terrarium.heracles.client.components.quests.QuestActionHandler;
import earth.terrarium.heracles.client.components.widgets.buttons.SpriteButton;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.UIColors;
import earth.terrarium.heracles.client.ui.UIComponents;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import net.minecraft.Optionull;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.concurrent.atomic.AtomicInteger;

public class EditQuestsScreen extends AbstractQuestsScreen {

    private final EditActionHandler handler;

    public EditQuestsScreen(Screen parent, QuestsContent content) {
        super(parent, content);

        this.handler = new EditActionHandler(() -> this.quests, content);
    }

    @Override
    protected GridLayout initHeader(AtomicInteger column) {
        GridLayout header = super.initHeader(column);

        GridLayout leftButtons = new GridLayout();

        leftButtons.addChild(
            SpriteButton.create(11, 11, UIConstants.EDIT, this::edit)
                .withTooltip(ConstantComponents.TOGGLE_EDIT),
            0, 0,
            leftButtons.newCellSettings().padding(1)
        );

        GridLayout rightButtons = new GridLayout();

        rightButtons.addChild(
            SpriteButton.create(11, 11, UIConstants.EDIT, this::edit)
                .withTooltip(ConstantComponents.TOGGLE_EDIT),
            0, 0,
            rightButtons.newCellSettings().padding(1)
        );

        rightButtons.addChild(
            SpriteButton.create(11, 11, UIConstants.CLOSE, this::onClose)
                .withTooltip(ConstantComponents.CLOSE),
            0, 1,
            rightButtons.newCellSettings().padding(1)
        );

        int half = this.contentWidth / 2;

        header.addChild(
            AlignedLayout.leftAlign(half, HEADER_HEIGHT, leftButtons),
            0, column.getAndIncrement()
        );

        header.addChild(
            AlignedLayout.rightAlign(this.contentWidth - half, HEADER_HEIGHT, rightButtons),
            0, column.getAndIncrement()
        );
        return header;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float f) {
        super.render(graphics, mouseX, mouseY, f);

        int left = this.quests.getX() + 5;
        int right = this.quests.getX() + this.quests.getWidth() - 5;
        int bottom = this.quests.getY() + this.quests.getHeight();

        Quest quest = Optionull.map(this.handler.getSelected(), ClientQuests.QuestEntry::value);
        if (quest == null) return;
        Vector2i position = quest.display().position(this.content.group());

        Component x = Component.translatable(UIComponents.X, position.x);
        Component y = Component.translatable(UIComponents.Y, position.y);

        //left
        int width = Math.max(font.width(x) + 10, font.width(y) + 10);
        boolean isNear = mouseX >= left - 5 && mouseX <= left + width + 5 && mouseY >= bottom - 25 && mouseY <= bottom;
        int color = isNear ? UIColors.QUESTS_COORDINATES | 0x90000000 : UIColors.QUESTS_COORDINATES;

        graphics.drawString(font, x, left, bottom - 20, color, false);
        graphics.drawString(font, y, left, bottom - 10, color, false);

        //right
        width = font.width(this.handler.getSelected().key()) + 10;
        left = right - (width - 10);
        isNear = mouseX >= left - 5 && mouseX <= left + width + 5 && mouseY >= bottom - 15 && mouseY <= bottom;
        color = isNear ? UIColors.QUESTS_COORDINATES | 0x90000000 : UIColors.QUESTS_COORDINATES;

        graphics.drawString(font, this.handler.getSelected().key(), left, bottom - 10, color, false);
    }

    @Override
    protected QuestActionHandler handler() {
        return handler;
    }

}

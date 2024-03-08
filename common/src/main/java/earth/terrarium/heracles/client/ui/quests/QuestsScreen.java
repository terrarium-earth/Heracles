package earth.terrarium.heracles.client.ui.quests;

import earth.terrarium.heracles.client.components.AlignedLayout;
import earth.terrarium.heracles.client.components.quests.QuestActionHandler;
import earth.terrarium.heracles.client.components.quests.QuestWidget;
import earth.terrarium.heracles.client.components.widgets.buttons.SpriteButton;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.OpenQuestPacket;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class QuestsScreen extends AbstractQuestsScreen {

    public QuestsScreen(Screen parent, QuestsContent content) {
        super(parent, content);
    }

    @Override
    protected GridLayout initHeader(AtomicInteger column) {
        GridLayout header = super.initHeader(column);

        GridLayout buttons = new GridLayout();
        AtomicInteger buttonColumn = new AtomicInteger();

        if (QuestTab.canEdit()) {
            buttons.addChild(
                SpriteButton.create(11, 11, UIConstants.EDIT, this::edit)
                    .withTooltip(ConstantComponents.TOGGLE_EDIT),
                0, buttonColumn.getAndIncrement(),
                buttons.newCellSettings().padding(1)
            );
        }

        buttons.addChild(
            SpriteButton.create(11, 11, UIConstants.CLOSE, this::onClose)
                .withTooltip(ConstantComponents.CLOSE),
            0, buttonColumn.getAndIncrement(),
            buttons.newCellSettings().padding(1)
        );

        header.addChild(
            AlignedLayout.rightAlign(this.contentWidth, HEADER_HEIGHT, buttons),
            0, column.getAndIncrement()
        );
        return header;
    }

    @Override
    protected QuestActionHandler handler() {
        return new QuestActionHandler() {
            @Override
            public boolean onLeftClick(double mouseX, double mouseY, @Nullable QuestWidget widget) {
                if (widget == null) return false;
                ClientQuests.QuestEntry entry = widget.entry();
                NetworkHandler.CHANNEL.sendToServer(new OpenQuestPacket(content.group(), entry.key()));
                return true;
            }
        };
    }
}

package earth.terrarium.heracles.client.ui.quests;

import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.heracles.client.components.quests.QuestActionHandler;
import earth.terrarium.heracles.client.components.quests.QuestWidget;
import earth.terrarium.heracles.client.components.widgets.buttons.SpriteButton;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.OpenQuestPacket;
import earth.terrarium.heracles.common.network.packets.quests.data.NetworkQuestData;
import earth.terrarium.heracles.common.utils.ModUtils;
import earth.terrarium.olympus.client.layouts.Layouts;
import earth.terrarium.olympus.client.layouts.LinearViewLayout;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.concurrent.atomic.AtomicInteger;

public class QuestsScreen extends AbstractQuestsScreen {

    private final QuestActionHandler handler;

    public QuestsScreen(Screen parent, QuestsContent content) {
        super(parent, content);
        this.handler = new QuestActionHandler() {

            private ClientQuests.QuestEntry selected;
            private long lastClick;

            @Override
            public boolean onLeftClick(double mouseX, double mouseY, @Nullable QuestWidget widget) {
                if (widget == null) {
                    quests.select(ModUtils.predicateFalse());
                    return false;
                }
                ClientQuests.QuestEntry entry = widget.entry();
                if (entry.equals(selected) && System.currentTimeMillis() - lastClick < 500) {
                    open();
                } else {
                    selected = entry;
                    lastClick = System.currentTimeMillis();
                    quests.select(questWidget -> questWidget == widget);
                }

                return true;
            }

            private void open() {
                if (this.selected == null) return;
                NetworkHandler.CHANNEL.sendToServer(new OpenQuestPacket(content.group(), this.selected.key()));
                this.selected = null;
                this.lastClick = 0;
            }

        };
    }

    @Override
    protected GridLayout initHeader(AtomicInteger column) {
        GridLayout header = super.initHeader(column);

        LinearViewLayout buttons = Layouts.row().withGap(2);

        if (QuestTab.canEdit()) {
            buttons.withChild(
                SpriteButton.create(11, 11, UIConstants.EDIT, this::edit)
                    .withTooltip(ConstantComponents.TOGGLE_EDIT)
            );
        }

        buttons.withChild(
            SpriteButton.create(11, 11, UIConstants.CLOSE, this::onClose)
                .withTooltip(ConstantComponents.CLOSE)
        );

        FrameLayout rightAligned = new FrameLayout();
        rightAligned.setMinWidth(this.contentWidth);
        rightAligned.setMinHeight(HEADER_HEIGHT);
        rightAligned.addChild(buttons, settings -> settings.alignHorizontallyRight().alignVerticallyMiddle());

        header.addChild(
            rightAligned,
            0, column.getAndIncrement()
        );

        return header;
    }

    @Override
    protected QuestActionHandler handler() {
        return this.handler;
    }
}

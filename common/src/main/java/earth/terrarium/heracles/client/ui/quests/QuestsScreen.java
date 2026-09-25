package earth.terrarium.heracles.client.ui.quests;

import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.heracles.client.components.quests.QuestActionHandler;
import earth.terrarium.heracles.client.components.quests.QuestWidget;
import earth.terrarium.heracles.client.components.quests.QuestsMinimap;
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
import net.minecraft.client.gui.GuiGraphics;
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

        LinearViewLayout leftButtons = Layouts.row().withGap(2);

        leftButtons.withChild(new SpriteButton(11, 11, UIConstants.SHOW_GRID) {
            @Override
            public void onPress() {
                DisplayConfig.showGrid = !DisplayConfig.showGrid;
                DisplayConfig.save();
            }

            @Override
            public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
                var sprite = this.sprites.get(this.isHovered() || DisplayConfig.showGrid, !this.active);
                graphics.blit(sprite, getX(), getY(), 0, 0, this.width, this.height, this.width, this.height);
            }
        }.withTooltip(ConstantComponents.Quests.SHOW_GRID));

        leftButtons.withChild(new SpriteButton(11, 11, UIConstants.TOGGLE_MINIMAP) {
            @Override
            public void onPress() {
                DisplayConfig.showMinimap = !DisplayConfig.showMinimap;
                DisplayConfig.save();
                init();
            }

            @Override
            public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
                var sprite = this.sprites.get(this.isHovered() || DisplayConfig.showMinimap, !this.active);
                graphics.blit(sprite, getX(), getY(), 0, 0, this.width, this.height, this.width, this.height);
            }
        }.withTooltip(ConstantComponents.Quests.TOGGLE_MINIMAP));

        leftButtons.withChild(new SpriteButton(11, 11, UIConstants.TOGGLE_MINIMAP_DOCKED) {
            @Override
            public void onPress() {
                DisplayConfig.dockMinimap = !DisplayConfig.dockMinimap;
                DisplayConfig.save();
                init();
            }

            @Override
            public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
                var sprite = this.sprites.get(this.isHovered() || DisplayConfig.dockMinimap, !this.active);
                graphics.blit(sprite, getX(), getY(), 0, 0, this.width, this.height, this.width, this.height);
            }
        }.withTooltip(DisplayConfig.dockMinimap ? ConstantComponents.Quests.UNDOCK_MINIMAP : ConstantComponents.Quests.DOCK_MINIMAP));

        LinearViewLayout rightButtons = Layouts.row().withGap(2);

        if (QuestTab.canEdit()) {
            rightButtons.withChild(
                SpriteButton.create(11, 11, UIConstants.EDIT, this::edit)
                    .withTooltip(ConstantComponents.TOGGLE_EDIT)
            );
        }

        rightButtons.withChild(
            SpriteButton.create(11, 11, UIConstants.CLOSE, this::onClose)
                .withTooltip(ConstantComponents.CLOSE)
        );

        int half = this.contentWidth / 2;

        FrameLayout leftAligned = new FrameLayout();
        leftAligned.setMinWidth(half);
        leftAligned.setMinHeight(HEADER_HEIGHT);
        leftAligned.addChild(leftButtons, settings -> settings.alignHorizontallyLeft().alignVerticallyMiddle());

        FrameLayout rightAligned = new FrameLayout();
        rightAligned.setMinWidth(this.contentWidth - half);
        rightAligned.setMinHeight(HEADER_HEIGHT);
        rightAligned.addChild(rightButtons, settings -> settings.alignHorizontallyRight().alignVerticallyMiddle());

        header.addChild(
            leftAligned,
            0, column.getAndIncrement()
        );

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

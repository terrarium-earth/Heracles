package earth.terrarium.heracles.client.ui.quests;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.components.quests.QuestActionHandler;
import earth.terrarium.heracles.client.components.quests.QuestsMinimap;
import earth.terrarium.heracles.client.components.quests.QuestsWidget;
import earth.terrarium.heracles.client.components.widgets.buttons.SpriteButton;
import earth.terrarium.heracles.client.handlers.ClientQuestNetworking;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.ui.UIColors;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.ui.modals.CreateQuestModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.quests.OpenQuestPacket;
import net.minecraft.Optionull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.navigation.ScreenAxis;
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

        SpriteButton showGridButton = new SpriteButton(11, 11, UIConstants.SHOW_GRID) {
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
        };

        showGridButton.withTooltip(ConstantComponents.Quests.SHOW_GRID);

        leftButtons.addChild(showGridButton, 0, 1, leftButtons.newCellSettings().padding(1));

        SpriteButton snapToGridButton = new SpriteButton(11, 11, UIConstants.SNAP_TO_GRID) {
            @Override
            public void onPress() {
                DisplayConfig.snapToGrid = !DisplayConfig.snapToGrid;
            }

            @Override
            public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
                var sprite = this.sprites.get(this.isHovered() || DisplayConfig.snapToGrid, !this.active);
                graphics.blit(sprite, getX(), getY(), 0, 0, this.width, this.height, this.width, this.height);
            }
        };

        snapToGridButton.withTooltip(ConstantComponents.Quests.SNAP_TO_GRID);

        leftButtons.addChild(snapToGridButton, 0, 2, leftButtons.newCellSettings().padding(1));

        SpriteButton toggleMinimapButton = new SpriteButton(11, 11, UIConstants.TOGGLE_MINIMAP) {
            @Override
            public void onPress() {
                DisplayConfig.showMinimap = !DisplayConfig.showMinimap;
                DisplayConfig.save();
            }

            @Override
            public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
                var sprite = this.sprites.get(this.isHovered() || DisplayConfig.showMinimap, !this.active);
                graphics.blit(sprite, getX(), getY(), 0, 0, this.width, this.height, this.width, this.height);
            }
        };

        toggleMinimapButton.withTooltip(ConstantComponents.Quests.TOGGLE_MINIMAP);

        leftButtons.addChild(toggleMinimapButton, 0, 3, leftButtons.newCellSettings().padding(1));

        SpriteButton dockMinimapButton = new SpriteButton(11, 11, UIConstants.TOGGLE_MINIMAP_DOCKED) {
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
        };

        dockMinimapButton.withTooltip(DisplayConfig.dockMinimap ? ConstantComponents.Quests.UNDOCK_MINIMAP : ConstantComponents.Quests.DOCK_MINIMAP);

        leftButtons.addChild(dockMinimapButton, 0, 4, leftButtons.newCellSettings().padding(1));

        GridLayout rightButtons = new GridLayout();

        rightButtons.addChild(
            SpriteButton.create(11, 11, UIConstants.ADD, this::add)
                .withTooltip(ConstantComponents.Quests.CREATE),
            0, 0,
            rightButtons.newCellSettings().padding(1)
        );

        rightButtons.addChild(
            SpriteButton.create(11, 11, UIConstants.EDIT, this::edit)
                .withTooltip(ConstantComponents.TOGGLE_EDIT),
            0, 1,
            rightButtons.newCellSettings().padding(1)
        );

        rightButtons.addChild(
            SpriteButton.create(11, 11, UIConstants.CLOSE, this::onClose)
                .withTooltip(ConstantComponents.CLOSE),
            0, 2,
            rightButtons.newCellSettings().padding(1)
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
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float f) {
        super.render(graphics, mouseX, mouseY, f);

        int left = this.quests.getX() + 5;
        int right = this.quests.getX() + this.quests.getWidth() - 5;
        int bottom = this.quests.getY() + this.quests.getHeight();

        Quest quest = Optionull.map(this.handler.getSelected(), ClientQuests.QuestEntry::value);
        if (quest == null) return;
        Vector2i position = quest.display().position(this.content.group());

        Component x = Component.translatable("gui.heracles.x", position.x);
        Component y = Component.translatable("gui.heracles.y", position.y);

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

    private void add() {
        Minecraft.getInstance().tell(() -> CreateQuestModal.open((id, name) -> {
            QuestsWidget quests = this.quests;
            Vector2i local = quests.toLocal(quests.getRectangle().getCenterInAxis(ScreenAxis.HORIZONTAL), quests.getRectangle().getCenterInAxis(ScreenAxis.VERTICAL));
            Quest quest = Quest.of(this.content.group(), name, local.sub(12, 12));
            ClientQuestNetworking.add(id, quest);
            NetworkHandler.CHANNEL.sendToServer(new OpenQuestPacket(content.group(), id));
        }));
    }

    @Override
    public QuestActionHandler handler() {
        return handler;
    }

}

package earth.terrarium.heracles.client.ui.quests;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import earth.terrarium.heracles.client.components.base.ListWidget;
import earth.terrarium.heracles.client.components.quests.QuestActionHandler;
import earth.terrarium.heracles.client.components.quests.QuestWidget;
import earth.terrarium.heracles.client.components.quests.QuestsWidget;
import earth.terrarium.heracles.client.components.string.TextWidget;
import earth.terrarium.heracles.client.components.widgets.buttons.SpriteButton;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.ui.modals.CreateGroupModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.handlers.progress.QuestProgress;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.groups.CreateGroupPacket;
import earth.terrarium.heracles.common.utils.ModUtils;
import earth.terrarium.olympus.client.ui.ClearableGridLayout;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractQuestsScreen extends BaseCursorScreen {

    public static final int HEADER_HEIGHT = 13;
    public static final int SPACER = 2;
    public static final int PADDING = 5;
    public static final int DOCKED_MINIMAP_HEIGHT = 66;

    protected int sideBarWidth;
    protected int contentWidth;
    protected int contentHeight;

    protected final Screen parent;
    public QuestsContent content;

    protected QuestsWidget quests;

    protected AbstractQuestsScreen(Screen parent, QuestsContent content) {
        super(CommonComponents.EMPTY);
        this.parent = parent;
        this.content = content;
    }

    public void updateProgress() {
        for (var entry : this.content.quests().entrySet()) {
            ClientQuests.get(entry.getKey()).ifPresent(quest -> {
                QuestProgress progress = ClientQuests.getProgress(entry.getKey());
                if (progress != null && progress.isClaimed(quest.value())) {
                    entry.setValue(ModUtils.QuestStatus.COMPLETED_CLAIMED);
                }
            });
        }

        List<Pair<ClientQuests.QuestEntry, ModUtils.QuestStatus>> quests = new ArrayList<>();
        content.quests().forEach((id, status) ->
            ClientQuests.get(id)
                .filter(quest -> quest.value().display().groups().containsKey(content.group()))
                .ifPresent(quest -> quests.add(Pair.of(quest, status)))
        );
        this.quests.update(quests);
        this.init();
    }

    @Override
    protected void init() {
        this.clearWidgets();
        this.sideBarWidth = Math.max((int) (width * 0.25f), 125);
        this.contentWidth = this.width - this.sideBarWidth;

        // formerly this.contentHeight = this.height - HEADER_HEIGHT - SPACER; removing - SPACER allows the background image to take up the entire content pane height.
        this.contentHeight = this.height - HEADER_HEIGHT;

        Layout sidebar = initSidebar(new AtomicInteger());
        Layout content = initContent(new AtomicInteger());

        sidebar.arrangeElements();
        content.arrangeElements();
        sidebar.visitWidgets(this::addRenderableWidget);
        content.visitWidgets(this::addRenderableWidget);
    }

    protected Layout initSidebar(AtomicInteger row) {
        ClearableGridLayout layout = new ClearableGridLayout();
        ClearableGridLayout header = new ClearableGridLayout();
        header.addChild(
            SpriteButton.create(11, 11, UIConstants.BACK, this::back).withTooltip(CommonComponents.GUI_BACK),
            0, 0,
            s -> s.padding(1)
        );
        header.addChild(new TextWidget(this.sideBarWidth - 26 - SPACER, 11, Component.literal("Groups"), Minecraft.getInstance().font), 0, 1);

        //add button
        if(QuestTab.isEditing()) {
            header.addChild(
                SpriteButton.create(11, 11, UIConstants.ADD, this::addGroup).withTooltip(ConstantComponents.Groups.CREATE),
                0, 2,
                s -> s.padding(1)
            );
        }

        header.addChild(SpacerElement.height(HEADER_HEIGHT + SPACER), 0, 3);
        layout.addChild(header, row.getAndIncrement(), 0);

        int groupsHeight = this.contentHeight - HEADER_HEIGHT - SPACER;
        if (DisplayConfig.dockMinimap && DisplayConfig.showMinimap) {
            groupsHeight -= DOCKED_MINIMAP_HEIGHT + SPACER;
        }
        ListWidget groups = new ListWidget(this.sideBarWidth - 2 - SPACER, groupsHeight);
        for (String group : ClientQuests.groups()) {
            groups.add(new GroupEntry(this.sideBarWidth, 20, group, group.equals(this.content.group()), this::init, groups));
        }
        layout.addChild(
            groups, row.getAndIncrement(), 0,
            s -> s.padding(1).paddingVertical(9)
        );

        return layout;
    }

    protected GridLayout initHeader(AtomicInteger column) {
        GridLayout header = new GridLayout();
        header.setPosition(this.sideBarWidth, 0);
        header.addChild(SpacerElement.height(HEADER_HEIGHT), 0, column.getAndIncrement());
        return header;
    }

    protected Layout initContent(AtomicInteger row) {
        ClearableGridLayout layout = new ClearableGridLayout();
        layout.setPosition(this.sideBarWidth, 0);
        layout.addChild(initHeader(new AtomicInteger()), row.getAndIncrement(), 0);

        List<Pair<ClientQuests.QuestEntry, ModUtils.QuestStatus>> quests = new ArrayList<>();
        content.quests().forEach((id, status) ->
            ClientQuests.get(id)
                .filter(quest -> quest.value().display().groups().containsKey(content.group()))
                .ifPresent(quest -> quests.add(Pair.of(quest, status)))
        );


        Set<String> selectedKeys = new HashSet<>();
        if (this.quests != null) {
            this.quests.visit(QuestWidget.class, widget -> {
                if (!widget.isSelected()) return;
                selectedKeys.add(widget.entry().key());
            });
        }

        this.quests = layout.addChild(new QuestsWidget(
            this.contentWidth, this.contentHeight,
            this.content, handler()
        ), row.getAndIncrement(), 0);
        this.quests.update(quests);
        this.quests.select((QuestWidget widget) -> selectedKeys.contains(widget.entry().key()));

        if (DisplayConfig.dockMinimap && DisplayConfig.showMinimap) {
            this.quests.setDockedMinimapBounds(
                getDockedMinimapX(), getDockedMinimapY(),
                getDockedMinimapWidth(), getDockedMinimapHeight()
            );
        } else {
            this.quests.setDockedMinimapBounds(0, 0, 0, 0);
        }

        return layout;
    }

    protected void back() {
        Minecraft.getInstance().setScreen(this.parent);
    }

    protected void addGroup() {
        CreateGroupModal.open(group -> {
            NetworkHandler.CHANNEL.sendToServer(new CreateGroupPacket(group));
            ClientQuests.groups().add(group);
            this.init();
        });
    }

    protected void edit() {
        if (!QuestTab.toggleEditing()) return;
        boolean isEditing = QuestTab.isEditing();
        Screen screen;
        if (isEditing) {
            screen = new EditQuestsScreen(this.parent, this.content);
        } else {
            screen = new QuestsScreen(this.parent, this.content);
        }
        Minecraft.getInstance().setScreen(screen);
    }

    protected abstract QuestActionHandler handler();

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.enableBlend();
        UIConstants.blitWithEdge(graphics, UIConstants.SIDEBAR_HEADER, 0, 0, this.sideBarWidth, HEADER_HEIGHT + SPACER, 2);
        UIConstants.blitWithEdge(graphics, UIConstants.CONTENT_HEADER, this.sideBarWidth, 0, this.contentWidth, HEADER_HEIGHT + SPACER, 2);
        UIConstants.blitWithEdge(graphics, UIConstants.SIDEBAR, 0, HEADER_HEIGHT + SPACER, this.sideBarWidth, this.height - HEADER_HEIGHT + SPACER, 2);
        UIConstants.blitWithEdge(graphics, UIConstants.CONTENT, this.sideBarWidth, HEADER_HEIGHT + SPACER, this.contentWidth, this.height - HEADER_HEIGHT + SPACER, 2);
        UIConstants.blitWithEdge(graphics, UIConstants.GROUPS, 0, HEADER_HEIGHT + SPACER, this.sideBarWidth - SPACER, this.height - HEADER_HEIGHT + SPACER, 2);
    }

    @Override
    public void actuallyRender(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.actuallyRender(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public Screen parent() {
        return this.parent;
    }

    public int getDockedMinimapX() {
        return 1;
    }

    public int getDockedMinimapY() {
        return this.height - DOCKED_MINIMAP_HEIGHT;
    }

    public int getDockedMinimapWidth() {
        return this.sideBarWidth - SPACER - 2;
    }

    public int getDockedMinimapHeight() {
        return DOCKED_MINIMAP_HEIGHT;
    }

    public void setContent(QuestsContent content) {
        this.content = content;
        if (this.quests != null) {
            this.quests.setContent(content);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.quests != null && this.quests.isMouseOver(mouseX, mouseY) && this.quests.isDragging()) {
            return this.quests.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
}

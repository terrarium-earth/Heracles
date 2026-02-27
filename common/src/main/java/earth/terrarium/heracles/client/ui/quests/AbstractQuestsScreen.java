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
import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.common.menus.quests.QuestsContent;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractQuestsScreen extends BaseCursorScreen {

    public static final int HEADER_HEIGHT = 13;
    public static final int SPACER = 2;
    public static final int PADDING = 5;

    protected int sideBarWidth;
    protected int contentWidth;
    protected int contentHeight;

    protected final Screen parent;
    protected final QuestsContent content;

    protected QuestsWidget quests;

    protected AbstractQuestsScreen(Screen parent, QuestsContent content) {
        super(CommonComponents.EMPTY);
        this.parent = parent;
        this.content = content;
    }

    @Override
    protected void init() {
        this.sideBarWidth = Math.max((int) (width * 0.25f), 125);
        this.contentWidth = this.width - this.sideBarWidth;
        this.contentHeight = this.height - HEADER_HEIGHT - SPACER;

        GridLayout sidebar = initSidebar(new AtomicInteger());
        GridLayout content = initContent(new AtomicInteger());

        sidebar.arrangeElements();
        content.arrangeElements();
        sidebar.visitWidgets(this::addRenderableWidget);
        content.visitWidgets(this::addRenderableWidget);
    }

    protected GridLayout initSidebar(AtomicInteger row) {
        GridLayout layout = new GridLayout(0, 0);
        GridLayout header = new GridLayout(0, 0);
        header.addChild(
            SpriteButton.create(11, 11, UIConstants.BACK, this::back).withTooltip(CommonComponents.GUI_BACK),
            0, 0,
            header.newCellSettings().padding(1)
        );
        header.addChild(new TextWidget(this.sideBarWidth - 26 - SPACER, 11, Component.literal("Groups"), Minecraft.getInstance().font), 0, 1);
        header.addChild(
            SpriteButton.create(11, 11, UIConstants.BACK, this::back).withTooltip(CommonComponents.GUI_BACK),
            0, 2,
            header.newCellSettings().padding(1)
        );
        header.addChild(SpacerElement.height(HEADER_HEIGHT + SPACER), 0, 3);
        layout.addChild(header, row.getAndIncrement(), 0);

        ListWidget groups = new ListWidget(this.sideBarWidth - 2 - SPACER, this.contentHeight);
        for (String group : ClientQuests.groups()) {
            groups.add(new GroupEntry(this.sideBarWidth, 20, group, group.equals(this.content.group())));
        }
        layout.addChild(
            groups, row.getAndIncrement(), 0,
            layout.newCellSettings().padding(1).paddingVertical(9)
        );

        return layout;
    }

    protected GridLayout initHeader(AtomicInteger column) {
        GridLayout header = new GridLayout(this.sideBarWidth, 0);
        header.addChild(SpacerElement.height(HEADER_HEIGHT), 0, column.getAndIncrement());
        return header;
    }

    protected GridLayout initContent(AtomicInteger row) {
        GridLayout layout = new GridLayout(this.sideBarWidth, 0);
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
        this.quests.select(widget -> selectedKeys.contains(widget.entry().key()));

        return layout;
    }

    protected void back() {
        Minecraft.getInstance().setScreen(this.parent);
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
    public void renderBackground(GuiGraphics graphics) {
        RenderSystem.enableBlend();
        UIConstants.blitWithEdge(graphics, UIConstants.SIDEBAR_HEADER, 0, 0, this.sideBarWidth, HEADER_HEIGHT + SPACER, 2);
        UIConstants.blitWithEdge(graphics, UIConstants.CONTENT_HEADER, this.sideBarWidth, 0, this.contentWidth, HEADER_HEIGHT + SPACER, 2);
        UIConstants.blitWithEdge(graphics, UIConstants.SIDEBAR, 0, HEADER_HEIGHT + SPACER, this.sideBarWidth, this.height - HEADER_HEIGHT + SPACER, 2);
        UIConstants.blitWithEdge(graphics, UIConstants.CONTENT, this.sideBarWidth, HEADER_HEIGHT + SPACER, this.contentWidth, this.height - HEADER_HEIGHT + SPACER, 2);
        UIConstants.blitWithEdge(graphics, UIConstants.GROUPS, 0, HEADER_HEIGHT + SPACER, this.sideBarWidth - SPACER, this.height - HEADER_HEIGHT + SPACER, 2);
    }

    @Override
    public void actuallyRender(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.actuallyRender(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public Screen parent() {
        return this.parent;
    }
}

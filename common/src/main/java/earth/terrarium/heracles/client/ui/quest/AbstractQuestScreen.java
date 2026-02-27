package earth.terrarium.heracles.client.ui.quest;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import earth.terrarium.heracles.api.client.theme.QuestsScreenTheme;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.quests.QuestDisplay;
import earth.terrarium.heracles.client.components.AlignedLayout;
import earth.terrarium.heracles.client.components.quest.QuestInfo;
import earth.terrarium.heracles.client.components.quest.QuestOverview;
import earth.terrarium.heracles.client.components.string.TextWidget;
import earth.terrarium.heracles.client.components.widgets.buttons.SelectedButton;
import earth.terrarium.heracles.client.components.widgets.buttons.SpriteButton;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.QuestTab;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.utils.ClientUtils;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.handlers.quests.QuestHandler;
import earth.terrarium.heracles.common.menus.quest.QuestContent;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.SpacerElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractQuestScreen extends BaseCursorScreen {

    protected static final LayoutSettings SIDEBAR_SETTINGS = LayoutSettings.defaults()
        .paddingHorizontal(5)
        .paddingVertical(1);

    public static final int HEADER_HEIGHT = 13;
    public static final int SPACER = 2;
    public static final int PADDING = 5;
    public static final int BUTTON_HEIGHT = 20;

    protected int sideBarWidth;
    protected int contentWidth;
    protected int contentHeight;

    protected final Screen parent;
    protected final QuestContent content;
    protected final QuestTab tab;

    protected AbstractQuestScreen(Screen parent, QuestContent content, QuestTab tab) {
        super(
            ClientQuests.get(content.id())
                .map(ClientQuests.QuestEntry::value)
                .map(Quest::display)
                .map(QuestDisplay::title)
                .orElse(CommonComponents.EMPTY)
                .copy()
                .append(" - [%s.json]".formatted(content.id()))
        );
        this.parent = parent;
        this.content = content;
        this.tab = tab;

        ClientQuests.mergeProgress(Map.of(content.id(), content.progress()));
    }

    public Quest quest() {
        return Optionull.map(entry(), ClientQuests.QuestEntry::value);
    }

    public ClientQuests.QuestEntry entry() {
        return ClientQuests.get(this.content.id())
            .orElse(null);
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
        header.addChild(SpacerElement.height(HEADER_HEIGHT + SPACER), 0, 1);
        layout.addChild(header, row.getAndIncrement(), 0);

        int paddedWidth = this.sideBarWidth - SPACER - PADDING * 2;
        layout.addChild(new QuestOverview(paddedWidth, this.content.id()), row.getAndIncrement(), 0, layout.newCellSettings().padding(PADDING).paddingBottom(1));
        layout.addChild(new QuestInfo(paddedWidth, this.content.id()), row.getAndIncrement(), 0, layout.newCellSettings().padding(PADDING).paddingTop(-SPACER));

        for (QuestTab tab : QuestTab.values()) {
            if (!tab.canBeShown(this.content)) continue;
            layout.addChild(
                SelectedButton.create(paddedWidth, BUTTON_HEIGHT, tab.getTitle(), () -> tab.open(this.content)),
                row.getAndIncrement(), 0, SIDEBAR_SETTINGS
            ).setSelected(this.tab == tab);
        }
        return layout;
    }

    protected GridLayout initContent(AtomicInteger row) {
        GridLayout layout = new GridLayout(this.sideBarWidth, 0);

        int quarter = Math.round(this.contentWidth / 4f);
        GridLayout header = new GridLayout(this.sideBarWidth, 0);

        GridLayout buttons = new GridLayout();

        int column = 0;

        if (ClientUtils.hasWorldFileAccess()) {
            buttons.addChild(
                SpriteButton.create(11, 11, UIConstants.FILE, this::openFile)
                    .withTooltip(ConstantComponents.OPEN_QUEST_FILE),
                0, column,
                buttons.newCellSettings().padding(1)
            );
            column++;
        }

        buttons.addChild(
            SpriteButton.create(11, 11, UIConstants.EDIT, this::edit)
                .withTooltip(ConstantComponents.TOGGLE_EDIT),
            0, column,
            buttons.newCellSettings().padding(1)
        );
        column++;

        buttons.addChild(
            SpriteButton.create(11, 11, UIConstants.CLOSE, this::onClose)
                .withTooltip(ConstantComponents.CLOSE),
            0, column,
            buttons.newCellSettings().padding(1)
        );

        header.addChild(SpacerElement.width(quarter), 0, 0);
        header.addChild(
            new TextWidget(this.contentWidth - quarter * 2, HEADER_HEIGHT, getTitle(), this.font)
                .alignCenter()
                .alignMiddle()
                .setColor(QuestsScreenTheme.getHeaderTitle()),
            0, 1,
            header.newCellSettings().padding(1)
        );
        header.addChild(
            AlignedLayout.rightAlign(quarter - 2, HEADER_HEIGHT, buttons),
            0, 2,
            header.newCellSettings()
        );

        layout.addChild(header, row.getAndIncrement(), 0);

        return layout;
    }

    protected void back() {
        Minecraft.getInstance().setScreen(this.parent);
    }

    protected void openFile() {
        Path path = QuestHandler.getQuestPath(this.quest(), this.content.id());
        if (path.toFile().isFile() && path.toFile().exists()) {
            Util.getPlatform().openFile(path.toFile());
        }
    }

    protected void edit() {
        if (!QuestTab.toggleEditing()) return;
        this.tab.open(this.content);
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        RenderSystem.enableBlend();
        UIConstants.blitWithEdge(graphics, UIConstants.SIDEBAR_HEADER, 0, 0, this.sideBarWidth, HEADER_HEIGHT + SPACER, 2);
        UIConstants.blitWithEdge(graphics, UIConstants.CONTENT_HEADER, this.sideBarWidth, 0, this.contentWidth, HEADER_HEIGHT + SPACER, 2);
        UIConstants.blitWithEdge(graphics, UIConstants.SIDEBAR, 0, HEADER_HEIGHT + SPACER, this.sideBarWidth, this.height - HEADER_HEIGHT + SPACER, 2);
        UIConstants.blitWithEdge(graphics, UIConstants.CONTENT, this.sideBarWidth, HEADER_HEIGHT + SPACER, this.contentWidth, this.height - HEADER_HEIGHT + SPACER, 2);
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

    public QuestContent content() {
        return this.content;
    }

    public Screen parent() {
        return this.parent;
    }
}

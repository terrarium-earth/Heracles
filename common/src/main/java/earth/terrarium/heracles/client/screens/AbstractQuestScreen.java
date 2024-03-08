package earth.terrarium.heracles.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.theme.QuestsScreenTheme;
import earth.terrarium.heracles.client.utils.ClientUtils;
import earth.terrarium.heracles.client.widgets.base.TemporaryWidget;
import earth.terrarium.heracles.client.widgets.modals.EditObjectModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractQuestScreen<T> extends BaseCursorScreen {

    public static final ResourceLocation HEADING = new ResourceLocation(Heracles.MOD_ID, "textures/gui/heading.png");

    protected final List<TemporaryWidget> temporaryWidgets = new ArrayList<>();
    protected boolean hasBackButton = true;

    protected final T content;

    protected static final float SIDE_BAR_PORTION = 0.25f;
    protected static int sideBarWidth;

    protected static final float QUEST_CONTENT_PORTION = 0.66f;
    protected static int questContentWidth;

    public AbstractQuestScreen(T content, Component component) {
        super(component);
        this.content = content;
    }

    @Override
    protected void init() {
        super.init();
        // There is a vertical 2-wide border area between the sidebar and the main area.
        // Established convention in this code-base counts this border area as "sidebar" in the general sense,
        // and subtracts 2 when referring to the sidebar area wholly within this border area.
        sideBarWidth = (int) (width * SIDE_BAR_PORTION) - 2;
        questContentWidth = (int) (width * QUEST_CONTENT_PORTION);

        if (hasBackButton) {
            addRenderableWidget(new ImageButton(1, 1, 11, 11, 0, 15, 11, HEADING, 256, 256, (button) ->
                goBack()
            )).setTooltip(Tooltip.create(CommonComponents.GUI_BACK));
        }
        addRenderableWidget(new ImageButton(this.width - 12, 1, 11, 11, 11, 15, 11, HEADING, 256, 256, (button) -> {
            if (this.minecraft != null && this.minecraft.player != null) {
                this.minecraft.player.closeContainer();
            }
        })).setTooltip(Tooltip.create(ConstantComponents.CLOSE));
    }

    @Override
    protected void clearWidgets() {
        super.clearWidgets();
        this.temporaryWidgets.clear();
    }

    public <R extends Renderable & TemporaryWidget> R addTemporary(R renderable) {
        addRenderableOnly(renderable);
        this.temporaryWidgets.add(renderable);
        return renderable;
    }

    public List<TemporaryWidget> temporaryWidgets() {
        return this.temporaryWidgets;
    }

    protected void goBack() {

    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int i, int j, float f) {
        this.renderBg(graphics, f, i, j);
        super.render(graphics, i, j, f);
        this.renderLabels(graphics, i, j);
    }

    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        if (drawSidebar()) {
            ClientUtils.blitTiling(graphics, HEADING, 0, 15, sideBarWidth, height - 15, 0, 128, 128, 128); // Side Background
            ClientUtils.blitTiling(graphics, HEADING, sideBarWidth + 2, 15, width - sideBarWidth, height - 15, 128, 128, 128, 128); // Main Background
            ClientUtils.blitTiling(graphics, HEADING, 0, 0, sideBarWidth, 15, 0, 0, 128, 15); // Side Header
            ClientUtils.blitTiling(graphics, HEADING, sideBarWidth + 2, 0, width - sideBarWidth, 15, 130, 0, 126, 15); // Main Header
            ClientUtils.blitTiling(graphics, HEADING, sideBarWidth, 0, 2, 15, 128, 0, 2, 15); // Header Separator
            ClientUtils.blitTiling(graphics, HEADING, sideBarWidth, 15, 2, height - 15, 128, 15, 2, 113); // Body Separator
        } else {
            ClientUtils.blitTiling(graphics, HEADING, 0, 15, width, height - 15, 128, 128, 128, 128); // Main Background
            ClientUtils.blitTiling(graphics, HEADING, 0, 0, width, 15, 130, 0, 126, 15); // Main Header
        }
        RenderSystem.disableBlend();
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        int center = questContentCenter();
        Component title = getTitle();
        graphics.drawString(
            this.font,
            title, (int) (center - (this.font.width(title) / 2f)), 3, QuestsScreenTheme.getHeaderTitle(),
            false
        );
    }

    public boolean isTemporaryWidgetVisible() {
        for (TemporaryWidget widget : temporaryWidgets) {
            if (widget.isVisible()) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        boolean visible = false;
        for (TemporaryWidget widget : this.temporaryWidgets) {
            visible |= widget.isVisible();
            if (widget.isVisible() && widget instanceof GuiEventListener listener) {
                return listener;
            }
        }
        if (visible) {
            return null;
        }
        return super.getFocused();
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        List<GuiEventListener> listeners = new ArrayList<>();
        for (TemporaryWidget widget : temporaryWidgets) {
            if (widget.isVisible() && widget instanceof GuiEventListener listener) {
                listeners.add(listener);
            }
        }
        if (!listeners.isEmpty()) {
            return listeners;
        }
        return super.children();
    }

    public List<? extends GuiEventListener> actualChildren() {
        return super.children();
    }

    @Override
    public void removed() {
        super.removed();
    }

    public EditObjectModal findOrCreateEditWidget() {
        boolean found = false;
        EditObjectModal widget = new EditObjectModal(this.width, this.height);
        for (TemporaryWidget temporaryWidget : this.temporaryWidgets()) {
            if (temporaryWidget instanceof EditObjectModal modal) {
                found = true;
                widget = modal;
                break;
            }
        }
        widget.setVisible(true);
        if (!found) {
            this.addTemporary(widget);
        }
        return widget;
    }

    public int questContentCenter() {
        // float, to avoid truncating (effectively rounding when 0.5f is added) twice
        float nonSideBarWidth = width - (width * SIDE_BAR_PORTION);
        return drawSidebar() ?
            (int) (0.5f + (width - (nonSideBarWidth / 2f))) :
            (int) (0.5f + (width / 2f));
    }

    public boolean drawSidebar() {
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

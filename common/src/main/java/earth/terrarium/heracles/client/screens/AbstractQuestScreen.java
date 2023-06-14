package earth.terrarium.heracles.client.screens;

import com.teamresourceful.resourcefullib.client.screens.BaseCursorScreen;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.widgets.base.TemporyWidget;
import earth.terrarium.heracles.client.widgets.modals.EditObjectModal;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import net.minecraft.client.Minecraft;
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

    protected final List<TemporyWidget> temporaryWidgets = new ArrayList<>();
    protected boolean hasBackButton = true;

    protected final T content;

    public AbstractQuestScreen(T content, Component component) {
        super(component);
        this.content = content;
    }

    @Override
    protected void init() {
        super.init();
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

    public <R extends Renderable & TemporyWidget> R addTemporary(R renderable) {
        addRenderableOnly(renderable);
        this.temporaryWidgets.add(renderable);
        return renderable;
    }

    public List<TemporyWidget> temporaryWidgets() {
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
        graphics.fill(0, 0, width, height, 0xD0000000);
        graphics.blitRepeating(HEADING, 0, 0, this.width, 15, 0, 0, 128, 15);
        if (drawSidebar()) {
            int sidebarWidth = (int) (this.width * 0.25f) - 2;
            graphics.blitRepeating(HEADING, sidebarWidth, 15, 2, this.height - 15, 128, 0, 2, 256);
            graphics.fill(sidebarWidth, 0, sidebarWidth + 2, 13, 0x80808080);
        }
    }

    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        int center = drawSidebar() ?
            (int) ((this.width * 0.25f) + ((this.width * 0.75f) / 2f))
            : (int) (this.width / 2f);
        graphics.drawString(
            this.font,
            this.title, (int) (center - (this.font.width(this.title) / 2f)), 3, 0x404040,
            false
        );
    }

    public boolean isTemporaryWidgetVisible() {
        for (TemporyWidget widget : temporaryWidgets) {
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
        for (TemporyWidget widget : this.temporaryWidgets) {
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
        for (TemporyWidget widget : temporaryWidgets) {
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) {
            return true;
        }
        if (this instanceof InternalKeyPressHook hook) {
            return hook.heracles$internalKeyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void removed() {
        super.removed();
    }

    public EditObjectModal findOrCreateEditWidget() {
        boolean found = false;
        EditObjectModal widget = new EditObjectModal(this.width, this.height);
        for (TemporyWidget temporaryWidget : this.temporaryWidgets()) {
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

    public boolean drawSidebar() {
        return true;
    }
}

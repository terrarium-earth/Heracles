package earth.terrarium.heracles.client.components.lists;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.client.components.widgets.buttons.SpriteButton;
import earth.terrarium.heracles.client.components.widgets.context.ContextMenu;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.ui.modals.DeleteConfirmModal;
import earth.terrarium.heracles.client.utils.UIUtils;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import net.minecraft.Util;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;

public abstract class AbstractEditListEntry<T> implements BaseListEntry<T> {

    private static final int BUTTON_WIDTH = 13;
    private static final int BUTTON_HEIGHT = 15;

    private static final int PADDING = 5;
    private static final int SPACE = BUTTON_WIDTH + PADDING * 2;

    private final GridLayout buttons = Util.make(new GridLayout().rowSpacing(PADDING), layout -> {
        layout.addChild(
            SpriteButton.create(BUTTON_WIDTH, BUTTON_HEIGHT, UIConstants.LIST_EDIT, this::edit)
                .withTooltip(Component.literal("Edit")),
            0, 0
        );
        layout.addChild(
            SpriteButton.create(BUTTON_WIDTH, BUTTON_HEIGHT, UIConstants.LIST_DELETE, this::tryDelete)
            .withTooltip(Component.literal("Delete")),
            1, 0
        );
    });

    protected QuestList<T> list;
    protected T value;
    protected DisplayWidget widget;

    private boolean hovered = false;

    public AbstractEditListEntry(QuestList<T> list, T value, DisplayWidget widget) {
        this.list = list;
        this.value = value;
        this.widget = widget;

        this.buttons.arrangeElements();
    }

    public int getItemWidth(boolean hovered, int width) {
        return hovered ? width - SPACE : width;
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        boolean wasHovered = this.hovered;
        this.hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + this.getHeight(width);

        int itemWidth = UIUtils.lerp(!wasHovered, partialTicks, getItemWidth(this.hovered, width), width);
        int height = this.getHeight(width);

        BaseListEntry.super.render(graphics, scissor, x, y, width, mouseX, mouseY, hovered, partialTicks);
        this.widget.render(graphics, scissor, x, y, itemWidth, mouseX, mouseY, hovered, partialTicks);

        if (!this.hovered) return;

        FrameLayout.centerInRectangle(this.buttons, x + itemWidth, y, SPACE, height);
        this.buttons.visitWidgets(widget -> {
            widget.render(graphics, mouseX, mouseY, partialTicks);
            CursorUtils.setCursor(widget.isHovered(), CursorScreen.Cursor.POINTER);
        });
    }

    @Override
    public int getHeight(int width) {
        return this.widget.getHeight(getItemWidth(this.hovered, width));
    }

    protected ClientQuests.QuestEntry entry() {
        return ClientQuests.get(this.list.content().id()).orElse(null);
    }

    protected abstract void setValue(T value);

    protected abstract void edit();

    protected abstract void delete();

    private void tryDelete() {
        DeleteConfirmModal.open(
            ConstantComponents.DELETE,
            Component.literal("Are you sure you want to delete this?"),
            this::delete
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        int height = this.getHeight(width);
        if (mouseX >= width - SPACE && mouseX <= width && mouseY >= 0 && mouseY <= height) {

            FrameLayout.centerInRectangle(this.buttons, 0, 0, SPACE, height);
            this.buttons.visitWidgets(widget -> widget.mouseClicked(mouseX - getItemWidth(this.hovered, width), mouseY, mouseButton));
            return true;
        }
        if (mouseButton == InputConstants.MOUSE_BUTTON_RIGHT) {
            ContextMenu.open(menu -> {
                menu.button(Component.literal("Edit"), this::edit);
                menu.dangerButton(Component.literal("Delete"), this::tryDelete);
                menu.divider();
                menu.button(Component.literal("Copy ID"), () -> UIUtils.copyToClipboard(this.id()));
            });
        }

        return this.widget.mouseClicked(mouseX, mouseY, mouseButton, width);
    }

    @Override
    public T value() {
        return value;
    }

    abstract public String id();

    @Override
    public void setList(QuestList<T> list) {
        this.list = list;
    }
}

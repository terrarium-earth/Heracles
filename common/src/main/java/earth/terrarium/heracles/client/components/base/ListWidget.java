package earth.terrarium.heracles.client.components.base;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.utils.UIUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ListWidget extends BaseParentWidget {

    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_PADDING = 2;
    private static final ResourceLocation SCROLLBAR = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/lists/scroll/bar.png");
    private static final ResourceLocation SCROLLBAR_THUMB = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/lists/scroll/thumb.png");

    private final List<Item> items = new ArrayList<>();

    private double scroll = 0;
    private int lastHeight = 0;
    private boolean scrolling = false;

    public ListWidget(int width, int height) {
        super(width, height);
    }

    public void update(ListWidget old) {
        if (old == null) return;
        if (this.items.size() != old.items.size()) return;
        if (this.height != old.height) return;
        updateLastHeight();
        if (this.lastHeight != old.lastHeight) return;

        this.scroll = old.scroll;
        this.scrolling = old.scrolling;
    }

    public void set(List<? extends Item> items) {
        this.items.clear();
        this.items.addAll(items);
        updateLastHeight();
    }

    public void add(Item item) {
        items.add(item);
        updateLastHeight();
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return items;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        boolean showsScrollBar = this.lastHeight > this.height;
        int actualWidth = getWidth() - (showsScrollBar ? SCROLLBAR_WIDTH + SCROLLBAR_PADDING * 2 : 0);

        graphics.enableScissor(getX(), getY(), getX() + actualWidth, getY() + this.height);

        int y = this.getY() - (int) scroll;
        this.lastHeight = 0;

        for (Item item : items) {
            item.setWidth(actualWidth);
            item.setX(getX());
            item.setY(y);

            item.render(graphics, this.isHovered ? mouseX : -1, this.isHovered ? mouseY : -1, partialTicks);
            y += item.getHeight();
            this.lastHeight += item.getHeight();
        }

        graphics.disableScissor();

        if (this.lastHeight > this.height) {
            int scrollBarHeight = (int) ((this.height / (double) this.lastHeight) * this.height) - SCROLLBAR_PADDING * 2;

            int scrollBarX = this.getX() + this.width - SCROLLBAR_WIDTH - SCROLLBAR_PADDING * 2;
            int scrollBarY = this.getY() + SCROLLBAR_PADDING + Math.round(((float) this.scroll / (float) this.lastHeight) * this.height);

            UIUtils.blitWithEdge(
                graphics,
                SCROLLBAR,
                scrollBarX + SCROLLBAR_PADDING * 2,
                this.getY() + SCROLLBAR_PADDING,
                SCROLLBAR_WIDTH - SCROLLBAR_PADDING * 2,
                this.height - SCROLLBAR_PADDING * 2,
                4
            );

            UIUtils.blitWithEdge(
                graphics,
                SCROLLBAR_THUMB,
                scrollBarX + SCROLLBAR_PADDING,
                scrollBarY,
                SCROLLBAR_WIDTH,
                scrollBarHeight,
                4
            );
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.scrolling) {
            double scrollBarHeight = (this.height / (double) this.lastHeight) * this.height;
            double scrollBarDragY = dragY / (this.height - scrollBarHeight);
            this.scroll = Mth.clamp(
                    this.scroll + scrollBarDragY * this.lastHeight, 0,
                    Math.max(0, this.lastHeight - this.height)
            );
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollY) {
        this.scroll = Mth.clamp(this.scroll - scrollY * 10, 0, Math.max(0, this.lastHeight - this.height));
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            if (isMouseOverScrollBar(mouseX, mouseY)) {
                this.scrolling = true;
                return true;
            }
            return super.mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if (i == 0) {
            this.scrolling = false;
        }
        return super.mouseReleased(d, e, i);
    }

    private boolean isMouseOverScrollBar(double mouseX, double mouseY) {
        if (this.lastHeight > this.height) {
            int scrollBarX = this.getX() + this.width - SCROLLBAR_WIDTH - SCROLLBAR_PADDING;
            return mouseX >= scrollBarX && mouseX <= scrollBarX + SCROLLBAR_WIDTH && mouseY >= this.getY() && mouseY <= this.getY() + this.height;
        }
        return false;
    }

    private void updateLastHeight() {
        boolean showsScrollBar = this.lastHeight > this.height;
        int actualWidth = getWidth() - (showsScrollBar ? SCROLLBAR_WIDTH + SCROLLBAR_PADDING * 2 : 0);

        this.lastHeight = 0;
        int y = this.getY() - (int) scroll;
        for (Item item : items) {
            item.setWidth(actualWidth);
            item.setX(getX());
            item.setY(y);
            this.lastHeight += item.getHeight();
            y += item.getHeight();
        }
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public interface Item extends GuiEventListener, Renderable, NarratableEntry, LayoutElement {

        @Override
        default @NotNull ScreenRectangle getRectangle() {
            return LayoutElement.super.getRectangle();
        }

        void setWidth(int width);
    }
}
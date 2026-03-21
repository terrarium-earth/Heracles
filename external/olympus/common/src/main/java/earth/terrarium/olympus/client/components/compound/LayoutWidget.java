package earth.terrarium.olympus.client.components.compound;

import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.common.utils.TriState;
import earth.terrarium.olympus.client.components.base.BaseParentWidget;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext;
import earth.terrarium.olympus.client.ui.UIConstants;
import earth.terrarium.olympus.client.utils.Orientation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.Layout;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.function.Consumers;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class LayoutWidget<T extends Layout> extends BaseParentWidget {
    private final T layout;
    private boolean arranged = false;

    private int overscrollX = 0;
    private int overscrollY = 0;

    private int xScroll;
    private int yScroll;

    private TriState scrollableX = TriState.FALSE;
    private TriState scrollableY = TriState.FALSE;
    private boolean autoFocus = true;

    private boolean draggingScrollbarX = false;
    private boolean draggingScrollbarY = false;

    private final List<BiConsumer<LayoutWidget<T>, T>> widthCallbacks = new ArrayList<>();
    private final List<BiConsumer<LayoutWidget<T>, T>> heightCallbacks = new ArrayList<>();
    private final List<BiConsumer<LayoutWidget<T>, T>> layoutCallbacks = new ArrayList<>();

    private int scrollWidth = 6;
    private int scrollMargin = 2;
    private int contentMargin = 0;

    private Identifier scrollbarBackground = null;
    private Identifier background = null;

    private WidgetRenderer<LayoutWidget<T>> scrollbarXRenderer = (graphics, context, partialTick) -> {
        var widget = context.getWidget();

        int scrollWidth = (int) (context.getWidth() * (context.getWidth() / (float) widget.getContentWidth())) + (widget.getViewWidth() - context.getWidth());
        int scrollX = (int) ((widget.getXScroll() + widget.getOverscrollX()) / (float) widget.getContentWidth() * context.getWidth());

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, UIConstants.SCROLLBAR,
                context.getX(),
                context.getY() + 2,
                context.getWidth(),
                context.getHeight() - 4
        );

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, UIConstants.SCROLLBAR_THUMB,
                context.getX() + scrollX,
                context.getY(),
                scrollWidth,
                context.getHeight()
        );
    };

    private WidgetRenderer<LayoutWidget<T>> scrollbarYRenderer = (graphics, context, partialTick) -> {
        var widget = context.getWidget();

        int scrollHeight = (int) (context.getHeight() * (context.getHeight() / (float) widget.getContentHeight())) + (widget.getViewHeight() - context.getHeight());
        int scrollY = (int) ((widget.getYScroll() + widget.getOverscrollY()) / (float) widget.getContentHeight() * context.getHeight());

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, UIConstants.SCROLLBAR,
                context.getX() + 2,
                context.getY(),
                context.getWidth() - 4,
                context.getHeight()
        );

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, UIConstants.SCROLLBAR_THUMB,
                context.getX(),
                context.getY() + scrollY,
                context.getWidth(),
                scrollHeight
        );
    };

    public LayoutWidget(T layout) {
        this.layout = layout;
    }

    public LayoutWidget<T> withContents(Consumer<T> consumer) {
        consumer.accept(layout);
        this.clear();
        layout.arrangeElements();
        layout.visitWidgets(this::addRenderableWidget);

        layoutCallbacks.forEach(callback -> callback.accept(this, layout));
        widthCallbacks.forEach(callback -> callback.accept(this, layout));
        heightCallbacks.forEach(callback -> callback.accept(this, layout));

        withScroll(xScroll, yScroll);
        return this;
    }

    protected boolean isXScrollbarVisible() {
        return scrollableX.isTrue() || (scrollableX.isUndefined() && layout.getWidth() > this.getWidth());
    }

    protected boolean isYScrollbarVisible() {
        return scrollableY.isTrue() || (scrollableY.isUndefined() && layout.getHeight() > this.getHeight());
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (!arranged) {
            withContents(Consumers.nop());
            arranged = true;
        }

        layout.setPosition(getX() - xScroll + contentMargin, getY() - yScroll + contentMargin);

        if (background != null) {
            graphics.blitSprite(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND, background, getX(), getY(), getViewWidth() + contentMargin * 2, getViewHeight() + contentMargin * 2);
        }

        graphics.enableScissor(getX() + contentMargin, getY() + contentMargin, getX() + getViewWidth() + contentMargin, getY() + getViewHeight() + contentMargin);
        super.renderWidget(graphics, mouseX, mouseY, partialTick);
        graphics.disableScissor();

        if (isXScrollbarVisible()) {
            if (scrollbarBackground != null) {
                graphics.blitSprite(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND, scrollbarBackground, getX(), getY() + getViewHeight() + contentMargin * 2, getViewWidth() + contentMargin * 2, getHeight() - getViewHeight() - contentMargin * 2);
            }
            scrollbarXRenderer.render(graphics, new WidgetRendererContext<>(this, mouseX, mouseY).setHeight(scrollWidth).setWidth(getViewWidth() - scrollMargin * 2).setX(getX() + scrollMargin).setY(this.getY() + this.getViewHeight() + scrollMargin + contentMargin * 2), partialTick);
        }

        if (isYScrollbarVisible()) {
            if (scrollbarBackground != null) {
                graphics.blitSprite(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND, scrollbarBackground, getX() + getViewWidth() + contentMargin * 2, getY(), getWidth() - getViewWidth() - contentMargin * 2, getViewHeight() + contentMargin * 2);
            }
            scrollbarYRenderer.render(graphics, new WidgetRendererContext<>(this, mouseX, mouseY).setWidth(scrollWidth).setHeight(getViewHeight() - scrollMargin * 2).setX(this.getX() + this.getViewWidth() + scrollMargin + contentMargin * 2).setY(getY() + scrollMargin), partialTick);
        }

        if (isYScrollbarVisible() && isXScrollbarVisible() && scrollbarBackground != null) {
            graphics.blitSprite(RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND, scrollbarBackground, getX() + getViewWidth() + contentMargin * 2, getY() + getViewHeight() + contentMargin * 2, getWidth() - getViewWidth() - contentMargin * 2, getHeight() - getViewHeight() - contentMargin * 2);
        }
    }

    @Override
    public void updateCursor(int mouseX, int mouseY) {
        if (isOverContent(mouseX, mouseY)) {
            super.updateCursor(mouseX, mouseY);
        } else {
            this.cursor = CursorScreen.Cursor.DEFAULT;
        }
    }

    public boolean isOverScrollbarX(int mouseX, int mouseY) {
        return isXScrollbarVisible() && mouseX >= getX() + scrollMargin && mouseX <= getX() + getViewHeight() - scrollMargin && mouseY >= getY() + getViewHeight() + scrollMargin && mouseY <= getY() + getHeight() - scrollMargin;
    }

    public boolean isOverScrollbarY(int mouseX, int mouseY) {
        return isYScrollbarVisible() && mouseX >= getX() + getViewWidth() + scrollMargin && mouseX <= getX() + getWidth() - scrollMargin && mouseY >= getY() + scrollMargin && mouseY <= getY() + getViewHeight() - scrollMargin;
    }

    public boolean isOverContent(int mouseX, int mouseY) {
        return mouseX >= getX() + contentMargin && mouseX <= getX() + contentMargin + getViewWidth() && mouseY >= getY() + contentMargin && mouseY <= getY() + contentMargin + getViewHeight();
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        var actualWidth = getViewWidth();
        var actualHeight = getViewHeight();

        if (draggingScrollbarX) {
            double scrollBarWidth = (actualWidth / ((double) layout.getWidth() + overscrollX * 2)) * actualWidth;
            double scrollBarDragX = dragX / (actualWidth - scrollBarWidth);
            withScrollX(xScroll + (int) (scrollBarDragX * (layout.getWidth() + overscrollX * 2)));
            return true;
        }

        if (draggingScrollbarY) {
            double scrollBarHeight = (actualHeight / ((double) layout.getHeight() + overscrollY * 2)) * actualHeight;
            double scrollBarDragY = dragY / (actualHeight - scrollBarHeight);
            withScrollY(yScroll + (int) (scrollBarDragY * (layout.getHeight() + overscrollY * 2)));
            return true;
        }

        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean contentScrolled = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (isMouseOver(mouseX, mouseY) && !contentScrolled) {
            var scrolled = false;
            if (isXScrollbarVisible()) {
                withScrollX(xScroll - (int) (scrollX * 10));
                scrolled = true;
            }

            if (isYScrollbarVisible()) {
                withScrollY(yScroll - (int) (scrollY * 10));
                scrolled = true;
            }

            return scrolled;
        }
        return contentScrolled;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (!isMouseOver(event.x(), event.y())) return false;
        if (isOverScrollbarX((int) event.x(), (int) event.y()) && isXScrollbarVisible()) {
            draggingScrollbarX = true;
            setDragging(true);
            return true;
        } else if (isOverScrollbarY((int) event.x(), (int) event.y()) && isYScrollbarVisible()) {
            draggingScrollbarY = true;
            setDragging(true);
            return true;
        }
        return super.mouseClicked(event, bl);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        this.draggingScrollbarX = false;
        this.draggingScrollbarY = false;
        setDragging(false);
        return super.mouseReleased(event);
    }

    // region Builder methods
    public LayoutWidget<T> withStretchToContentSize() {
        return withLayoutCallback((widget, layout) -> widget.setSize(layout.getWidth(), layout.getHeight()));
    }

    public LayoutWidget<T> withStretchToContentWidth() {
        return withLayoutCallback((widget, layout) -> widget.setWidth(layout.getWidth()));
    }

    public LayoutWidget<T> withStretchToContentHeight() {
        return withLayoutCallback((widget, layout) -> widget.setHeight(layout.getHeight()));
    }

    public LayoutWidget<T> withContentFillWidth() {
        return withWidthCallback((widget, layout) -> layout.visitWidgets(child -> child.setWidth(widget.getViewWidth())));
    }

    public LayoutWidget<T> withContentFillHeight() {
        return withHeightCallback((widget, layout) -> layout.visitWidgets(child -> child.setHeight(widget.getViewHeight())));
    }

    public LayoutWidget<T> withContentFill() {
        return withContentFillWidth().withContentFillHeight();
    }

    public LayoutWidget<T> withEqualSpacing(Orientation orientation) {
        return withLayoutCallback((widget, layout) -> {
            List<LayoutElement> elements = new ArrayList<>();
            layout.visitWidgets(elements::add);
            if (elements.isEmpty()) return;

            var widgetSize = orientation.getValue(widget.getViewWidth(), widget.getViewHeight());
            if (elements.size() == 1) {
                orientation.setPos(elements.getFirst(), widgetSize / 2 - orientation.getSize(elements.getFirst()) / 2);
            } else {
                int spacing = (widgetSize - elements.stream().mapToInt(orientation::getSize).sum()) / (elements.size() - 1);
                int value = orientation.getValue(widget.getX(), widget.getY());
                for (LayoutElement element : elements) {
                    orientation.setPos(element, value);
                    value += orientation.getSize(element) + spacing;
                }
            }
        });
    }

    public LayoutWidget<T> withLayoutCallback(BiConsumer<LayoutWidget<T>, T> callback) {
        layoutCallbacks.add(callback);
        callback.accept(this, layout);
        return this;
    }

    public LayoutWidget<T> withWidthCallback(BiConsumer<LayoutWidget<T>, T> callback) {
        widthCallbacks.add(callback);
        callback.accept(this, layout);
        return this;
    }

    public LayoutWidget<T> withHeightCallback(BiConsumer<LayoutWidget<T>, T> callback) {
        heightCallbacks.add(callback);
        callback.accept(this, layout);
        return this;
    }

    public LayoutWidget<T> withScrollableX(TriState scrollableX) {
        this.scrollableX = scrollableX;
        return this;
    }

    public LayoutWidget<T> withScrollableY(TriState scrollableY) {
        this.scrollableY = scrollableY;
        return this;
    }

    public LayoutWidget<T> withScrollable(TriState scrollableX, TriState scrollableY) {
        return withScrollableX(scrollableX).withScrollableY(scrollableY);
    }

    public LayoutWidget<T> withScroll(int x, int y) {
        return withScrollX(x).withScrollY(y);
    }

    public LayoutWidget<T> withScrollX(int x) {
        this.xScroll = Mth.clamp(x, -overscrollX, Math.max(0, layout.getWidth() + overscrollX - this.getViewWidth()));
        return this;
    }

    public LayoutWidget<T> withScrollY(int y) {
        this.yScroll = Mth.clamp(y, -overscrollY, Math.max(0, layout.getHeight() + overscrollY - this.getViewHeight()));
        return this;
    }

    public LayoutWidget<T> withOverscroll(int overscrollX, int overscrollY) {
        return withOverscrollX(overscrollX).withOverscrollY(overscrollY);
    }

    public LayoutWidget<T> withOverscrollX(int overscrollX) {
        this.overscrollX = overscrollX;
        return withScrollX(-overscrollX);
    }

    public LayoutWidget<T> withOverscrollY(int overscrollY) {
        this.overscrollY = overscrollY;
        return withScrollY(-overscrollY);
    }

    public LayoutWidget<T> withScrollToBottom() {
        this.yScroll = layout.getHeight() - this.getHeight();
        return this;
    }

    public LayoutWidget<T> withScrollToRight() {
        this.xScroll = layout.getWidth() - this.getWidth();
        return this;
    }

    public LayoutWidget<T> withScrollbarWidth(int scrollWidth) {
        this.scrollWidth = scrollWidth;
        return this;
    }

    public LayoutWidget<T> withScrollbarMargin(int margin) {
        this.scrollMargin = margin;
        return this;
    }

    public LayoutWidget<T> withContentMargin(int margin) {
        this.contentMargin = margin;
        return this;
    }

    public LayoutWidget<T> withScrollbarXRenderer(WidgetRenderer<LayoutWidget<T>> scrollbarXRenderer) {
        this.scrollbarXRenderer = scrollbarXRenderer;
        return this;
    }

    public LayoutWidget<T> withScrollbarYRenderer(WidgetRenderer<LayoutWidget<T>> scrollbarYRenderer) {
        this.scrollbarYRenderer = scrollbarYRenderer;
        return this;
    }

    public LayoutWidget<T> withScrollbarBackground(Identifier scrollbarBackground) {
        this.scrollbarBackground = scrollbarBackground;
        return this;
    }

    public LayoutWidget<T> withTexture(Identifier texture) {
        this.background = texture;
        return this;
    }

    @Override
    public LayoutWidget<T> withTooltip(Component tooltip) {
        super.withTooltip(tooltip);
        return this;
    }

    @Override
    public LayoutWidget<T> withSize(int width, int height) {
        super.withSize(width, height);
        return this;
    }

    @Override
    public LayoutWidget<T> withSize(int size) {
        super.withSize(size);
        return this;
    }

    @Override
    public LayoutWidget<T> withPosition(int x, int y) {
        super.withPosition(x, y);
        return this;
    }

    public LayoutWidget<T> withAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
        return this;
    }
    //endregion

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        super.setFocused(focused);
        if (focused instanceof AbstractWidget widget && this.autoFocus) {
            if (isYScrollbarVisible()) {
                var isPastBottom = widget.getBottom() > this.getY() + contentMargin + this.getViewHeight();
                var isPastTop = widget.getY() < this.getY() + contentMargin;

                if (isPastBottom && !isPastTop) {
                    yScroll = Mth.clamp(yScroll + widget.getBottom() - (this.getY() + contentMargin + this.getViewHeight()), -overscrollY, Math.max(0, layout.getHeight() + overscrollY - this.getViewHeight()));
                } else if (isPastTop && !isPastBottom) {
                    yScroll = Mth.clamp(yScroll - (this.getY() + contentMargin - widget.getY()), -overscrollY, Math.max(0, layout.getHeight() + overscrollY - this.getViewHeight()));
                }
            }

            if (isXScrollbarVisible()) {
                var isPastRight = widget.getRight() > this.getX() + contentMargin + this.getViewWidth();
                var isPastLeft = widget.getX() < this.getX() + contentMargin;

                if (isPastRight && !isPastLeft) {
                    xScroll = Mth.clamp(xScroll + widget.getRight() - (this.getX() + contentMargin + this.getViewWidth()), -overscrollX, Math.max(0, layout.getWidth() + overscrollX - this.getViewWidth()));
                } else if (isPastLeft && !isPastRight) {
                    xScroll = Mth.clamp(xScroll - (this.getX() + contentMargin - widget.getX()), -overscrollX, Math.max(0, layout.getWidth() + overscrollX - this.getViewWidth()));
                }
            }
        }
    }

    @Override
    public void setX(int x) {
        super.setX(x);
        layout.setX(x);
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        layout.setY(y);
    }

    @Override
    public void setWidth(int width) {
        super.setWidth(width);
        widthCallbacks.forEach(callback -> callback.accept(this, layout));
        layout.arrangeElements();
    }

    @Override
    public void setHeight(int height) {
        super.setHeight(height);
        heightCallbacks.forEach(callback -> callback.accept(this, layout));
        layout.arrangeElements();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        widthCallbacks.forEach(callback -> callback.accept(this, layout));
        heightCallbacks.forEach(callback -> callback.accept(this, layout));
        layout.arrangeElements();
    }

    public int getViewWidth() {
        return this.getWidth() - (contentMargin * 2) - (isYScrollbarVisible() ? scrollWidth + scrollMargin * 2 : 0);
    }

    public int getViewHeight() {
        return this.getHeight() - (contentMargin * 2) - (isXScrollbarVisible() ? scrollWidth + scrollMargin * 2 : 0);
    }

    public int getContentWidth() {
        return layout.getWidth() + overscrollX * 2;
    }

    public int getContentHeight() {
        return layout.getHeight() + overscrollY * 2;
    }

    public int getXScroll() {
        return xScroll;
    }

    public int getYScroll() {
        return yScroll;
    }

    public int getOverscrollX() {
        return overscrollX;
    }

    public int getOverscrollY() {
        return overscrollY;
    }
}

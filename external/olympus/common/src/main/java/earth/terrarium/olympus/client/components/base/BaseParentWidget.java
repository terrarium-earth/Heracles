package earth.terrarium.olympus.client.components.base;

import com.teamresourceful.resourcefullib.client.components.CursorWidget;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class BaseParentWidget extends BaseWidget implements ContainerEventHandler {

    protected final List<Renderable> renderables = new ArrayList<>();
    protected final List<GuiEventListener> children = new ArrayList<>();

    @Nullable
    protected GuiEventListener focused;
    protected boolean isDragging;

    protected CursorScreen.Cursor cursor = CursorScreen.Cursor.DEFAULT;

    public BaseParentWidget(int width, int height) {
        super(width, height);
    }

    public BaseParentWidget() {}

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return children;
    }

    protected <T extends GuiEventListener & Renderable> T addRenderableWidget(T widget) {
        this.renderables.add(widget);
        this.children.add(widget);
        return widget;
    }

    protected void removeWidget(Predicate<GuiEventListener> predicate) {
        this.children.removeIf(widget -> {
            if (predicate.test(widget)) {
                if (widget instanceof Renderable) {
                    this.renderables.remove(widget);
                }
                return true;
            }
            return false;
        });
    }

    protected void clear() {
        this.renderables.clear();
        this.children.clear();
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (isMouseOver(mouseX, mouseY)) {
            for (Renderable renderable : renderables) {
                renderable.render(graphics, mouseX, mouseY, partialTicks);
            }

            updateCursor(mouseX, mouseY);
        } else {
            for (Renderable renderable : renderables) {
                renderable.render(graphics, -1, -1, partialTicks);
            }

            this.cursor = CursorScreen.Cursor.DEFAULT;
        }
    }

    public void updateCursor(int mouseX, int mouseY) {
        getChildAt(mouseX, mouseY).ifPresentOrElse(
            widget -> {
                if (widget instanceof CursorWidget cursorWidget) {
                    this.cursor = cursorWidget.getCursor();
                } else if (widget instanceof AbstractWidget abstractWidget && abstractWidget.visible) {
                    if (abstractWidget.active) {
                        cursor = widget instanceof EditBox || widget instanceof MultiLineEditBox ? CursorScreen.Cursor.TEXT : CursorScreen.Cursor.POINTER;
                    } else {
                        cursor = CursorScreen.Cursor.DISABLED;
                    }
                } else {
                    this.cursor = CursorScreen.Cursor.DEFAULT;
                }
            },
            () -> this.cursor = CursorScreen.Cursor.DEFAULT
        );
    }

    @Override
    public final boolean isDragging() {
        return this.isDragging;
    }

    @Override
    public final void setDragging(boolean isDragging) {
        this.isDragging = isDragging;
    }

    @Nullable
    @Override
    public GuiEventListener getFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(@Nullable GuiEventListener focused) {
        if (this.focused != null) {
            this.focused.setFocused(false);
        }

        if (focused != null) {
            focused.setFocused(true);
        }

        this.focused = focused;
    }

    @Override
    public boolean isFocused() {
        return ContainerEventHandler.super.isFocused();
    }

    @Override
    public void setFocused(boolean focused) {
        if (this.focused != null) {
            this.focused.setFocused(focused);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        Optional<GuiEventListener> optional = this.getChildAt(event.x(), event.y());
        if (optional.isPresent()) {
            GuiEventListener guiEventListener = optional.get();
            if (guiEventListener.mouseClicked(event, bl)) {
                this.setFocused(guiEventListener);
                if (event.input() == 0) {
                    this.setDragging(true);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        return ContainerEventHandler.super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        return ContainerEventHandler.super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        return ContainerEventHandler.super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        return ContainerEventHandler.super.keyReleased(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        return ContainerEventHandler.super.charTyped(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return ContainerEventHandler.super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent event) {
        return ContainerEventHandler.super.nextFocusPath(event);
    }

    public <T> void visit(Class<T> tClass, Consumer<T> consumer) {
        for (Renderable renderable : renderables) {
            if (tClass.isInstance(renderable)) {
                consumer.accept(tClass.cast(renderable));
            }
        }
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return this.cursor;
    }
}

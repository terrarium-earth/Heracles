package earth.terrarium.heracles.client.components.base;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class BaseParentWidget extends BaseWidget implements ContainerEventHandler {

    protected final List<Renderable> renderables = new ArrayList<>();
    protected final List<GuiEventListener> children = new ArrayList<>();

    @Nullable
    private GuiEventListener focused;
    private boolean isDragging;

    public BaseParentWidget(int width, int height) {
        super(width, height);
    }

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
        for (Renderable renderable : renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTicks);
        }
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return ContainerEventHandler.super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return ContainerEventHandler.super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return ContainerEventHandler.super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public <T> void visit(Class<T> tClass, Consumer<T> consumer) {
        for (Renderable renderable : renderables) {
            if (tClass.isInstance(renderable)) {
                consumer.accept(tClass.cast(renderable));
            }
        }
    }
}

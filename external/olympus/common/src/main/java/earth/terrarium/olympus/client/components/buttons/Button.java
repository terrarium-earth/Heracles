package earth.terrarium.olympus.client.components.buttons;

import com.mojang.blaze3d.platform.InputConstants;
import com.teamresourceful.resourcefullib.client.components.CursorWidget;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import earth.terrarium.olympus.client.components.base.BaseWidget;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRenderer;
import earth.terrarium.olympus.client.components.base.renderer.WidgetRendererContext;
import earth.terrarium.olympus.client.components.dropdown.DropdownBuilder;
import earth.terrarium.olympus.client.components.dropdown.DropdownState;
import earth.terrarium.olympus.client.ui.UIConstants;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;

public class Button extends BaseWidget implements CursorWidget {

    private WidgetRenderer<? super Button> renderer = WidgetRenderer.empty();
    private final Int2ObjectMap<Runnable> actions = new Int2ObjectArrayMap<>();
    private WidgetSprites sprites = UIConstants.BUTTON;
    private ButtonShape shape = ButtonShapes.RECTANGLE;

    public Button() {
        super();
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.isHovered = graphics.containsPointInScissor(mouseX, mouseY) && isMouseOver(mouseX, mouseY);

        int color = ARGB.color(0xFF, 0xFF, 0xFF, (int) (this.alpha * 255f));

        if (this.sprites != null) {
            graphics.blitSprite(
                    RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND,
                    this.sprites.get(this.active, this.isHoveredOrFocused()),
                    this.getX(), this.getY(),
                    this.getWidth(), this.getHeight(),
                    color
            );
        }
        this.renderer.render(graphics, new WidgetRendererContext<>(this, mouseX, mouseY), partialTick);
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return !this.isActive() ? CursorScreen.Cursor.DISABLED : CursorScreen.Cursor.POINTER;
    }

    public Button withRenderer(WidgetRenderer<? super Button> renderer) {
        this.renderer = renderer;
        return this;
    }

    public Button withCallback(int key, Runnable onPress) {
        this.actions.put(key, onPress);
        return this;
    }

    public Button withCallback(Runnable onPress) {
        this.actions.defaultReturnValue(onPress);
        return this;
    }

    public Button withTexture(@Nullable WidgetSprites sprites) {
        this.sprites = sprites;
        return this;
    }

    @Override
    public Button withSize(int width, int height) {
        return (Button) super.withSize(width, height);
    }

    public <T> DropdownBuilder<T> withDropdown(DropdownState<T> state) {
        state.setButton(this);
        return new DropdownBuilder<>(state);
    }

    public Button withShape(ButtonShape shape) {
        this.shape = shape;
        return this;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (!this.active || !this.visible) return false;
        return this.shape.isInside(mouseX - this.getX(), mouseY - this.getY(), this.getWidth(), this.getHeight());
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (this.active && this.visible && event.isSelection()) {
            var action = this.actions.get(InputConstants.MOUSE_BUTTON_LEFT);
            if (action != null) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                action.run();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (this.active && this.visible && this.isMouseOver(event.x(), event.y())) {
            var action = this.actions.get(event.input());
            if (action != null) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                action.run();
                return true;
            }
        }
        return false;
    }
}

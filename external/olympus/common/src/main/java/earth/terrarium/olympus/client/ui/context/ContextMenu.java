package earth.terrarium.olympus.client.ui.context;

import com.mojang.blaze3d.platform.Window;
import earth.terrarium.olympus.client.components.Widgets;
import earth.terrarium.olympus.client.components.base.ListWidget;
import earth.terrarium.olympus.client.components.dropdown.DropdownState;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.constants.MinecraftColors;
import earth.terrarium.olympus.client.ui.Overlay;
import earth.terrarium.olympus.client.ui.OverlayAlignment;
import earth.terrarium.olympus.client.ui.UIConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ContextMenu extends Overlay {

    private static final int PADDING = 3;

    private final List<Supplier<AbstractWidget>> actions = new ArrayList<>();

    private Identifier texture = UIConstants.LIST_BG;
    private int x;
    private int y;
    private int contextHeight;
    private int contextWidth;

    private int maxWidth;
    private int maxHeight;

    private OverlayAlignment alignment = null;
    private DropdownState<?> parent = null;
    private Runnable onClose = () -> {};
    private boolean autoClose = true;

    private ListWidget list;

    protected ContextMenu(Screen background, int x, int y) {
        super(background);
        this.x = x;
        this.y = y;
    }

    @Override
    protected void init() {
        var currentActions = this.actions.stream().map(Supplier::get).toList();

        var contentWidth = currentActions.stream().mapToInt(AbstractWidget::getWidth).max().orElse(0);
        var contentHeight = currentActions.stream().mapToInt(AbstractWidget::getHeight).sum();

        this.contextWidth = maxWidth > 0 ? Math.min(maxWidth, contentWidth + 4) : contentWidth + 4;
        this.contextHeight = maxHeight > 0 ? Math.min(maxHeight, contentHeight + 3) : contentHeight + 3;

        this.list = new ListWidget(contextWidth - 4, contextHeight - 3 - (maxHeight > 0 && maxHeight < contentHeight ? 1 : 0));
        this.list.set(currentActions);

        if (this.alignment != null && this.parent != null) {
            var pos = this.alignment.getPos(parent.getButton(), this.contextWidth, this.contextHeight);
            this.x = pos.x;
            this.y = pos.y;
        } else {
            if (this.contextHeight + this.y > this.height) {
                this.y = this.height - this.contextHeight;
            }
            if (this.contextWidth + this.x > this.width) {
                this.x = this.width - this.contextWidth;
            }
        }

        this.list.setPosition(this.x + 2, this.y + 2);
        this.addRenderableWidget(this.list);
    }

    public ContextMenu add(Supplier<AbstractWidget> action) {
        this.actions.add(action);
        return this;
    }

    public ContextMenu button(Component text, Runnable action) {
        return this.add(() -> Widgets.button()
                .withCallback(action)
                .withTexture(UIConstants.LIST_ENTRY)
                .withRenderer(WidgetRenderers.text(text).withColor(MinecraftColors.WHITE))
                .withSize(font.width(text) + PADDING * 2, font.lineHeight + 1 + PADDING * 2)
        );
    }

    public ContextMenu dangerButton(Component text, Runnable action) {
        return this.add(() -> Widgets.button()
                .withCallback(action)
                .withTexture(UIConstants.LIST_ENTRY)
                .withRenderer(WidgetRenderers.text(text).withColor(MinecraftColors.RED))
                .withSize(font.width(text) + PADDING * 2, font.lineHeight + 1 + PADDING * 2)
        );
    }

    public ContextMenu primaryButton(Component text, Runnable action) {
        return this.add(() -> Widgets.button()
                .withCallback(action)
                .withTexture(UIConstants.LIST_ENTRY)
                .withRenderer(WidgetRenderers.text(text).withColor(MinecraftColors.GREEN))
                .withSize(font.width(text) + PADDING * 2, font.lineHeight + 1 + PADDING * 2)
        );
    }

    public ContextMenu divider() {
        return this.add(DividerWidget::new);
    }

    public ContextMenu withBounds(int x, int y) {
        this.maxWidth = x;
        this.maxHeight = y;
        return this;
    }

    public ContextMenu withAlignment(OverlayAlignment side, DropdownState<?> parent) {
        this.alignment = side;
        this.parent = parent;
        return this;
    }

    public ContextMenu withTexture(Identifier texture) {
        this.texture = texture;
        return this;
    }

    public ContextMenu withCloseCallback(Runnable onClose) {
        this.onClose = onClose;
        return this;
    }

    public ContextMenu withAutoCloseOff() {
        this.autoClose = false;
        return this;
    }

    @Override
    public void onClose() {
        this.onClose.run();
        super.onClose();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        super.mouseClicked(event, bl);
        if (this.list.isMouseOver(event.x(), event.y())) {
            if (!this.list.isScrolling() && this.autoClose) {
                this.onClose();
            }
        } else {
            this.onClose();
        }
        return true;
    }

    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, this.texture, this.x, this.y, this.contextWidth, this.contextHeight);
    }

    public static void open(Consumer<ContextMenu> consumer) {
        MouseHandler mouse = Minecraft.getInstance().mouseHandler;
        Window window = Minecraft.getInstance().getWindow();
        double mouseX = mouse.xpos() * (double) window.getGuiScaledWidth() / (double) window.getScreenWidth();
        double mouseY = mouse.ypos() * (double) window.getGuiScaledHeight() / (double) window.getScreenHeight();

        open(mouseX, mouseY, consumer);
    }

    public static void open(double x, double y, Consumer<ContextMenu> consumer) {
        open((int) x, (int) y, consumer);
    }

    public static void open(int x, int y, Consumer<ContextMenu> consumer) {
        Minecraft mc = Minecraft.getInstance();
        Screen background = mc.screen;
        ContextMenu menu = new ContextMenu(background, x, y);
        consumer.accept(menu);
        mc.setScreen(menu);
    }
}

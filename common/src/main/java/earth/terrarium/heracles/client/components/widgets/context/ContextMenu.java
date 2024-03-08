package earth.terrarium.heracles.client.components.widgets.context;

import com.mojang.blaze3d.platform.Window;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.ClearableGridLayout;
import earth.terrarium.heracles.client.ui.Overlay;
import earth.terrarium.heracles.client.utils.UIUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public class ContextMenu extends Overlay {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/context/background.png");
    private static final int PADDING = 5;

    private final List<IntFunction<AbstractWidget>> actions = new ArrayList<>();
    private final int x;
    private final int contextWidth;
    private int y;
    private int contextHeight;

    private final ClearableGridLayout layout = new ClearableGridLayout();

    protected ContextMenu(Screen background, int x, int y, int width) {
        super(background);

        this.x = x;
        this.y = y;
        this.contextWidth = width;
    }

    @Override
    protected void init() {
        this.layout.clear();
        int i = 0;
        for (IntFunction<AbstractWidget> action : this.actions) {
            AbstractWidget widget = action.apply(this.contextWidth - PADDING * 2);

            this.layout.addChild(widget, i, 0);
            i++;
        }

        this.layout.arrangeElements();
        this.contextHeight = this.layout.getHeight() + PADDING * 2;

        if (this.contextHeight + this.y > this.height) {
            this.y = this.height - this.contextHeight;
        }
        this.layout.setPosition(this.x + PADDING, this.y + PADDING);
        this.layout.visitWidgets(this::addRenderableWidget);
    }

    public ContextMenu add(IntFunction<AbstractWidget> action) {
        this.actions.add(action);
        return this;
    }

    public ContextMenu button(Component text, Runnable action) {
        return this.add(width -> new ContextButtonWidget(width, text, action, false));
    }

    public ContextMenu dangerButton(Component text, Runnable action) {
        return this.add(width -> new ContextButtonWidget(width, text, action, true));
    }

    public ContextMenu divider() {
        return this.add(DividerWidget::new);
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        UIUtils.blitWithEdge(graphics, TEXTURE, this.x, this.y, this.contextWidth, this.contextHeight, 4);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.onClose();
        super.mouseClicked(mouseX, mouseY, button);
        return true;
    }

    public static void open(int width, Consumer<ContextMenu> consumer) {
        MouseHandler mouse = Minecraft.getInstance().mouseHandler;
        Window window = Minecraft.getInstance().getWindow();
        double mouseX = mouse.xpos() * (double) window.getGuiScaledWidth() / (double) window.getScreenWidth();
        double mouseY = mouse.ypos() * (double) window.getGuiScaledHeight() / (double) window.getScreenHeight();

        open(mouseX, mouseY, width, consumer);
    }

    public static void open(double x, double y, int width, Consumer<ContextMenu> consumer) {
        open((int) x, (int) y, width, consumer);
    }

    public static void open(int x, int y, int width, Consumer<ContextMenu> consumer) {
        Minecraft mc = Minecraft.getInstance();
        Screen background = mc.screen;
        int contextX = x + width > mc.getWindow().getGuiScaledWidth() ? x - width : x;
        ContextMenu menu = new ContextMenu(background, contextX, y, width);
        consumer.accept(menu);
        mc.setScreen(menu);
    }
}

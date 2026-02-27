package earth.terrarium.heracles.client.components.widgets.dropdown;

import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.base.BaseWidget;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.utils.UIUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Consumer;

public class Dropdown<T> extends BaseWidget {

    private static final ResourceLocation CHEVRON_DOWN = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/dropdown/chevron_down.png");
    private static final ResourceLocation CHEVRON_UP = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/dropdown/chevron_up.png");

    public static final int SELECTED = 0x505050;
    public static final int COLOR = 0xFEFEFE;

    private final Dropdown<?> parent;
    private final Map<T, Component> options;
    private final Consumer<T> onSelect;
    private T selected;

    public Dropdown(Dropdown<T> old, int width, int height, Map<T, Component> options, T selected) {
        this(old, width, height, options, selected, value -> {});
    }

    public Dropdown(Dropdown<T> old, int width, int height, Map<T, Component> options, T selected, Consumer<T> onSelect) {
        super(width, height);

        this.parent = old;
        this.options = options;
        this.onSelect = onSelect;
        this.selected = old != null ? old.selected : selected;
    }

    public boolean isDropdownOpen() {
        return Minecraft.getInstance().screen instanceof DropdownScreen<?> screen && screen.isOriginator(this);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        ResourceLocation sprite = UIConstants.BUTTON.get(this.isHovered(), !this.active);
        UIUtils.blitWithEdge(graphics, sprite, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 3);

        int textOffset = (this.height - 8) / 2;

        graphics.drawString(font, getText(this.selected), this.getX() + textOffset, this.getY() + textOffset - 1, SELECTED, false);

        int chevronOffset = (this.height - 16) / 2;

        ResourceLocation chevron = this.isDropdownOpen() ? CHEVRON_UP : CHEVRON_DOWN;

        graphics.blit(chevron, this.getX() + this.width - chevronOffset - 16, this.getY() + chevronOffset, 0, 0, 16, 16, 16, 16);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        DropdownScreen<T> screen = new DropdownScreen<>(Minecraft.getInstance().screen, this);
        Minecraft.getInstance().setScreen(screen);
    }

    public Component getText(T value) {
        return this.options.getOrDefault(value, CommonComponents.ELLIPSIS);
    }

    public Map<T, Component> options() {
        return this.options;
    }

    public T selected() {
        return this.selected;
    }

    public void select(T option) {
        this.selected = option;
        this.onSelect.accept(option);
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return CursorScreen.Cursor.POINTER;
    }

    public boolean is(Dropdown<?> dropdown) {
        return this == dropdown || this.parent != null && this.parent.is(dropdown);
    }
}

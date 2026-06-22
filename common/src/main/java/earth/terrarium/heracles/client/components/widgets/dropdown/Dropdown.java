package earth.terrarium.heracles.client.components.widgets.dropdown;
/*
import com.teamresourceful.resourcefullib.common.color.Color;import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.utils.UIUtils;
import earth.terrarium.olympus.client.components.buttons.Button;
import earth.terrarium.olympus.client.components.dropdown.DropdownState;
import earth.terrarium.olympus.client.components.renderers.WidgetRenderers;
import earth.terrarium.olympus.client.ui.UIConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Consumer;

public class Dropdown<T> extends Button {

    private static final ResourceLocation CHEVRON_DOWN =  Heracles.id("textures/gui/sprites/dropdown/chevron_down.png");
    private static final ResourceLocation CHEVRON_UP =  Heracles.id("textures/gui/sprites/dropdown/chevron_up.png");

    public static final int SELECTED = 0x505050;
    public static final int COLOR = 0xFEFEFE;

    private final Dropdown<?> parent;
    private final Map<T, Component> options;
    private final Consumer<T> onSelect;
    private final DropdownState<T> state;

    public Dropdown(Dropdown<T> old, int width, int height, Map<T, Component> options, T selected) {
        this(old, width, height, options, selected, value -> {});
    }

    public Dropdown(Dropdown<T> old, int width, int height, Map<T, Component> options, T selected, Consumer<T> onSelect) {
        super();

        this.withSize(width, height);

        this.parent = old;
        this.options = options;
        this.onSelect = onSelect;

        T initial = old != null ? old.state.get() : selected;
        this.state = DropdownState.of(initial);

        int listHeight = Math.min(24 * 5, this.options.size() * 24) + 3;
        this.withDropdown(this.state)
            .withOptions(new ArrayList<>(this.options.keySet()))
            .withEntryHeight(24)
            .withSize(width, listHeight)
            .withEntryRenderer(value -> WidgetRenderers.text(this.getText(value)).withLeftAlignment().withColor(new Color(COLOR)).withPaddingLeft(6))            .withCallback(this::select)
            .build();
    }

    public boolean isDropdownOpen() {
        return this.state.isOpened();
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        ResourceLocation sprite = UIConstants.BUTTON.get(this.isActive(), this.isHovered);
        UIUtils.blitWithEdge(graphics, sprite, this.getX(), this.getY(), this.getWidth(), this.getHeight(), 3);

        int textOffset = (this.height - 8) / 2;

        graphics.drawString(font, getText(this.state.get()), this.getX() + textOffset, this.getY() + textOffset - 1, SELECTED, false);

        int chevronOffset = (this.height - 16) / 2;

        ResourceLocation chevron = this.isDropdownOpen() ? CHEVRON_UP : CHEVRON_DOWN;

        graphics.blit(chevron, this.getX() + this.width - chevronOffset - 16, this.getY() + chevronOffset, 0, 0, 16, 16, 16, 16);
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
    }

    public Component getText(T value) {
        return this.options.getOrDefault(value, CommonComponents.ELLIPSIS);
    }

    public Map<T, Component> options() {
        return this.options;
    }

    public T selected() {
        return this.state.get();
    }

    public void select(T option) {
        this.state.set(option);
        this.onSelect.accept(option);
    }

    public boolean is(Dropdown<?> dropdown) {
        return this == dropdown || this.parent != null && this.parent.is(dropdown);
    }
}
*/
package earth.terrarium.heracles.client.components.widgets.dropdown;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.base.ListWidget;
import earth.terrarium.heracles.client.ui.Overlay;
import earth.terrarium.heracles.client.utils.UIUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;

public class DropdownScreen<T> extends Overlay {

    private static final ResourceLocation LIST = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/dropdown/list.png");

    private final Dropdown<T> dropdown;

    protected DropdownScreen(Screen background, Dropdown<T> dropdown) {
        super(background);
        this.dropdown = dropdown;
    }

    public int x() {
        return this.dropdown.getX();
    }

    public int y() {
        int y = this.dropdown.getY() + this.dropdown.getHeight();
        if (y + this.height() > this.background.height) {
            y = this.dropdown.getY() - this.height();
        }
        return y;
    }

    public int width() {
        return this.dropdown.getWidth();
    }

    public int height() {
        return Math.min(24 * 5, this.dropdown.options().size() * 24) + 3;
    }

    @Override
    protected void init() {
        ListWidget list = new ListWidget(this.width() - 3, this.height() - 3);
        list.setPosition(this.x() + 1, this.y() + 2);

        for (var entry : this.dropdown.options().entrySet()) {
            T value = entry.getKey();
            list.add(new DropdownEntry<>(
                this.width() - 3, 24,
                this.dropdown, value, () -> {
                    this.dropdown.select(value);
                    this.onClose();
                }
            ));
        }

        addRenderableWidget(list);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!super.mouseClicked(mouseX, mouseY, button)) {
            this.onClose();
        }
        return true;
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        UIUtils.blitWithEdge(graphics, LIST, this.x(), this.y(), this.width(), this.height(), 3);
    }

    public boolean isOriginator(Dropdown<?> dropdown) {
        return this.dropdown.is(dropdown);
    }
}

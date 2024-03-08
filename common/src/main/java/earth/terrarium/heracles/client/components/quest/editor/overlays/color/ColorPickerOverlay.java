package earth.terrarium.heracles.client.components.quest.editor.overlays.color;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.components.widgets.WidgetSprites;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.ui.Overlay;
import earth.terrarium.heracles.client.ui.UIConstants;
import earth.terrarium.heracles.client.utils.UIUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Locale;

public class ColorPickerOverlay extends Overlay {

    public static final WidgetSprites SPRITES = new WidgetSprites(
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/editor/color/normal.png"),
        new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/editor/color/hovered.png")
    );
    private static final int WIDGET_SIZE = 16;
    private static final int SPACING = 2;
    private static final int PADDING = 5;

    private final AbstractWidget button;

    protected ColorPickerOverlay(AbstractWidget button, Screen background) {
        super(background);
        this.button = button;
    }

    public int x() {
        int x = this.button.getX() - this.width();
        if (x < 0) {
            x = this.button.getX() + this.width();
            if (x + this.width() > this.background.width) {
                x = x - this.width() / 2;
            }
        }
        return x;
    }

    public int y() {
        return this.button.getY() + this.button.getHeight();
    }

    public int width() {
        return WIDGET_SIZE * 4 + SPACING * 3 + PADDING * 2;
    }

    public int height() {
        return WIDGET_SIZE * 4 + SPACING * 3 + PADDING * 2;
    }

    @Override
    protected void init() {
        GridLayout layout = new GridLayout().spacing(SPACING);
        GridLayout.RowHelper row = layout.createRowHelper(4);

        for (ChatFormatting value : ChatFormatting.values()) {
            if (!value.isColor()) continue;
            String formattedName = WordUtils.capitalizeFully(
                value.name().toLowerCase(Locale.ROOT).replace("_", " ")
            );

            Component text = Component.translatableWithFallback(
                "gui.heracles.editor.color." + value.getName(),
                formattedName
            );

            ColorButton button = ColorButton.of(() -> value, text, () -> {
                DisplayConfig.editorColor = value;
                DisplayConfig.save();
                this.onClose();
            });

            row.addChild(button);
        }

        layout.arrangeElements();
        layout.setPosition(this.x() + PADDING, this.y() + PADDING);
        layout.visitWidgets(this::addRenderableWidget);
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        UIUtils.blitWithEdge(graphics, UIConstants.MODAL_HEADER, this.x(), this.y(), this.width(), this.height(), 3);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean isHovered = mouseX >= this.x() && mouseX <= this.x() + this.width() && mouseY >= this.y() && mouseY <= this.y() + this.height();
        if (!isHovered) {
            this.onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public static void open(AbstractWidget button) {
        Screen background = Minecraft.getInstance().screen;
        ColorPickerOverlay modal = new ColorPickerOverlay(button, background);
        Minecraft.getInstance().setScreen(modal);
    }
}

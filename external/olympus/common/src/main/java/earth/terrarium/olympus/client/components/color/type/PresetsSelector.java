package earth.terrarium.olympus.client.components.color.type;

import com.teamresourceful.resourcefullib.common.color.Color;
import earth.terrarium.olympus.client.components.base.BaseWidget;
import earth.terrarium.olympus.client.components.color.ColorPresetType;
import earth.terrarium.olympus.client.constants.MinecraftColors;
import earth.terrarium.olympus.client.utils.State;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class PresetsSelector extends BaseWidget {
    private final Color[] presets;
    private final State<ColorPresetType> type;
    private final State<Color> state;
    private final boolean withAlpha;
    private final Identifier background;

    private Collection<Color> colors = new ArrayList<>();
    private ColorPresetType lastType;

    public PresetsSelector(int width, Color[] presets, State<ColorPresetType> type, State<Color> state, boolean withAlpha, Identifier background) {
        super(width, (width / 8 * 2) + 6);
        this.presets = presets;
        this.type = type;
        this.state = state;
        this.getColors();
        this.withAlpha = withAlpha;
        this.background = background;
    }

    private Collection<Color> getColors() {
        if (this.lastType != this.type.get()) {
            this.colors = switch (this.type.get()) {
                case DEFAULTS -> {
                    List<Color> colors = new ArrayList<>();
                    for (Color preset : this.presets) {
                        int color = withAlpha ? preset.getValue() : preset.getValue() | 0xFF000000;
                        colors.add(new Color(color));
                    }
                    yield colors;
                }
                case RECENTS -> RecentColorStorage.getRecentColors(this.withAlpha);
                case MC_COLORS -> List.of(MinecraftColors.COLORS);
            };
            this.lastType = this.type.get();
        }
        return this.colors;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, background, this.getX(), this.getY(), this.getWidth(), this.getHeight());
        int size = (this.getWidth() - 18) / 8;

        int i = 0;
        for (Color color : getColors()) {
            if (i >= 16) break; // Only render the first 16 colors 2x8 grid
            int j = i % 8;
            int k = i / 8;
            int x = this.getX() + 4 + (j * size) + (2 * j);
            int y = this.getY() + 4 + (k * size) + (2 * k);
            int rgba = color.getValue();
            if (!withAlpha) rgba |= 0xFF000000;
            graphics.fill(x, y, x + size, y + size, rgba);
            graphics.renderOutline(x, y, size, size, 0xFFDDDDDD);
            if (mouseX >= x && mouseX <= x + size && mouseY >= y && mouseY <= y + size) {
                graphics.renderOutline(x, y, size, size, 0xFF000000);
                Screen screen = Minecraft.getInstance().screen;
                if (screen != null) {
                    if (!withAlpha) rgba &= 0x00FFFFFF;
                    var text = Component.literal("[").withColor(rgba | 0xFF000000)
                            .append(Component.literal(String.format(Locale.ROOT, "#%06X", rgba)).withColor(0xFFFFFFFF))
                            .append(Component.literal("]")).withColor(rgba | 0xFF000000);

                    graphics.setTooltipForNextFrame(text, mouseX, mouseY);
                }
            }
            i++;
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (event.input() != 0) return false;
        int size = (this.getWidth() - 18) / 8;
        int i = 0;
        for (Color color : getColors()) {
            if (i >= 16) break;
            int j = i % 8;
            int k = i / 8;
            int x = this.getX() + 3 + (j * size) + (2 * j);
            int y = this.getY() + 3 + (k * size) + (2 * k);
            if (event.x() >= x && event.x() <= x + size && event.y() >= y && event.y() <= y + size) {
                RecentColorStorage.add(this.state.get());
                this.lastType = null;
                this.state.set(new Color(color.getValue()));
                return true;
            }
            i++;
        }
        return false;
    }
}

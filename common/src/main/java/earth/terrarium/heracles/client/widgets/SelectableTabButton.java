package earth.terrarium.heracles.client.widgets;

import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.client.widgets.buttons.ThemedButton;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class SelectableTabButton extends AbstractButton implements ThemedButton {

    private final Runnable onSelect;
    private boolean selected;

    public SelectableTabButton(int x, int y, int width, int height, Component component, Runnable onSelect) {
        super(x, y, width, height, component);
        this.onSelect = onSelect;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public void onPress() {
        if (!this.selected && this.onSelect != null) {
            this.onSelect.run();
        }
        this.selected = true;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }

    @Override
    public int getTextureY(boolean active, boolean hovered) {
        return 60 + (selected ? 40 : 0) + (hovered ? 20 : 0);
    }

    @Override
    public int getTextColor(boolean active, float alpha) {
        return QuestScreenTheme.getTabButton(selected) | Mth.ceil(alpha * 255.0F) << 24;
    }
}

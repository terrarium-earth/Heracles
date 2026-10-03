package earth.terrarium.heracles.client.components.widgets.buttons;

import com.teamresourceful.resourcefullib.client.components.CursorWidget;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import earth.terrarium.heracles.client.components.widgets.WidgetSprites;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SpriteButton extends Button implements CursorWidget {

    protected WidgetSprites sprites;

    public SpriteButton(int width, int height, WidgetSprites sprites) {
        super(0, 0, width, height, CommonComponents.EMPTY, button -> {}, DEFAULT_NARRATION);
        this.sprites = sprites;
    }


    public SpriteButton(int width, int height, WidgetSprites sprites, OnPress onPress) {
        super(0, 0, width, height, CommonComponents.EMPTY, onPress, DEFAULT_NARRATION);
        this.sprites = sprites;
    }

    public static SpriteButton create(int width, int height, WidgetSprites sprites, OnPress onPress) {
        return new SpriteButton(width, height, sprites, onPress);
    }

    public static SpriteButton create(int width, int height, WidgetSprites sprites, Runnable onPress) {
        return SpriteButton.create(width, height, sprites, (button) -> onPress.run());
    }

    public static SpriteButton create(int width, int height, WidgetSprites sprites) {
        return new SpriteButton(width, height, sprites);
    }

    public SpriteButton withTooltip(Component component) {
        this.setTooltip(Tooltip.create(component));
        return this;
    }

    public SpriteButton withSprites(WidgetSprites sprites) {
        this.sprites = sprites;
        return this;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        ResourceLocation sprite = this.sprites.get(this.isHovered(), !this.active);
        graphics.blit(sprite, getX(), getY(), 0, 0, this.width, this.height, this.width, this.height);
    }

    @Override
    public CursorScreen.Cursor getCursor() {
        return !this.isActive() ? CursorScreen.Cursor.DISABLED : CursorScreen.Cursor.POINTER;
    }
}

package earth.terrarium.heracles.client.components.lists;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.Heracles;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public interface BaseListEntry<T> extends ListEntry<T> {

    ResourceLocation BACKGROUND_LEFT = Heracles.id("lists/entry_left");
    ResourceLocation BACKGROUND_RIGHT = Heracles.id("lists/entry_right");

    @Override
    @MustBeInvokedByOverriders
    default void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        int height = getHeight(width);
        RenderSystem.enableBlend();
        graphics.blitSprite(BACKGROUND_LEFT, x, y, width, height);
        graphics.blitSprite(BACKGROUND_RIGHT, x, y, width, height);
        RenderSystem.disableBlend();
    }
}

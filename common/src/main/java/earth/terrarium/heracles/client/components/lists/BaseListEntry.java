package earth.terrarium.heracles.client.components.lists;

import com.mojang.blaze3d.systems.RenderSystem;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.Heracles;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public interface BaseListEntry<T> extends ListEntry<T> {

    ResourceLocation BACKGROUND = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/lists/entry.png");
    ResourceLocation BACKGROUND_HOVERED = new ResourceLocation(Heracles.MOD_ID, "textures/gui/sprites/lists/entry.png");

    @Override
    @MustBeInvokedByOverriders
    default void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        int height = getHeight(width);
        RenderSystem.enableBlend();
        graphics.blitNineSliced(BACKGROUND, x, y, 42, height, 3, 42, 42, 0, 0);
        graphics.blitNineSliced(BACKGROUND, x + 42, y, width - 42, height, 3, 86, 42, 42, 0);
        RenderSystem.disableBlend();
    }
}

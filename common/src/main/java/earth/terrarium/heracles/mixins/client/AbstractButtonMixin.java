package earth.terrarium.heracles.mixins.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import earth.terrarium.heracles.client.widgets.buttons.ThemedButton;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractButton.class)
public abstract class AbstractButtonMixin extends AbstractWidget {
    public AbstractButtonMixin(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @WrapWithCondition(
        method = "renderWidget",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;blitSprite(Lnet/minecraft/resources/ResourceLocation;IIII)V"
        )
    )
    public boolean heracles$applyThemedTexture(
        GuiGraphics graphics, ResourceLocation sprite, int x, int y, int width, int height
    ) {
        if (((Object) this) instanceof ThemedButton tb) {
            graphics.blitSprite(
                tb.getSprite(this.active, this.isHoveredOrFocused()),
                this.getX(), this.getY(),
                this.getWidth(), this.getHeight()
            );
            return false;
        }
        return true;
    }

    @WrapWithCondition(
        method = "renderWidget",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/AbstractButton;renderString(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;I)V"
        )
    )
    public boolean heracles$applyThemedColor(AbstractButton button, GuiGraphics guiGraphics, Font font, int color) {
        if (((Object) this) instanceof ThemedButton tb) {
            button.renderString(guiGraphics, font, tb.getTextColor(this.active, this.alpha));
            return false;
        }
        return true;
    }

}

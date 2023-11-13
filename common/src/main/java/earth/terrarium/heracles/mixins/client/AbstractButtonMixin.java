package earth.terrarium.heracles.mixins.client;

import earth.terrarium.heracles.client.widgets.buttons.ThemedButton;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(AbstractButton.class)
public abstract class AbstractButtonMixin extends AbstractWidget {
    public AbstractButtonMixin(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @ModifyArgs(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blitNineSliced(Lnet/minecraft/resources/ResourceLocation;IIIIIIIIII)V"))
    public void applyThemedTexture(Args args) {
        if (((Object) this) instanceof ThemedButton tb) {
            ThemedButton.TextureBounds bounds = tb.getTextureBounds(this.active, this.isHoveredOrFocused());
            args.setAll(tb.getTexture(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), bounds.sliceWidth(), bounds.sliceHeight(), bounds.sourceWidth(), bounds.sourceHeight(), bounds.sourceX(), bounds.sourceY());
        }
    }

    @ModifyArg(method = "renderWidget", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractButton;renderString(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;I)V"), index = 2)
    public int applyThemedColor(int original) {
        if (((Object) this) instanceof ThemedButton tb) {
            return tb.getTextColor(this.active, this.alpha);
        }
        return original;
    }
}

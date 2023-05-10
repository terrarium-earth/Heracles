package earth.terrarium.heracles.mixins;

import earth.terrarium.heracles.client.widgets.base.FileWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Shadow
    public abstract List<? extends GuiEventListener> children();

    @Inject(
        method = "onFilesDrop",
        at = @At("HEAD")
    )
    private void heracles$onFileDropped(List<Path> packs, CallbackInfo ci) {
        for (GuiEventListener child : children()) {
            if (child instanceof FileWidget widget) {
                widget.onFilesDrop(packs);
            }
        }
    }
}

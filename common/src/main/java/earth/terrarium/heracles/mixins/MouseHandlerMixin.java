package earth.terrarium.heracles.mixins;

import earth.terrarium.heracles.client.screens.pinned.PinnedQuestDisplay;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @Inject(
        method = "method_1611",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;mouseClicked(DDI)Z"
        ),
        cancellable = true
    )
    private static void heracles$onMouseClicked(boolean[] bls, Screen screen, double mouseX, double mouseY, int button, CallbackInfo ci) {
        if (PinnedQuestDisplay.click(mouseX, mouseY)) {
            ci.cancel();
        }
    }
}

package earth.terrarium.heracles.mixins;

import earth.terrarium.heracles.client.screens.InternalKeyPressHook;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(AbstractContainerScreen.class)
public abstract class InternalKeyPressedScreen extends Screen implements InternalKeyPressHook {

    protected InternalKeyPressedScreen(Component component) {
        super(component);
    }

    @Override
    public boolean heracles$internalKeyPressed(int keyCode, int scanCode, int modifiers) {
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

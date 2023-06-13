package earth.terrarium.heracles.client.widgets.boxes;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class EnterableEditBox extends EditBox {

    private Consumer<String> enter;

    public EnterableEditBox(Font font, int i, int j, int k, int l, Component component) {
        super(font, i, j, k, l, component);
    }

    public EnterableEditBox(Font font, int i, int j, int k, int l, @Nullable EditBox editBox, Component component) {
        super(font, i, j, k, l, editBox, component);
    }

    public EnterableEditBox setEnter(Consumer<String> enter) {
        this.enter = enter;
        return this;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (canConsumeInput()) {
            if (keyCode == InputConstants.KEY_RETURN || keyCode == InputConstants.KEY_NUMPADENTER) {
                if (enter != null) {
                    enter.accept(getValue());
                }
                return true;
            }
        }
        return false;
    }
}

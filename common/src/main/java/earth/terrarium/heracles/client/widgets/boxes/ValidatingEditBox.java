package earth.terrarium.heracles.client.widgets.boxes;

import com.mojang.blaze3d.vertex.PoseStack;
import earth.terrarium.heracles.client.widgets.base.ValidatingWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

public class ValidatingEditBox extends EditBox implements ValidatingWidget {

    private final Predicate<String> validator;

    public ValidatingEditBox(Font font, int x, int y, int width, int height, Component message, Predicate<String> validator) {
        super(font, x, y, width, height, message);
        this.validator = validator;
        setBordered(false);
    }

    @Override
    public void renderWidget(PoseStack poseStack, int i, int j, float f) {
        if (this.isVisible()) {
            int k = this.isFocused() ? this.validator.test(getValue()) ? -1 : 0xFFFF0000 : -6250336;
            fill(poseStack, this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, k);
            fill(poseStack, this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -16777216);
        }
        super.renderWidget(poseStack, i, j, f);
    }

    @Override
    public int getInnerWidth() {
        return this.width - 8;
    }

    @Override
    public boolean isValid() {
        return this.validator.test(getValue());
    }
}

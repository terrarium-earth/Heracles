package earth.terrarium.heracles.client.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

import java.util.OptionalInt;
import java.util.function.IntConsumer;

public class NumberEditBox extends EditBox {

    public NumberEditBox(Font font, int x, int y, int width, int height, Component message) {
        super(font, x, y, width, height, message);
        setBordered(true);

        setFilter(s -> {
            if (s.isEmpty() || s.equals("-")) {
                return true;
            }
            try {
                Integer.parseInt(s);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        });
    }

    public void setNumberResponder(IntConsumer responder) {
        setResponder(s -> {
            if (s.isEmpty() || s.equals("-")) {
                responder.accept(0);
            } else {
                responder.accept(Integer.parseInt(s));
            }
        });
    }

    @Override
    public void renderWidget(PoseStack poseStack, int i, int j, float f) {
        Font font = Minecraft.getInstance().font;
        if (isVisible()) {
            int textWidth = font.width(getMessage());
            font.draw(poseStack, getMessage(), getX() - textWidth - 4, getY() + (this.height - font.lineHeight - 1) / 2f, isFocused() ? 0xffffff : 0x808080);
//            if (isFocused()) {
//                renderOutline(poseStack, this.getX() - 2, this.getY() - 2, this.width + 2, this.height + 2, 0xffffffff);
//            }
        }
        super.renderWidget(poseStack, i, j, f);
    }

    @Override
    public int getInnerWidth() {
        return this.width - 8;
    }

    public void setIfNotFocused(int value) {
        if (!isFocused()) {
            getIntValue().ifPresentOrElse(current -> {
                if (current != value) {
                    setValue(Integer.toString(value));
                }
            }, () -> {
                setValue(Integer.toString(value));
            });
        }
    }

    public OptionalInt getIntValue() {
        if (getValue().isEmpty()) {
            return OptionalInt.empty();
        }
        try {
            return OptionalInt.of(Integer.parseInt(getValue()));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }
}

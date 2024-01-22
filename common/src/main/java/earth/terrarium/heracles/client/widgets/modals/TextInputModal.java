package earth.terrarium.heracles.client.widgets.modals;

import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.client.theme.EditorTheme;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import earth.terrarium.heracles.client.widgets.boxes.EnterableEditBox;
import earth.terrarium.heracles.client.widgets.buttons.ThemedButton;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class TextInputModal<T> extends BaseModal {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/text.png");
    private static final int WIDTH = 168;
    private static final int HEIGHT = 57;

    private final Component title;
    private final EnterableEditBox editBox;

    private T data;
    private BiConsumer<T, String> callback;

    public TextInputModal(int screenWidth, int screenHeight, Component title, BiConsumer<T, String> callback, Predicate<String> validator) {
        super(screenWidth, screenHeight, WIDTH, HEIGHT);
        this.title = title;
        this.callback = callback;
        editBox = addChild(new EnterableEditBox(Minecraft.getInstance().font, this.x + 8, this.y + 19, 152, 14, Component.nullToEmpty("Group Name")));
        editBox.setEnter(value -> {
            if (!value.isBlank()) {
                this.callback.accept(this.data, value);
                editBox.setValue("");
                this.hide();
            }
        });

        var submitButton = addChild(createButton(ConstantComponents.SUBMIT, this.x + WIDTH - 7, this.y + HEIGHT - 20, b -> {
            if (!editBox.getValue().isBlank()) {
                this.callback.accept(this.data, editBox.getValue());
                editBox.setValue("");
                this.hide();
            }
        }));
        submitButton.active = false;
        addChild(createButton(CommonComponents.GUI_CANCEL, submitButton.getX() - 2, this.y + HEIGHT - 20, b ->
            this.hide()
        ));
        editBox.setMaxLength(32);
        editBox.setResponder(s -> submitButton.active = !s.trim().isEmpty() && validator.test(s.trim()));
    }

    @Override
    protected void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(TEXTURE, this.x, this.y, 0, 0, this.width, this.height, 256, 256);
        renderChildren(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        graphics.drawString(
            font,
            this.title, this.x + 8, this.y + 6, EditorTheme.getModalTextTitle(),
            false
        );
    }

    private Button createButton(Component component, int x, int y, Button.OnPress onPress) {
        int width = Minecraft.getInstance().font.width(component) + 8;
        return ThemedButton.builder(component, onPress)
            .bounds(x - width, y, width, 15)
            .build();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!isVisible()) return false;
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        int x = screenWidth / 2 - (WIDTH / 2);
        int y = screenHeight / 2 - (HEIGHT / 2);

        if (mouseX < x || mouseX > x + WIDTH || mouseY < y || mouseY > y + HEIGHT) {
            setVisible(false);
        }
        return true;
    }

    public void setText(String text) {
        editBox.setValue(text);
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setCallback(BiConsumer<T, String> callback) {
        this.callback = callback;
    }
}

package earth.terrarium.heracles.client.widgets.modals;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.QuestActionPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

public class TextInputModal<T> extends BaseModal {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/text.png");
    private static final int WIDTH = 168;
    private static final int HEIGHT = 57;

    private final Component title;

    private T data;

    public TextInputModal(int screenWidth, int screenHeight, Component title, BiConsumer<T, String> callback, Predicate<String> validator) {
        super(screenWidth, screenHeight, WIDTH, HEIGHT);
        this.title = title;
        var editBox = addChild(new EditBox(Minecraft.getInstance().font, this.x + 8, this.y + 19, 152, 14, Component.nullToEmpty("Group Name")));
        var submitButton = addChild(createButton(Component.nullToEmpty("Submit"), this.x + WIDTH - 7, this.y + HEIGHT - 20, b -> {
            if (editBox != null && !editBox.getValue().isBlank()) {
                callback.accept(this.data, editBox.getValue());
                editBox.setValue("");
                visible = false;
            }
        }));
        submitButton.active = false;
        addChild(createButton(Component.nullToEmpty("Cancel"), submitButton.getX() - 2, this.y + HEIGHT - 20, b ->
            this.visible = false
        ));
        editBox.setMaxLength(32);
        editBox.setResponder(s -> submitButton.active = !s.trim().isEmpty() && validator.test(s.trim()));
    }

    @Override
    protected void renderBackground(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        RenderUtils.bindTexture(TEXTURE);

        Gui.blit(pose, this.x, this.y, 0, 0, this.width, this.height, 256, 256);

        renderChildren(pose, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderForeground(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        font.draw(pose, this.title, x + 8, y + 6, 0x404040);
    }

    private Button createButton(Component component, int x, int y, Button.OnPress onPress) {
        int width = Minecraft.getInstance().font.width(component) + 8;
        return Button.builder(component, onPress)
            .bounds(x - width, y, width, 15)
            .build();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!visible) return false;
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        int x = screenWidth / 2 - (WIDTH / 2);
        int y = screenHeight / 2 - (HEIGHT / 2);

        if (mouseX < x || mouseX > x + WIDTH || mouseY < y || mouseY > y + HEIGHT) {
            setVisible(false);
        }
        return true;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return visible &&
            mouseX >= (screenWidth / 2f) - (WIDTH / 2f) && mouseX <= (screenWidth / 2f) + (WIDTH / 2f) &&
            mouseY >= (screenHeight / 2f) - (HEIGHT / 2f) && mouseY <= (screenHeight / 2f) + (HEIGHT / 2f);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            NetworkHandler.CHANNEL.sendToServer(new QuestActionPacket(QuestActionPacket.Action.SAVE));
        }
    }

    public void setData(T data) {
        this.data = data;
    }
}

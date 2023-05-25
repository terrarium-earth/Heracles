package earth.terrarium.heracles.client.widgets.modals;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.widgets.Dropdown;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import earth.terrarium.heracles.client.widgets.boxes.PlaceholerEditBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;

public class CreateObjectModal extends BaseModal {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/create_object.png");
    private static final int WIDTH = 168;
    private static final int HEIGHT = 84;

    private final Dropdown<ResourceLocation> typeBox;
    private final EditBox nameBox;

    private Component title = CommonComponents.EMPTY;
    private BiConsumer<ResourceLocation, String> callback = (r, s) -> {};
    private BiPredicate<ResourceLocation, String> validator = (r, s) -> true;

    public CreateObjectModal(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight, WIDTH, HEIGHT);
        this.typeBox = addChild(new Dropdown<>(
            this.x + 8, this.y + 19, 152, 14,
            "Type",
            ResourceLocation::toString
        ));

        Button submitButton = addChild(createButton(Component.nullToEmpty("Submit"), this.x + WIDTH - 7, this.y + HEIGHT - 20, b -> onSubmit()));
        submitButton.active = false;
        addChild(createButton(Component.nullToEmpty("Cancel"), submitButton.getX() - 2, this.y + HEIGHT - 20, b ->
            this.visible = false
        ));
        this.nameBox = addChild(new PlaceholerEditBox(Minecraft.getInstance().font, this.x + 8, this.y + 46, 152, 14, Component.literal("Identifier")));
        BooleanSupplier valid = () -> !nameBox.getValue().trim().isEmpty() && this.typeBox.value() != null && validator.test(this.typeBox.value(), nameBox.getValue().trim());
        nameBox.setMaxLength(32);
        nameBox.setResponder(s -> submitButton.active = valid.getAsBoolean());
        this.typeBox.setResponder(s -> submitButton.active = valid.getAsBoolean());
    }

    private void onSubmit() {
        boolean nameValid = this.nameBox != null && !nameBox.getValue().isBlank();
        boolean typeValid = this.typeBox != null && this.typeBox.value() != null;

        if (typeValid && nameValid) {
            this.callback.accept(this.typeBox.value(), nameBox.getValue());
            nameBox.setValue("");
            this.typeBox.setSelectedOption(null);
            visible = false;
        }
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

    public void update(
        BiConsumer<ResourceLocation, String> callback,
        BiPredicate<ResourceLocation, String> validator,
        Component title,
        Collection<ResourceLocation> suggestions
    ) {
        this.callback = callback;
        this.validator = validator;
        this.title = title;
        this.typeBox.setOptions(suggestions);
    }
}

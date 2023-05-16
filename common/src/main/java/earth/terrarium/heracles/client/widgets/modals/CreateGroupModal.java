package earth.terrarium.heracles.client.widgets.modals;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.ClientQuests;
import earth.terrarium.heracles.client.screens.quests.GroupsList;
import earth.terrarium.heracles.client.screens.quests.QuestsScreen;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.CreateGroupPacket;
import earth.terrarium.heracles.common.network.packets.QuestActionPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CreateGroupModal extends BaseModal {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/create.png");
    private static final int WIDTH = 168;
    private static final int HEIGHT = 57;

    public CreateGroupModal(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight, WIDTH, HEIGHT);
        var editBox = addChild(new EditBox(Minecraft.getInstance().font, this.x + 8, this.y + 19, 152, 14, Component.nullToEmpty("Group Name")));
        var submitButton = addChild(createButton(Component.nullToEmpty("Submit"), this.x + WIDTH - 7, this.y + HEIGHT - 20, b -> {
            if (editBox != null && !editBox.getValue().isBlank()) {
                NetworkHandler.CHANNEL.sendToServer(new CreateGroupPacket(editBox.getValue()));
                ClientQuests.groups().add(editBox.getValue());
                if (Minecraft.getInstance().screen instanceof QuestsScreen screen) {
                    screen.getGroupsList().addEntry(new GroupsList.Entry(editBox.getValue()));
                }
                editBox.setValue("");
                visible = false;
            }
        }));
        submitButton.active = false;
        addChild(createButton(Component.nullToEmpty("Cancel"), submitButton.getX() - 2, this.y + HEIGHT - 20, b ->
            this.visible = false
        ));
        editBox.setMaxLength(32);
        editBox.setResponder(s -> submitButton.active = !s.trim().isEmpty() && !ClientQuests.groups().contains(s.trim()));
    }

    @Override
    protected void renderBackground(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        RenderUtils.bindTexture(TEXTURE);

        pose.pushPose();
        pose.translate(0, 0, 150);
        Gui.fill(pose, 0, 15, this.screenWidth, this.screenHeight, 0x80000000);
        Gui.blit(pose, this.x, this.y, 0, 0, this.width, this.height, 256, 256);

        renderChildren(pose, mouseX, mouseY, partialTick);

    }

    @Override
    protected void renderForeground(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        font.draw(pose, "Create Group", x + 8, y + 6, 0x404040);

        pose.popPose();
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
}

package earth.terrarium.heracles.client.widgets.modals.upload;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import earth.terrarium.heracles.client.widgets.base.FileWidget;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.QuestActionPacket;
import earth.terrarium.heracles.common.network.packets.UploadQuestPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UploadModal extends BaseModal implements FileWidget {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/uploading.png");
    private static final int WIDTH = 168;
    private static final int HEIGHT = 173;

    private double scrollAmount = 0;

    private final List<UploadModalItem> items = new ArrayList<>();

    public UploadModal(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight, WIDTH, HEIGHT);
        var submitButton = addChild(createButton(Component.nullToEmpty("Submit"), this.x + WIDTH - 7, this.y + HEIGHT - 20, b -> {
            var iterator = items.iterator();
            while (iterator.hasNext()) {
                var item = iterator.next();
                if (!item.isErrored()) {
                    Quest quest = item.quest();
                    String filename = item.path().getFileName().toString();
                    NetworkHandler.CHANNEL.sendToServer(new UploadQuestPacket(filename.substring(0, filename.lastIndexOf(".")), quest));
                    iterator.remove();
                }
            }
            if (items.isEmpty()) {
                setVisible(false);
            }
        }));
        addChild(createButton(Component.nullToEmpty("Cancel"), submitButton.getX() - 2, this.y + HEIGHT - 20, b -> {
            items.clear();
            visible = false;
        }));
    }

    @Override
    protected void renderBackground(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        RenderUtils.bindTexture(TEXTURE);

        pose.pushPose();
        pose.translate(0, 0, 150);
        Gui.fill(pose, 0, 15, screenWidth, screenHeight, 0x80000000);
        Gui.blit(pose, x, y, 0, 0, WIDTH, HEIGHT, 256, 256);

        renderChildren(pose, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderForeground(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        font.draw(pose, "Import Quests", x + 8, y + 6, 0x404040);

        int y = this.y + 19;
        int x = this.x + 8;
        int tempY = y;
        tempY -= scrollAmount;

        try (var scissor = RenderUtils.createScissorBoxStack(new ScissorBoxStack(), Minecraft.getInstance(), pose, x, y, 152, 130)) {
            for (UploadModalItem item : items) {
                boolean hovering = mouseY >= y && mouseY <= y + 148;
                boolean hoveringRemove = mouseX >= x + UploadModalItem.WIDTH - 11 && mouseX <= x + UploadModalItem.WIDTH - 2 && mouseY >= tempY + 2 && mouseY <= tempY + 11;
                item.render(pose, scissor.stack(), x, tempY, mouseX, mouseY, hovering, hoveringRemove);
                tempY += 28;
            }
        }

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
            if (this.items.isEmpty()) {
                setVisible(false);
            }
        }

        y += 19;
        x += 8;
        int tempY = y;
        tempY -= scrollAmount;

        var iterator = items.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            if (mouseY >= y && mouseY <= y + 148) {
                boolean hoveringRemove = mouseX >= x + UploadModalItem.WIDTH - 11 && mouseX <= x + UploadModalItem.WIDTH - 2 && mouseY >= tempY + 2 && mouseY <= tempY + 11;
                if (hoveringRemove) {
                    iterator.remove();
                }
            }
            tempY += 28;
        }

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        this.scrollAmount = Mth.clamp(this.scrollAmount - scrollAmount * 10, 0.0D, Math.max(0, (this.items.size() * 28) - 130));
        return true;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return visible &&
            mouseX >= (screenWidth / 2f) - (WIDTH / 2f) && mouseX <= (screenWidth / 2f) + (WIDTH / 2f) &&
            mouseY >= (screenHeight / 2f) - (HEIGHT / 2f) && mouseY <= (screenHeight / 2f) + (HEIGHT / 2f);
    }

    @Override
    public void onFilesDrop(List<Path> paths) {
        for (Path path : paths) {
            items.add(UploadModalItem.of(path));
        }
        items.sort((first, second) -> Boolean.compare(first.isErrored(), second.isErrored()));
        Collections.reverse(items);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            NetworkHandler.CHANNEL.sendToServer(new QuestActionPacket(QuestActionPacket.Action.SAVE));
        }
    }
}

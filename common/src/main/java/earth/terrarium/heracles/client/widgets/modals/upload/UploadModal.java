package earth.terrarium.heracles.client.widgets.modals.upload;

import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.client.handlers.ClientQuestNetworking;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import earth.terrarium.heracles.client.widgets.base.FileWidget;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
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
        var submitButton = addChild(createButton(ConstantComponents.SUBMIT, this.x + WIDTH - 7, this.y + HEIGHT - 20, b -> {
            var iterator = items.iterator();
            while (iterator.hasNext()) {
                var item = iterator.next();
                if (!item.isErrored()) {
                    Quest quest = item.quest();
                    String filename = item.path().getFileName().toString();
                    ClientQuestNetworking.add(filename.substring(0, filename.lastIndexOf(".")), quest);
                    iterator.remove();
                }
            }
            if (items.isEmpty()) {
                setVisible(false);
            }
        }));
        addChild(createButton(CommonComponents.GUI_CANCEL, submitButton.getX() - 2, this.y + HEIGHT - 20, b -> {
            items.clear();
            this.hide();
        }));
    }

    @Override
    protected void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(TEXTURE, x, y, 0, 0, WIDTH, HEIGHT, 256, 256);
        renderChildren(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.drawString(
            Minecraft.getInstance().font,
            "Import Quests", x + 8, y + 6, 0x404040,
            false
        );

        int y = this.y + 19;
        int x = this.x + 8;
        int tempY = y;
        tempY -= scrollAmount;

        try (var scissor = RenderUtils.createScissor(Minecraft.getInstance(), graphics, x, y, 152, 130)) {
            for (UploadModalItem item : items) {
                boolean hovering = mouseY >= y && mouseY <= y + 148;
                boolean hoveringRemove = mouseX >= x + UploadModalItem.WIDTH - 11 && mouseX <= x + UploadModalItem.WIDTH - 2 && mouseY >= tempY + 2 && mouseY <= tempY + 11;
                item.render(graphics, scissor.stack(), x, tempY, mouseX, mouseY, hovering, hoveringRemove);
                tempY += 28;
            }
        }
    }

    private Button createButton(Component component, int x, int y, Button.OnPress onPress) {
        int width = Minecraft.getInstance().font.width(component) + 8;
        return Button.builder(component, onPress)
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
    public void onFilesDrop(List<Path> paths) {
        for (Path path : paths) {
            items.add(UploadModalItem.of(path));
        }
        items.sort((first, second) -> Boolean.compare(first.isErrored(), second.isErrored()));
        Collections.reverse(items);
    }
}

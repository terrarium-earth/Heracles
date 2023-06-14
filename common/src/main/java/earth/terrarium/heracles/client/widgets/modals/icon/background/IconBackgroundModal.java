package earth.terrarium.heracles.client.widgets.modals.icon.background;

import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.widgets.base.BaseModal;
import earth.terrarium.heracles.client.widgets.modals.upload.UploadModalItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class IconBackgroundModal extends BaseModal {

    public static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/uploading.png");
    private static final int WIDTH = 168;
    private static final int HEIGHT = 173;

    private double scrollAmount = 0;

    private final List<BackgroundModalItem> items = new ArrayList<>();
    private Consumer<ResourceLocation> callback;

    public IconBackgroundModal(int screenWidth, int screenHeight) {
        super(screenWidth, screenHeight, WIDTH, HEIGHT);
    }

    @Override
    protected void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(TEXTURE, x, y, 0, 0, WIDTH, HEIGHT, 256, 256);
        renderChildren(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderForeground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;
        int textX = (WIDTH - font.width("Choose Background")) / 2;
        graphics.drawString(
            font,
            "Choose Background", x + textX, y + 6, 0x404040,
            false
        );

        int y = this.y + 19;
        int x = this.x + 8;
        int tempY = y;
        tempY -= scrollAmount;

        try (var scissor = RenderUtils.createScissor(Minecraft.getInstance(), graphics, x, y, 152, 130)) {
            for (BackgroundModalItem item : items) {
                boolean hovering = mouseY >= y && mouseY <= y + 148;
                item.render(graphics, scissor.stack(), x, tempY, mouseX, mouseY, hovering);
                tempY += 28;
            }
        }
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

        y += 19;
        x += 8;
        int tempY = y;
        tempY -= scrollAmount;

        for (BackgroundModalItem item : items) {
            if (mouseY >= y && mouseY <= y + 148) {
                if (mouseX >= x && mouseX <= x + UploadModalItem.WIDTH && mouseY >= tempY && mouseY <= tempY + 28) {
                    if (callback != null) {
                        callback.accept(item.texture());
                    }
                    return true;
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

    public void update(Collection<ResourceLocation> textures, Consumer<ResourceLocation> callback) {
        this.items.clear();
        textures.forEach(texture -> this.items.add(new BackgroundModalItem(texture)));
        this.callback = callback;
    }
}

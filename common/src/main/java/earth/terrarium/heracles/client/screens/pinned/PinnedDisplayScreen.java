package earth.terrarium.heracles.client.screens.pinned;

import com.mojang.blaze3d.systems.RenderSystem;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import org.joml.Vector2i;

public class PinnedDisplayScreen extends Screen {

    private int sectionWidth = 0;
    private int sectionHeight = 0;

    private boolean dragging = false;

    private int index = DisplayConfig.pinnedIndex;

    private final Vector2i offset = new Vector2i();
    private final Vector2i start = new Vector2i();
    private final Vector2i startOffset = new Vector2i();

    public PinnedDisplayScreen() {
        super(CommonComponents.EMPTY);
    }

    @Override
    protected void init() {
        super.init();
        this.sectionWidth = this.width / 4;
        this.sectionHeight = this.height / 4;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.enableBlend();
        graphics.fill(0, 0, this.width, this.height, 0x80000000);
        for (int i = 0; i < 4; i++) {
            int y = i * this.sectionHeight;
            boolean hovered = dragging && mouseX < this.sectionWidth && mouseY > y && mouseY < y + this.sectionHeight;
            graphics.fill(0, y, this.sectionWidth, y + this.sectionHeight, hovered ? 0x80A0A0A0 : 0x80808080);
            graphics.renderOutline(0, y, this.sectionWidth, this.sectionHeight, 0xEE808080);
        }

        for (int i = 0; i < 4; i++) {
            int y = i * this.sectionHeight;
            boolean hovered = dragging && mouseX > this.width - this.sectionWidth && mouseY > y && mouseY < y + this.sectionHeight;
            graphics.fill(this.width - this.sectionWidth, y, this.width, y + this.sectionHeight, hovered ? 0x80A0A0A0 : 0x80808080);
            graphics.renderOutline(this.width - this.sectionWidth, y, this.sectionWidth, this.sectionHeight, 0xEE808080);
        }
        int popupX = dragging ? offset.x() : PinnedQuestDisplay.x(index, sectionWidth, this.width);
        int popupY = dragging ? offset.y() : PinnedQuestDisplay.y(index, sectionHeight - 20, this.height);

        renderFakePopup(graphics, popupX, popupY);
    }

    private void renderFakePopup(GuiGraphics graphics, int x, int y) {
        graphics.fill(x, y, x + this.sectionWidth, y + this.sectionHeight - 20, 0x80000000);
        graphics.drawString(
            font,
            "Pinned Quests", x + 5, y + 5, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            "Quest 1", x + 5, y + 14, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            "Quest 2", x + 5, y + 23, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            "Quest 3", x + 5, y + 32, 0xFFFFFFFF,
            false
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        start.set((int) mouseX, (int) mouseY);
        startOffset.set(PinnedQuestDisplay.x(index, sectionWidth, this.width), PinnedQuestDisplay.y(index, sectionHeight, this.height));
        dragging = true;
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (int i = 0; i < 4; i++) {
            int y = i * this.sectionHeight;
            if (mouseX < this.sectionWidth && mouseY > y && mouseY < y + this.sectionHeight) {
                index = i;
            }
        }
        for (int i = 0; i < 4; i++) {
            int y = i * this.sectionHeight;
            if (mouseX > this.width - this.sectionWidth && mouseY > y && mouseY < y + this.sectionHeight) {
                index = i + 4;
            }
        }
        offset.set(PinnedQuestDisplay.x(index, sectionWidth, this.width), PinnedQuestDisplay.y(index, sectionHeight, this.height));
        dragging = false;
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        int newX = (int) (mouseX - start.x() + startOffset.x());
        int newY = (int) (mouseY - start.y() + startOffset.y());
        offset.set(newX, newY);
        return true;
    }

    @Override
    public void removed() {
        super.removed();
        DisplayConfig.pinnedIndex = index;
        DisplayConfig.save();
    }
}

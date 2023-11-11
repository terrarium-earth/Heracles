package earth.terrarium.heracles.client.screens.pinned;

import com.mojang.blaze3d.systems.RenderSystem;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.utils.ClientUtils;
import earth.terrarium.heracles.client.utils.ThemeColors;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector2i;

import java.util.List;

public class PinnedDisplayScreen extends Screen {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Heracles.MOD_ID, "textures/gui/pinned.png");

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
        ClientUtils.blitTiling(graphics, TEXTURE, 0, 0, this.width, this.height, 0, 128, 128, 128);
        for (int x : List.of(0, this.width - this.sectionWidth)) {
            for (int i = 0; i < 4; i++) {
                int y = i * this.sectionHeight;
                boolean hovered = dragging && mouseX > x && mouseX < x + this.sectionWidth && mouseY > y && mouseY < y + this.sectionHeight;
                graphics.blitNineSliced(TEXTURE, x, y, this.sectionWidth, this.sectionHeight, 3, 64, 64, hovered ? 64 : 0, 64);
            }
        }
        RenderSystem.disableBlend();

        int popupX = dragging ? offset.x() : PinnedQuestDisplay.x(index, sectionWidth, this.width);
        int popupY = dragging ? offset.y() : PinnedQuestDisplay.y(index, sectionHeight - 19, this.height);
        renderFakePopup(graphics, popupX, popupY);
    }

    private void renderFakePopup(GuiGraphics graphics, int x, int y) {
        RenderSystem.enableBlend();
        graphics.blitNineSliced(TEXTURE, x + 1, y + 1, this.sectionWidth - 2, 10, 3, 64, 10, 0, 0);
        graphics.blitNineSliced(TEXTURE, x + 1, y + 11, this.sectionWidth - 2, this.sectionHeight - 30, 3, 64, 10, 0, 10);
        RenderSystem.disableBlend();
        graphics.drawString(
            font,
            ConstantComponents.PinnedQuests.TITLE, x + (this.sectionWidth - font.width(ConstantComponents.PinnedQuests.TITLE)) / 2, y + 3, ThemeColors.PINNED_TITLE,
            false
        );
        graphics.drawString(
            font,
            "Quest 1", x + 5, y + 14, ThemeColors.PINNED_QUEST,
            false
        );
        graphics.drawString(
            font,
            "Quest 2", x + 5, y + 23, ThemeColors.PINNED_QUEST,
            false
        );
        graphics.drawString(
            font,
            "Quest 3", x + 5, y + 32, ThemeColors.PINNED_QUEST,
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

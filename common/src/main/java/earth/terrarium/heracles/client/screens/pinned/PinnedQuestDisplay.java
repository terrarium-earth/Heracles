package earth.terrarium.heracles.client.screens.pinned;

import com.mojang.blaze3d.platform.Window;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.heracles.Heracles;
import earth.terrarium.heracles.client.handlers.DisplayConfig;
import earth.terrarium.heracles.client.handlers.PinnedQuests;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.List;

public class PinnedQuestDisplay {

    private static final ResourceLocation MOVE_ICON = new ResourceLocation(Heracles.MOD_ID, "textures/gui/move_icon.png");

    public static void render(GuiGraphics graphics) {
        Minecraft mc = Minecraft.getInstance();
        if (PinnedQuests.display().isEmpty()) return;
        if (mc.screen instanceof PinnedDisplayScreen) return;
        Window window = mc.getWindow();
        int mouseX = Mth.floor(mc.mouseHandler.xpos() * (double) window.getGuiScaledWidth() / (double) window.getScreenWidth());
        int mouseY = Mth.floor(mc.mouseHandler.ypos() * (double) window.getGuiScaledHeight() / (double) window.getScreenHeight());

        Font font = mc.font;
        List<PinnedDisplay> pinnedQuests = PinnedQuests.display();

        int x = x(DisplayConfig.pinnedIndex, PinnedQuests.width() + 10, mc.getWindow().getGuiScaledWidth());
        int y = y(DisplayConfig.pinnedIndex, PinnedQuests.height() + 2, mc.getWindow().getGuiScaledHeight());

        boolean hovered = mc.screen instanceof ChatScreen && mouseX >= x && mouseX <= x + PinnedQuests.width() + 10 && mouseY >= y && mouseY <= y + PinnedQuests.height() + 2;

        graphics.fill(x, y, x + PinnedQuests.width() + 10, y + PinnedQuests.height() + 2, 0x80000000);
        graphics.fill(x, y, x + PinnedQuests.width() + 10, y + 10, 0x80000000);
        graphics.fill(x, y + 10, x + PinnedQuests.width() + 10, y + 11, 0xff808080);
        if (hovered) {
            graphics.renderOutline(x, y, PinnedQuests.width() + 10, PinnedQuests.height() + 2, 0xFFFFFFFF);
        }

        int titleX = x + (PinnedQuests.width() + 10 - font.width(ConstantComponents.PinnedQuests.TITLE)) / 2;

        graphics.drawString(
            font,
            ConstantComponents.PinnedQuests.TITLE, titleX, y + 2, 0xFF808080,
            false
        );

        if (hovered) {
            graphics.blit(MOVE_ICON, x + PinnedQuests.width(), y + 1, 0, 0, 9, 9, 9, 9);
            if (mouseX >= x + PinnedQuests.width() && mouseX <= x + PinnedQuests.width() + 9 && mouseY >= y + 1 && mouseY <= y + 10) {
                ScreenUtils.setTooltip(ConstantComponents.PinnedQuests.MOVE);
            }
        }

        y += 12;

        var iterator = pinnedQuests.iterator();
        while (iterator.hasNext()) {
            PinnedDisplay display = iterator.next();
            boolean collapsed = PinnedQuests.isCollapsed(display.quest().key());
            MutableComponent title = collapsed ? ConstantComponents.ARROW_RIGHT.copy().append(" ") : ConstantComponents.ARROW_DOWN.copy().append(" ");
            title = title.append(display.title());
            graphics.drawString(
                font,
                title, x + 5, y, 0xFFFFFFFF,
                false
            );
            if (!PinnedQuests.isCollapsed(display.quest().key())) {
                y += 9;
                for (var task : display.tasks()) {
                    graphics.drawString(
                        font,
                        task, x + 5, y, 0xFFFFFFFF,
                        false
                    );
                    y += 9;
                }
            } else if (iterator.hasNext()) {
                y += 9;
            }
        }
    }

    public static boolean click(double mouseX, double mouseY) {
        if (!(Minecraft.getInstance().screen instanceof ChatScreen)) return false;
        List<PinnedDisplay> pinnedQuests = PinnedQuests.display();

        int x = x(DisplayConfig.pinnedIndex, PinnedQuests.width() + 10, Minecraft.getInstance().getWindow().getGuiScaledWidth());
        int y = y(DisplayConfig.pinnedIndex, PinnedQuests.height() + 2, Minecraft.getInstance().getWindow().getGuiScaledHeight());

        if (mouseX >= x + PinnedQuests.width() && mouseX <= x + PinnedQuests.width() + 10 && mouseY >= y && mouseY <= y + 10) {
            Minecraft.getInstance().setScreen(new PinnedDisplayScreen());
            return true;
        }

        y += 12;

        for (PinnedDisplay display : pinnedQuests) {
            if (mouseX >= x && mouseX <= x + PinnedQuests.width() && mouseY >= y && mouseY <= y + 9) {
                PinnedQuests.toggleCollapse(display.quest().key());
                return true;
            }
            if (!PinnedQuests.isCollapsed(display.quest().key())) {
                y += 9;
                y += 9 * display.tasks().size();
            }
        }
        return false;
    }

    public static int x(int value, int width, int screenWidth) {
        if (value >= 4) {
            return screenWidth - width;
        }
        return 0;
    }

    public static int y(int value, int height, int screenHeight) {
        float y = (value % 4) * (screenHeight / 4f);
        int newValue = (value % 4);
        if (newValue >= 2) {
            return Math.round(y + (screenHeight / 4f) - height);
        }
        return Math.round((value % 4) * (screenHeight / 4f));
    }
}

package earth.terrarium.heracles.client.screens.quest.tasks;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.utils.ModUtils;
import earth.terrarium.heracles.common.utils.ModUtils.QuestStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.Map;

public record TaskListHeadingWidget(float completion, Map<String, QuestStatus> quests) implements DisplayWidget {

    private static final String DESC_INCOMPLETE = "task.heracles.progress.desc.incomplete";
    private static final String DESC_COMPLETE = "task.heracles.progress.desc.complete";

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        graphics.fill(x, y, x + width, y + 30, 0xD0000000);
        graphics.renderOutline(x, y, width, 30, 0xFFFFFFFF);

        String desc = completion == 1.0F ? DESC_COMPLETE : DESC_INCOMPLETE;

        graphics.drawString(
            Minecraft.getInstance().font,
            ConstantComponents.Tasks.PROGRESS, x + 5, y + 5, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            Minecraft.getInstance().font,
            String.format("%.2f%%", this.completion * 100), x + width - 5 - Minecraft.getInstance().font.width(String.format("%.2f%%", this.completion * 100)), y + 5, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            Minecraft.getInstance().font,
            Component.translatable(desc, quests.values().stream().filter(qs -> qs == ModUtils.QuestStatus.COMPLETED).count()), x + 5, y + 25 - Minecraft.getInstance().font.lineHeight, 0xFF696969,
            false
        );
    }

    @Override
    public int getHeight(int width) {
        return 30;
    }
}

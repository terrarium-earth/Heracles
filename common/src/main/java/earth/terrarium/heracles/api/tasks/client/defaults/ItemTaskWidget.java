package earth.terrarium.heracles.api.tasks.client.defaults;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.tasks.QuestTaskDisplayFormatter;
import earth.terrarium.heracles.api.tasks.defaults.ItemQuestTask;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.NumericTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ItemTaskWidget(ItemQuestTask task, TaskProgress<NumericTag> progress,
                             List<ItemStack> stacks) implements DisplayWidget {

    private static final String TITLE_SINGULAR = "task.heracles.item.title.singular";
    private static final String TITLE_PLURAL = "task.heracles.item.title.plural";
    private static final String DESC_SINGULAR = "task.heracles.item.desc.singular";
    private static final String DESC_PLURAL = "task.heracles.item.desc.plural";

    public ItemTaskWidget(ItemQuestTask task, TaskProgress<NumericTag> progress) {
        this(task, progress, task.item().stream().map(ItemStack::new).toList());
    }

    @Override
    public void render(PoseStack pose, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        WidgetUtils.drawBackground(pose, x, y, width);
        int iconSize = (int) (width * 0.1f);
        ItemStack item = this.getCurrentItem();
        Minecraft.getInstance().getItemRenderer().renderGuiItem(
            pose, item, x + 5 + (int) (iconSize / 2f) - 8, y + 5 + (int) (iconSize / 2f) - 8
        );
        String title = this.task.target() == 1 ? TITLE_SINGULAR : TITLE_PLURAL;
        String desc = this.task.target() == 1 ? DESC_SINGULAR : DESC_PLURAL;
        Component name = getItemName();
        font.draw(pose, Component.translatable(title, name), x + iconSize + 10, y + 5, 0xFFFFFFFF);
        font.draw(pose, Component.translatable(desc, this.task.target(), name), x + iconSize + 10, y + 7 + font.lineHeight, 0xFF808080);
        String progress = QuestTaskDisplayFormatter.create(this.task, this.progress);
        font.draw(pose, progress, x + width - 5 - font.width(progress), y + 5, 0xFFFFFFFF);

        int progressY = y + 5 + (font.lineHeight + 2) * 2;
        WidgetUtils.drawProgressBar(pose, x + iconSize + 10, progressY + 2, x + width - 5, progressY + font.lineHeight - 2, this.task, this.progress);
    }

    private ItemStack getCurrentItem() {
        int index = Math.max(0, (int) ((System.currentTimeMillis() / 1000) % this.task.item().size()));
        return this.stacks.get(index);
    }

    private Component getItemName() {
        return switch (this.stacks.size()) {
            case 0 -> CommonComponents.EMPTY;
            case 1 -> this.stacks.get(0).getHoverName();
            default -> Component.literal("???");
        };
    }

    @Override
    public int getHeight(int width) {
        return (int) (width * 0.1f) + 10;
    }
}

package earth.terrarium.heracles.api.tasks.client.defaults;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.tasks.QuestTaskDisplayFormatter;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter;
import earth.terrarium.heracles.api.tasks.defaults.GatherItemTask;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NumericTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public record ItemTaskWidget(
    GatherItemTask task, TaskProgress<NumericTag> progress, List<ItemStack> stacks
) implements DisplayWidget {

    private static final String DESC_SINGULAR = "task.heracles.item.desc.singular";
    private static final String DESC_PLURAL = "task.heracles.item.desc.plural";

    public ItemTaskWidget(GatherItemTask task, TaskProgress<NumericTag> progress) {
        this(
            task,
            progress,
            task.item().getValue().map(
                item -> List.of(item.getDefaultInstance()),
                tag -> ModUtils.getValue(Registries.ITEM, tag).stream().map(ItemStack::new).toList()
            )
        );
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
        String desc = this.task.target() == 1 ? DESC_SINGULAR : DESC_PLURAL;
        font.draw(pose, TaskTitleFormatter.create(this.task), x + iconSize + 10, y + 5, 0xFFFFFFFF);
        font.draw(pose, Component.translatable(desc, this.task.target(), task.item().getDisplayName(Item::getDescription)), x + iconSize + 10, y + 7 + font.lineHeight, 0xFF808080);
        String progress = QuestTaskDisplayFormatter.create(this.task, this.progress);
        font.draw(pose, progress, x + width - 5 - font.width(progress), y + 5, 0xFFFFFFFF);

        int height = getHeight(width);
        WidgetUtils.drawProgressBar(pose, x + iconSize + 10, y + height - font.lineHeight + 2, x + width - 5, y + height - 2, this.task, this.progress);
    }

    private ItemStack getCurrentItem() {
        if (this.stacks.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int index = Math.max(0, (int) ((System.currentTimeMillis() / 1000) % this.stacks.size()));
        return this.stacks.get(index);
    }

    @Override
    public int getHeight(int width) {
        return (int) (width * 0.1f) + 10;
    }
}

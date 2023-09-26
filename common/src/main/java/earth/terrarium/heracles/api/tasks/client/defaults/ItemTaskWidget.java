package earth.terrarium.heracles.api.tasks.client.defaults;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter;
import earth.terrarium.heracles.api.tasks.defaults.GatherItemTask;
import earth.terrarium.heracles.common.constants.ConstantComponents;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import earth.terrarium.heracles.common.network.NetworkHandler;
import earth.terrarium.heracles.common.network.packets.tasks.ManualItemTaskPacket;
import earth.terrarium.heracles.common.utils.ModUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NumericTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public final class ItemTaskWidget implements DisplayWidget {

    private static final String DESC_ITEM = "task.heracles.item.desc.item";
    private static final String DESC_TAG = "task.heracles.item.desc.tag";
    private static final String DESC_SUBMIT_ITEM = "task.heracles.item.submit.desc.item";
    private static final String DESC_SUBMIT_TAG = "task.heracles.item.submit.desc.tag";

    private final String quest;
    private final GatherItemTask task;
    private final TaskProgress<NumericTag> progress;
    private final List<ItemStack> stacks;

    public ItemTaskWidget(String quest, GatherItemTask task, TaskProgress<NumericTag> progress) {
        this.quest = quest;
        this.task = task;
        this.progress = progress;
        this.stacks = task.item().getValue().map(
            item -> List.of(item.getDefaultInstance()),
            tag -> ModUtils.getValue(Registries.ITEM, tag).stream().map(ItemStack::new).toList()
        );
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        WidgetUtils.drawBackground(graphics, x, y, width);
        int iconSize = (int) (width * 0.1f);
        ItemStack item = this.getCurrentItem();
        graphics.renderFakeItem(item, x + 5 + (int) (iconSize / 2f) - 8, y + 5 + (int) (iconSize / 2f) - 8);
        String desc = task.collectionType() == GatherItemTask.CollectionType.AUTOMATIC ? (task.item().isTag() ? DESC_TAG : DESC_ITEM) : (task.item().isTag() ? DESC_SUBMIT_TAG : DESC_SUBMIT_ITEM);
        graphics.drawString(
            font,
            TaskTitleFormatter.create(this.task), x + iconSize + 10, y + 5, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            Component.translatable(desc, this.task.target(), task.item().getDisplayName(Item::getDescription)), x + iconSize + 10, y + 7 + font.lineHeight, 0xFF808080,
            false
        );
        WidgetUtils.drawProgressText(graphics, x, y, width, this.task, this.progress);
        int height = getHeight(width);
        WidgetUtils.drawProgressBar(graphics, x + iconSize + 10, y + height - font.lineHeight + 2, x + width - 5, y + height - 2, this.task, this.progress);

        if (task.collectionType() == GatherItemTask.CollectionType.MANUAL) {
            int buttonY = y + height - font.lineHeight - 10;
            int buttonWidth = font.width(ConstantComponents.Tasks.SUBMIT);
            boolean buttonHovered = mouseX > x + width - 2 - buttonWidth && mouseX < x + width - 2 && mouseY > buttonY && mouseY < buttonY + font.lineHeight;

            Component text = buttonHovered ? ConstantComponents.Tasks.SUBMIT.copy().withStyle(ChatFormatting.UNDERLINE) : ConstantComponents.Tasks.SUBMIT;
            graphics.drawString(font, text, x + width - 2 - buttonWidth, buttonY, progress.isComplete() ? 0xFF707070 : 0xFFD0D0D0, false);
            CursorUtils.setCursor(buttonHovered, progress.isComplete() ? CursorScreen.Cursor.DISABLED : CursorScreen.Cursor.POINTER);
            if (buttonHovered) {
                ScreenUtils.setTooltip(Component.translatable("task.heracles.item.submit.button.tooltip", this.task.target()));
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        if (task.collectionType() == GatherItemTask.CollectionType.MANUAL && !progress.isComplete()) {
            Font font = Minecraft.getInstance().font;
            int buttonY = getHeight(width) - 19;
            boolean buttonHovered = mouseX > width - 2 - font.width(ConstantComponents.Tasks.SUBMIT) && mouseX < width - 2 && mouseY > buttonY && mouseY < buttonY + font.lineHeight;
            if (buttonHovered) {
                NetworkHandler.CHANNEL.sendToServer(new ManualItemTaskPacket(this.quest, this.task.id()));

                if (Minecraft.getInstance().player != null) {
                    progress.addProgress(GatherItemTask.TYPE, task, Pair.of(Optional.empty(), Minecraft.getInstance().player.getInventory()));
                }
                return true;
            }
        }
        return false;
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

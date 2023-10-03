package earth.terrarium.heracles.api.tasks.client.defaults;

import com.mojang.datafixers.util.Pair;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import com.teamresourceful.resourcefullib.client.screens.CursorScreen;
import com.teamresourceful.resourcefullib.client.utils.CursorUtils;
import com.teamresourceful.resourcefullib.client.utils.ScreenUtils;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.tasks.CollectionType;
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

    private static final String TITLE_ITEM = "task.heracles.item.title.item";
    private static final String TITLE_TAG = "task.heracles.item.title.tag";
    private static final String TITLE_SUBMIT_ITEM = "task.heracles.item.submit.title.item";
    private static final String TITLE_SUBMIT_TAG = "task.heracles.item.submit.title.tag";    
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
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        WidgetUtils.drawBackground(graphics, x, y, width, getHeight(width));
        int iconSize = 32;
        ItemStack item = this.getCurrentItem();
        WidgetUtils.drawItemIconWithTooltip(graphics, item, x, y, iconSize, mouseX, mouseY);
        graphics.fill(x + iconSize + 9, y + 5, x + iconSize + 10, y + getHeight(width) - 5, 0xFF909090);
        String title = chooseGatherKey(task, TITLE_ITEM, TITLE_TAG, TITLE_SUBMIT_ITEM, TITLE_SUBMIT_TAG);
        String desc = chooseGatherKey(task, DESC_ITEM, DESC_TAG, DESC_SUBMIT_ITEM, DESC_SUBMIT_TAG);
        graphics.drawString(
            font,
            Component.translatable(title, task.item().getDisplayName(Item::getDescription)), x + iconSize + 16, y + 5, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            Component.translatable(desc, this.task.target(), task.item().getDisplayName(Item::getDescription)), x + iconSize + 16, y + 7 + font.lineHeight, 0xFF808080,
            false
        );
        WidgetUtils.drawProgressText(graphics, x, y, width, this.task, this.progress);
        int height = getHeight(width);
        WidgetUtils.drawProgressBar(graphics, x + iconSize + 16, y + height - font.lineHeight - 6, x + width - 5, y + height - 5, this.task, this.progress);
        if (task.collectionType() == CollectionType.MANUAL) {
            int buttonY = y + height - font.lineHeight - 17;
            int buttonWidth = font.width(ConstantComponents.Tasks.SUBMIT);
            boolean buttonHovered = mouseX > x + width - 5 - buttonWidth && mouseX < x + width - 5 && mouseY > buttonY && mouseY < buttonY + font.lineHeight;

            Component text = buttonHovered ? ConstantComponents.Tasks.SUBMIT.copy().withStyle(ChatFormatting.UNDERLINE) : ConstantComponents.Tasks.SUBMIT;
            graphics.drawString(font, text, x + width - 5 - buttonWidth, buttonY, progress.isComplete() ? 0xFF707070 : 0xFFD0D0D0, false);
            CursorUtils.setCursor(buttonHovered, progress.isComplete() ? CursorScreen.Cursor.DISABLED : CursorScreen.Cursor.POINTER);
            if (buttonHovered) {
                ScreenUtils.setTooltip(Component.translatable("task.heracles.item.submit.button.tooltip", this.task.target()));
            }
        }
    }

    private static String chooseGatherKey(GatherItemTask task, String item, String tag, String submitItem, String submitTag) {
        if (task.collectionType() == CollectionType.AUTOMATIC) {
            if (task.item().isTag()) {
                return tag;
            } else {
                return item;
            }
        }
        else {
            if (task.item().isTag()) {
                return submitTag;
            } else {
                return submitItem;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton, int width) {
        if (task.collectionType() == CollectionType.MANUAL && !progress.isComplete()) {
            Font font = Minecraft.getInstance().font;
            int buttonY = getHeight(width) - font.lineHeight - 17;
            int buttonWidth = font.width(ConstantComponents.Tasks.SUBMIT);
            boolean buttonHovered = mouseX > width - 5 - buttonWidth && mouseX < width - 5 && mouseY > buttonY && mouseY < buttonY + font.lineHeight;
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
        return 42;
    }
}

package earth.terrarium.heracles.api.tasks.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.tasks.QuestTaskDisplayFormatter;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter;
import earth.terrarium.heracles.api.tasks.defaults.BiomeTask;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.ByteTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class BiomeTaskWidget implements DisplayWidget {

    private static final Component DESCRIPTION = Component.translatable("task.heracles.biome.desc.singular");

    private final BiomeTask task;
    private final TaskProgress<ByteTag> progress;

    public BiomeTaskWidget(BiomeTask task, TaskProgress<ByteTag> progress) {
        this.task = task;
        this.progress = progress;
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        WidgetUtils.drawBackground(graphics, x, y, width, getHeight(width));
        int iconSize = 32;
        this.task.icon().renderOrStack(getIcon(), graphics, scissor, x, y, iconSize);
        graphics.fill(x + iconSize + 9, y + 5, x + iconSize + 10, y + getHeight(width) - 5, 0xFF909090);
        graphics.drawString(
            font,
            task.titleOr(DESCRIPTION), x + iconSize + 16, y + 6, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            TaskTitleFormatter.create(this.task), x + iconSize + 16, y + 8 + font.lineHeight, 0xFF808080,
            false
        );
        String progress = QuestTaskDisplayFormatter.create(this.task, this.progress);
        graphics.drawString(
            font,
            progress, x + width - 5 - font.width(progress), y + 6, 0xFFFFFFFF,
            false
        );

        int height = getHeight(width);
        WidgetUtils.drawProgressBar(graphics, x + iconSize + 16, y + height - font.lineHeight - 5, x + width - 5, y + height - 6, this.task, this.progress);
    }

    private static final Item[] ICONS = {
        Items.SPRUCE_SAPLING, Items.BIRCH_SAPLING, Items.JUNGLE_SAPLING, Items.ACACIA_SAPLING, Items.DARK_OAK_SAPLING, Items.OAK_SAPLING, Items.CHERRY_SAPLING,
        Items.MOSS_BLOCK, Items.SNOW_BLOCK, Items.CACTUS, Items.GRASS_BLOCK, Items.FIRE_CORAL, Items.PODZOL, Items.SAND, Items.SANDSTONE, Items.RED_SAND,
    };

    private static ItemStack getIcon() {
        int value = (int) (System.currentTimeMillis() / 1000L);
        return ICONS[value % ICONS.length].getDefaultInstance();
    }

    @Override
    public int getHeight(int width) {
        return 42;
    }

}

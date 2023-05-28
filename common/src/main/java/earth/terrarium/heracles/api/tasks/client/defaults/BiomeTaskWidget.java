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
        WidgetUtils.drawBackground(graphics, x, y, width);
        int iconSize = (int) (width * 0.1f);
        graphics.renderFakeItem(getIcon(), x + 5 + (int) (iconSize / 2f) - 8, y + 5 + (int) (iconSize / 2f) - 8);
        graphics.drawString(
            font,
            TaskTitleFormatter.create(this.task), x + iconSize + 10, y + 5, 0xFFFFFFFF,
            false
        );
        graphics.drawString(
            font,
            DESCRIPTION, x + iconSize + 10, y + 7 + font.lineHeight, 0xFF808080,
            false
        );
        String progress = QuestTaskDisplayFormatter.create(this.task, this.progress);
        graphics.drawString(
            font,
            progress, x + width - 5 - font.width(progress), y + 5, 0xFFFFFFFF,
            false
        );

        int height = getHeight(width);
        WidgetUtils.drawProgressBar(graphics, x + iconSize + 10, y + height - font.lineHeight + 2, x + width - 5, y + height - 2, this.task, this.progress);
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
        return (int) (width * 0.1f) + 10;
    }

}

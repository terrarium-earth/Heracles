package earth.terrarium.heracles.api.tasks.client.defaults;

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter;
import earth.terrarium.heracles.api.tasks.defaults.ChangedDimensionTask;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.NumericTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;

public final class DimensionTaskWidget implements DisplayWidget {

    private final ChangedDimensionTask task;
    private final TaskProgress<NumericTag> progress;

    public DimensionTaskWidget(ChangedDimensionTask task, TaskProgress<NumericTag> progress) {
        this.task = task;
        this.progress = progress;
    }

    @Override
    public void render(GuiGraphics graphics, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        int iconSize = 32;
        this.task.icon().renderOrStack(getIcon(), graphics, x + 5, y + 5, iconSize);
        graphics.drawString(
            font,
            task.titleOr(TaskTitleFormatter.create(this.task)), x + iconSize + 16, y + 6, QuestScreenTheme.getTaskTitle(),
            false
        );
        graphics.drawString(
            font,
            getDescription(), x + iconSize + 16, y + 8 + font.lineHeight, QuestScreenTheme.getTaskDescription(),
            false
        );
        WidgetUtils.drawProgressText(graphics, x, y, width, this.task, this.progress);

        int height = getHeight(width);
        WidgetUtils.drawProgressBar(graphics, x + iconSize + 16, y + height - font.lineHeight - 5, x + width - 5, y + height - 6, this.task, this.progress);
    }

    private Component getDescription() {
        String fromTranslation = Optionull.mapOrDefault(Optionull.map(this.task.from(), ResourceKey::location),
            id -> Util.makeDescriptionId("dimension", id), "task.heracles.changed_dimension.desc.dim.any");
        String toTranslation = Optionull.mapOrDefault(Optionull.map(this.task.to(), ResourceKey::location),
            id -> Util.makeDescriptionId("dimension", id), "task.heracles.changed_dimension.desc.dim.any");

        return Component.translatable(
            "task.heracles.changed_dimension.desc.singular",
            Component.translatableWithFallback(fromTranslation, Objects.toString(Optionull.map(this.task.from(), ResourceKey::location))),
            Component.translatableWithFallback(toTranslation, Objects.toString(Optionull.map(this.task.to(), ResourceKey::location)))
        );
    }

    private static final Item[] ICONS = {
        Items.END_PORTAL_FRAME, Items.GRASS_BLOCK, Items.NETHERRACK, Items.END_STONE
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

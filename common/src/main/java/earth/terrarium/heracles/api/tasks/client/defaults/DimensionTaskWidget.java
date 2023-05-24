package earth.terrarium.heracles.api.tasks.client.defaults;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack;
import earth.terrarium.heracles.api.client.DisplayWidget;
import earth.terrarium.heracles.api.client.WidgetUtils;
import earth.terrarium.heracles.api.tasks.QuestTaskDisplayFormatter;
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter;
import earth.terrarium.heracles.api.tasks.defaults.ChangedDimensionTask;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.nbt.ByteTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;

public final class DimensionTaskWidget implements DisplayWidget {

    private final ChangedDimensionTask task;
    private final TaskProgress<ByteTag> progress;

    public DimensionTaskWidget(ChangedDimensionTask task, TaskProgress<ByteTag> progress) {
        this.task = task;
        this.progress = progress;
    }

    @Override
    public void render(PoseStack pose, ScissorBoxStack scissor, int x, int y, int width, int mouseX, int mouseY, boolean hovered, float partialTicks) {
        Font font = Minecraft.getInstance().font;
        WidgetUtils.drawBackground(pose, x, y, width);
        int iconSize = (int) (width * 0.1f);
        Minecraft.getInstance().getItemRenderer().renderGuiItem(pose, getIcon(), x + 5 + (int) (iconSize / 2f) - 8, y + 5 + (int) (iconSize / 2f) - 8);
        font.draw(pose, TaskTitleFormatter.create(this.task), x + iconSize + 10, y + 5, 0xFFFFFFFF);
        font.draw(pose, getDescription(), x + iconSize + 10, y + 7 + font.lineHeight, 0xFF808080);
        String progress = QuestTaskDisplayFormatter.create(this.task, this.progress);
        font.draw(pose, progress, x + width - 5 - font.width(progress), y + 5, 0xFFFFFFFF);

        int height = getHeight(width);
        WidgetUtils.drawProgressBar(pose, x + iconSize + 10, y + height - font.lineHeight + 2, x + width - 5, y + height - 2, this.task, this.progress);
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
        return (int) (width * 0.1f) + 10;
    }

}
